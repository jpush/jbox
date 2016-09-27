package com.jiguang.jbox.channel;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.Message;
import com.jiguang.jbox.data.source.ChannelDataSource;
import com.jiguang.jbox.data.source.ChannelRepository;
import com.jiguang.jbox.data.source.DeveloperDataSource;
import com.jiguang.jbox.data.source.DeveloperRepository;
import com.jiguang.jbox.data.source.local.ChannelLocalDataSource;
import com.jiguang.jbox.data.source.local.DeveloperLocalDataSource;
import com.jiguang.jbox.data.source.remote.DeveloperRemoteDataSource;
import com.jiguang.jbox.util.HttpUtil;
import com.jiguang.jbox.util.LogUtil;
import com.jiguang.jbox.util.ViewHolder;
import com.jiguang.jbox.view.TopBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 扫描二维码后的 channel 展示界面。
 */
public class ChannelActivity extends Activity {
    private final String TAG = "ChannelActivity";

    public static final String EXTRA_DEV_KEY = "dev_key";

    private static final int MSG_DEV_UPDATE = 0;

    private ListView mListView;

    private SubChannelListAdapter mListAdapter;

    private ChannelRepository mChannelRepository;

    private List<Channel> mChannels = new ArrayList<>();

    private Set<String> mTags = new HashSet<>();  // 订阅 Channel 的 tag

    private Handler mHandler;

    // head view.
    private TextView mTvDevName;
    private TextView mTvDevDesc;
    private CircleImageView mIvAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        final String devKey = getIntent().getStringExtra(EXTRA_DEV_KEY);
        LogUtil.LOGI(TAG, "devKey: " + devKey);

        TopBar topBar = (TopBar) findViewById(R.id.topBar);
        topBar.setLeftClick(new View.OnClickListener() {    // 顶部栏返回事件。
            @Override
            public void onClick(View view) {
                onBack();
            }
        });

        mListView = (ListView) findViewById(R.id.lv_channel);

        mHandler = new MyHandler();

        DeveloperLocalDataSource devLocalDataSource = DeveloperLocalDataSource.getInstance(this);
        DeveloperRemoteDataSource devRemoteDataSource = DeveloperRemoteDataSource.getInstance(this);

        DeveloperRepository devRepository = DeveloperRepository.getInstance(devLocalDataSource,
                devRemoteDataSource);

        // Init head view.
        final View headView = getLayoutInflater().inflate(
                R.layout.view_subscribe_channel, null, false);
        mIvAvatar = (CircleImageView) headView.findViewById(R.id.iv_dev_icon);
        mTvDevName = (TextView) headView.findViewById(R.id.tv_name);
        mTvDevDesc = (TextView) headView.findViewById(R.id.tv_desc);

        // 初始化开发者信息。
        devRemoteDataSource.getDeveloper(devKey, new DeveloperDataSource.LoadDevCallback() {
            @Override
            public void onDevLoaded(Developer dev) {
                android.os.Message msg = new android.os.Message();
                msg.what = MSG_DEV_UPDATE;
                Bundle bundle = new Bundle();
                bundle.putString("devName", dev.getDevName());
                bundle.putString("desc", dev.getDesc());
                bundle.putString("avatarPath", dev.getAvatarPath());
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }

            @Override
            public void onDataNotAvailable() {
                Toast.makeText(getApplicationContext(), "获取 developer 信息出错。",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        final OnChannelCheckedListener channelCheckedListener = new OnChannelCheckedListener() {
            @Override
            public void onChannelChecked(int position, boolean isChecked) {
                mChannels.get(position).setSubscribe(isChecked);
            }
        };

//       服务器端的 Channel 列表,要和本地数据库中的做对比。
        final List<String> remoteChannels = HttpUtil.getInstance(this).requestChannels(devKey);

        ChannelLocalDataSource channelLocalDataSource = ChannelLocalDataSource.getInstance(this);
        mChannelRepository = ChannelRepository.getInstance(channelLocalDataSource);

        mChannelRepository.getChannels(devKey, new ChannelDataSource.LoadChannelsCallback() {
            @Override
            public void onChannelsLoaded(List<Channel> channels) {
                if (remoteChannels != null) {
                    // 从本地获取的 Channel, 将订阅状态配置到服务器获取的 Channel。
                    for (int i = 0; i < remoteChannels.size(); i++) {
                        String channelName = remoteChannels.get(i);
                        if (remoteChannels.contains(channelName)) {
                            mChannels.add(channels.get(i));
                        } else {
                            Channel channel = new Channel(channelName);
                            channel.setDevKey(devKey);
                            mChannels.add(channel);
                        }
                    }
                } else {
                    // 如果为 null,代表网络请求错误。
                    Toast.makeText(getApplicationContext(), "网络请求错误", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDataNotAvailable() {
                // 如果本地还没有数据,就直接使用从服务器获取的数据。
                mChannels = new ArrayList<>();
                for (String channelName : remoteChannels) {
                    mChannels.add(new Channel(channelName));
                }
            }
        });

        mListView.addHeaderView(headView);
        mListView.setEmptyView(findViewById(R.id.tv_hint));
        mListAdapter = new SubChannelListAdapter(mChannels, channelCheckedListener);
        mListView.setAdapter(mListAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBack();   // 当点击回退键,保存数据,并打上 tag。
    }

    /**
     * 将数据保存到数据库中，并打上 JPush TAG。
     */
    private void onBack() {
        for (Channel c : mChannels) {
            if (c.isSubscribe()) {
                mTags.add(c.getDevKey() + "_" + c.getName());
            }
        }

        if (!mTags.isEmpty()) {
            JPushInterface.setTags(this, mTags, new TagAliasCallback() {
                @Override
                public void gotResult(int result, String desc, Set<String> set) {
                    if (result == 0) {
                        Toast.makeText(getApplicationContext(), "订阅成功", Toast.LENGTH_SHORT).show();
                        // 将数据保存到数据库中。
                        mChannelRepository.saveChannels(mChannels);
                    } else {
                        Toast.makeText(getApplicationContext(), "订阅失败", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            });
        }
    }

    interface OnChannelCheckedListener {

        /**
         * 根据 Channel 的选择状态来判断是否订阅。
         *
         * @param position
         * @param isChecked
         */
        void onChannelChecked(int position, boolean isChecked);
    }

    private static class SubChannelListAdapter extends BaseAdapter {

        private List<Channel> mChannels;

        private OnChannelCheckedListener mChannelCheckedListener;

        public SubChannelListAdapter(List<Channel> channels, OnChannelCheckedListener listener) {
            mChannels = channels;
            mChannelCheckedListener = listener;
        }

        public void replaceData(List<Channel> channels) {
            mChannels = channels;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mChannels.size();
        }

        @Override
        public Channel getItem(int i) {
            return mChannels.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                convertView = inflater.inflate(R.layout.item_subscribe_channel, viewGroup, false);
            }

            Channel channel = getItem(i);

            ImageView ivIcon = ViewHolder.get(convertView, R.id.iv_icon);

            TextView tvChannel = ViewHolder.get(convertView, R.id.tv_channel);
            tvChannel.setText(channel.getName());

            CheckBox cbIsSubscribe = ViewHolder.get(convertView, R.id.cb_isSubscribe);
            cbIsSubscribe.setChecked(channel.isSubscribe());
            cbIsSubscribe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mChannelCheckedListener.onChannelChecked(i, isChecked);
                }
            });

            return convertView;
        }
    }

    private class MyHandler extends Handler {

        public MyHandler() {
            super();
        }

        @Override
        public void dispatchMessage(android.os.Message msg) {
            // 更新 UI
            switch (msg.what) {
                case ChannelActivity.MSG_DEV_UPDATE:    // 更新 head view 数据
                    Bundle data = msg.getData();

                    mTvDevName.setText(data.getString("devName"));

                    if (!TextUtils.isEmpty(data.getString("desc"))) {
                        mTvDevDesc.setText(data.getString("desc"));
                    }

                    if (!TextUtils.isEmpty(data.getString("avatarPath"))) {
                        Bitmap avatarBitmap = BitmapFactory.decodeFile(data.getString("avatarPath"));
                        mIvAvatar.setImageBitmap(avatarBitmap);
                    }
                    break;
                default:
            }
        }
    }
}

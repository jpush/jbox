package com.jiguang.jbox.channel;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.source.ChannelDataSource;
import com.jiguang.jbox.data.source.ChannelRepository;
import com.jiguang.jbox.data.source.DeveloperDataSource;
import com.jiguang.jbox.data.source.DeveloperRepository;
import com.jiguang.jbox.data.source.local.ChannelLocalDataSource;
import com.jiguang.jbox.data.source.local.DeveloperLocalDataSource;
import com.jiguang.jbox.data.source.remote.DeveloperRemoteDataSource;
import com.jiguang.jbox.main.MainActivity;
import com.jiguang.jbox.util.HttpUtil;
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
    private static final int MSG_CHANNEL_UPDATE = 1;

    private ListView mListView;

    private SubChannelListAdapter mListAdapter;

    private ChannelRepository mChannelRepository;

    private List<Channel> mChannels = new ArrayList<>();

    private List<Channel> mLocalChannels;

    private Set<String> mTags = new HashSet<>();  // 订阅 Channel 的 tag

    private Handler mHandler;

    // head view.
    private TextView mTvDevName;
    private TextView mTvDevDesc;
    private CircleImageView mIvAvatar;

    private String mDevName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        final String devKey = getIntent().getStringExtra(EXTRA_DEV_KEY);

        mHandler = new MyHandler();

        TopBar topBar = (TopBar) findViewById(R.id.topBar);
        topBar.setLeftClick(new View.OnClickListener() {    // 顶部栏返回事件。
            @Override
            public void onClick(View view) {
                onBack();
            }
        });

        mListView = (ListView) findViewById(R.id.lv_channel);

        // Init head view.
        View headView = getLayoutInflater().inflate(
                R.layout.view_subscribe_channel, mListView, false);
        mIvAvatar = (CircleImageView) headView.findViewById(R.id.iv_dev_icon);
        mTvDevName = (TextView) headView.findViewById(R.id.tv_name);
        mTvDevDesc = (TextView) headView.findViewById(R.id.tv_desc);
        mListView.addHeaderView(headView);

        mListAdapter = new SubChannelListAdapter(mChannels, new OnChannelCheckedListener() {
            @Override
            public void onChannelChecked(int position, boolean isChecked) {
                mChannels.get(position).setSubscribe(isChecked);
            }
        });
        mListView.setAdapter(mListAdapter);

        View emptyView = findViewById(R.id.tv_hint);
//        mListView.setEmptyView(emptyView);

        // 申请外部存储访问权限。
        if (ContextCompat.checkSelfPermission(AppApplication.getAppContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        DeveloperLocalDataSource devLocalDataSource = DeveloperLocalDataSource.getInstance();
        DeveloperRemoteDataSource devRemoteDataSource = DeveloperRemoteDataSource.getInstance();

        final DeveloperRepository devRepository = DeveloperRepository.getInstance(
                devLocalDataSource, devRemoteDataSource);

        // 初始化开发者信息。
        devRemoteDataSource.getDeveloper(devKey, new DeveloperDataSource.LoadDevCallback() {
            @Override
            public void onDevLoaded(Developer dev) {
                mDevName = dev.getDevName();

//                devRepository.saveDeveloper(dev);

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

        // 初始化 Channel 列表数据。
        ChannelLocalDataSource channelLocalDataSource = ChannelLocalDataSource.getInstance();
        mChannelRepository = ChannelRepository.getInstance(channelLocalDataSource);
//
//        mChannelRepository.getChannels(devKey, new ChannelDataSource.LoadChannelsCallback() {
//            @Override
//            public void onChannelsLoaded(List<Channel> channels) {
//                mLocalChannels = channels;
//            }
//
//            @Override
//            public void onDataNotAvailable() {
//                // 本地没有数据。
//                mLocalChannels = new ArrayList<>();
//            }
//        });

        // 服务器端的 Channel 列表,要和本地数据库中的做对比。
        HttpUtil.getInstance().requestChannels(devKey,
                new ChannelDataSource.LoadChannelsNameCallback() {
                    @Override
                    public void onChannelsNameLoaded(List<String> channels) {
                        if (mLocalChannels == null || mLocalChannels.isEmpty()) {
                            // 直接用服务器数据。
                            for (String name : channels) {
                                Channel channel = new Channel(name);
                                channel.setDevKey(devKey);
                                mChannels.add(channel);
                            }
                        } else {
                            // 将本地数据和服务器数据做对比。
                            for (String name : channels) {
                                Channel channel = new Channel(name);
                                channel.setDevKey(devKey);

                                for (Channel c : mLocalChannels) {
                                    if (name.equals(c.getName()) && c.isSubscribe()) {
                                        channel.setSubscribe(true);
                                    }
                                }

                                mChannels.add(channel);
                            }
                        }
                        mListAdapter.replaceData(mChannels);
                        mChannelRepository.saveChannels(mChannels); // 更新本地数据库
                    }

                    @Override
                    public void onDataNotAvailable() {
                        Toast.makeText(getApplicationContext(), "网络请求错误", Toast.LENGTH_SHORT).show();
                    }
                });

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
                }
            });
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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

        SubChannelListAdapter(List<Channel> channels, OnChannelCheckedListener listener) {
            mChannels = channels;
            mChannelCheckedListener = listener;
        }

        void replaceData(List<Channel> channels) {
            mChannels = channels;
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
            String name = channel.getName();

            TextView tvHead = ViewHolder.get(convertView, R.id.tv_head);
            tvHead.setText(name.substring(0, 1).toUpperCase());

            TextView tvChannel = ViewHolder.get(convertView, R.id.tv_channel);
            tvChannel.setText(name);

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

    // TODO: 改进内存泄漏风险。
    private class MyHandler extends Handler {

        MyHandler() {
            super();
        }

        @Override
        public void dispatchMessage(android.os.Message msg) {
            // 更新 UI
            switch (msg.what) {
                case ChannelActivity.MSG_DEV_UPDATE:    // 更新 head view 数据
                    Bundle data = msg.getData();

                    mTvDevName.setText(data.getString("devName"));

                    if (!TextUtils.isEmpty(data.getString("avatarPath"))) {
                        Bitmap bitmap = BitmapFactory.decodeFile(data.getString("avatarPath"));
                        bitmap = Bitmap.createScaledBitmap(bitmap, mIvAvatar.getWidth(),
                                mIvAvatar.getHeight(), true);
                        mIvAvatar.setImageBitmap(bitmap);
                    }

                    if (!TextUtils.isEmpty(data.getString("desc"))) {
                        mTvDevDesc.setText(data.getString("desc"));
                    }

                    break;
                default:
            }
        }
    }
}

package com.jiguang.jbox.channel;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
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

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.source.ChannelDataSource;
import com.jiguang.jbox.data.source.DeveloperDataSource;
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

    private SubChannelListAdapter mListAdapter;

    private List<Channel> mChannels = new ArrayList<>();

    private List<Channel> mLocalChannels;

    private Set<String> mTags = new HashSet<>();  // 订阅 Channel 的 tag

    private Handler mHandler;

    // head view.
    private TextView mTvDevName;
    private TextView mTvDevDesc;
    private CircleImageView mIvAvatar;

    private String mDevKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        mDevKey = getIntent().getStringExtra(EXTRA_DEV_KEY);
        AppApplication.currentDevKey = mDevKey;

        mHandler = new MyHandler();

        TopBar topBar = (TopBar) findViewById(R.id.topBar);
        topBar.setLeftClick(new View.OnClickListener() {    // 顶部栏返回事件。
            @Override
            public void onClick(View view) {
                onBack();
            }
        });

        ListView listView = (ListView) findViewById(R.id.lv_channel);

        // Init head view.
        View headView = getLayoutInflater().inflate(
                R.layout.view_subscribe_channel, listView, false);
        mIvAvatar = (CircleImageView) headView.findViewById(R.id.iv_dev_icon);
        mTvDevName = (TextView) headView.findViewById(R.id.tv_name);
        mTvDevDesc = (TextView) headView.findViewById(R.id.tv_desc);
        listView.addHeaderView(headView);

        mListAdapter = new SubChannelListAdapter(mChannels, new OnChannelCheckedListener() {
            @Override
            public void onChannelChecked(int position, boolean isChecked) {
                mChannels.get(position).isSubscribe = isChecked;
            }
        });
        listView.setAdapter(mListAdapter);

        // 初始化开发者信息。
        HttpUtil.getInstance().requestDeveloper(mDevKey, new DeveloperDataSource.LoadDevCallback() {
            @Override
            public void onDevLoaded(Developer dev) {
                android.os.Message msg = new android.os.Message();
                msg.what = MSG_DEV_UPDATE;
                Bundle bundle = new Bundle();
                bundle.putString("devName", dev.name);
                bundle.putString("desc", dev.desc);
                bundle.putString("avatarUrl", dev.avatarUrl);
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

        // 从本地数据库中进行查询。
        mLocalChannels = new Select().from(Channel.class).where("DevKey = ?", mDevKey).execute();

        if (!HttpUtil.isNetworkAvailable() && mLocalChannels != null) {
            mListAdapter.replaceData(mLocalChannels);
        } else {
            // 服务器端的 Channel 列表,要和本地数据库中的做对比。
            HttpUtil.getInstance().requestChannels(mDevKey,
                    new ChannelDataSource.LoadChannelsCallback() {
                        @Override
                        public void onChannelsLoaded(List<Channel> channels) {
                            mChannels = channels;

                            if (mLocalChannels == null) {
                                mListAdapter.replaceData(mChannels);
                                return;
                            }

                            // 将本地数据和服务器数据做对比，同步订阅状态。
                            for (Channel channel : mChannels) {
                                for (Channel localChannel : mLocalChannels) {
                                    if (localChannel.name.equals(channel.name)) {
                                        channel.isSubscribe = localChannel.isSubscribe;
                                    }
                                }
                            }
                            mListAdapter.replaceData(channels);
                        }

                        @Override
                        public void onDataNotAvailable() {
                            Toast.makeText(getApplicationContext(), "网络请求错误", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        onBack();   // 当点击回退键,保存数据,并打上 tag。
        super.onBackPressed();
    }

    /**
     * 将数据保存到数据库中，并打上 JPush TAG。
     */
    private void onBack() {
        if (!mChannels.equals(mLocalChannels)) {
            new Delete().from(Channel.class).where("DevKey = ?", mDevKey).execute();

            for (Channel c : mChannels) {
                if (c.isSubscribe) {
                    AppApplication.currentChannelName = c.name;
                    mTags.add(c.devKey + "_" + c.name);
                }
                c.save();
            }

            if (!mTags.isEmpty()) {
                JPushInterface.setTags(this, mTags, new TagAliasCallback() {
                    @Override
                    public void gotResult(int result, String desc, Set<String> set) {
                        if (result == 0) {
                            Toast.makeText(getApplicationContext(), "订阅成功", Toast.LENGTH_SHORT).show();

                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "订阅失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                AppApplication.shouldUpdateData = true;
            }
        }
    }

    interface OnChannelCheckedListener {

        /**
         * 根据 Channel 的选择状态来判断是否订阅。
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
//            notifyDataSetChanged();
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
            String name = channel.name;

            TextView tvHead = ViewHolder.get(convertView, R.id.tv_head);
            tvHead.setText(name.substring(0, 1).toUpperCase());

            TextView tvChannel = ViewHolder.get(convertView, R.id.tv_channel);
            tvChannel.setText(name);

            CheckBox cbIsSubscribe = ViewHolder.get(convertView, R.id.cb_isSubscribe);
            cbIsSubscribe.setChecked(channel.isSubscribe);
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

                    if (!TextUtils.isEmpty(data.getString("avatarUrl"))) {
                        Glide.with(AppApplication.getAppContext())
                                .load(data.getString("avatarUrl"))
                                .dontAnimate()
                                .into(mIvAvatar);
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

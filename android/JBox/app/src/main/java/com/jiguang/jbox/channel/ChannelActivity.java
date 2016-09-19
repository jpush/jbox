package com.jiguang.jbox.channel;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.jiguang.jbox.data.source.ChannelDataSource;
import com.jiguang.jbox.data.source.ChannelRepository;
import com.jiguang.jbox.data.source.DeveloperDataSource;
import com.jiguang.jbox.data.source.DeveloperRepository;
import com.jiguang.jbox.data.source.local.ChannelLocalDataSource;
import com.jiguang.jbox.data.source.local.DeveloperLocalDataSource;
import com.jiguang.jbox.data.source.remote.DeveloperRemoteDataSource;
import com.jiguang.jbox.util.ViewHolder;

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

    public static final String EXTRA_DEV_KEY = "dev_key";

    private ListView mListView;

    private SubChannelListAdapter mListAdapter;

    private ChannelRepository mChannelRepository;

    private List<Channel> mChannels;

    private Set<String> mTags;  // 订阅 Channel 的 tag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        String devKey = getIntent().getStringExtra(EXTRA_DEV_KEY);

        mListView = (ListView) findViewById(R.id.lv_channel);

        DeveloperLocalDataSource devLocalDataSource = DeveloperLocalDataSource.getInstance(this);
        DeveloperRemoteDataSource devRemoteDataSource = DeveloperRemoteDataSource.getInstance();

        DeveloperRepository devRepository = DeveloperRepository.getInstance(devLocalDataSource,
                devRemoteDataSource);

        final View headView = getLayoutInflater().inflate(
                R.layout.view_subscribe_channel, mListView, false);

        devRepository.getDeveloper(devKey, new DeveloperDataSource.LoadDevCallback() {
            @Override
            public void onDevLoaded(Developer dev) {
                if (!TextUtils.isEmpty(dev.getAvatarPath())) {
                    CircleImageView ivAvatar = (CircleImageView) headView.findViewById(R.id.iv_dev_icon);
                    Bitmap avatarBitmap = BitmapFactory.decodeFile(dev.getAvatarPath());
                    ivAvatar.setImageBitmap(avatarBitmap);
                }

                TextView tvDevName = (TextView) headView.findViewById(R.id.tv_devname);
                tvDevName.setText(dev.getDevName());

                TextView tvDevDesc = (TextView) headView.findViewById(R.id.tv_description);
                tvDevDesc.setText(dev.getDesc());
            }

            @Override
            public void onDataNotAvailable() {
                Toast.makeText(getApplicationContext(), "获取 developer 信息出错。",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mListView.addHeaderView(headView);

        // 获取 channel 信息。
        ChannelLocalDataSource channelLocalDataSource = ChannelLocalDataSource.getInstance(this);
        mChannelRepository = ChannelRepository.getInstance(channelLocalDataSource);

        final OnChannelCheckedListener channelCheckedListener = new OnChannelCheckedListener() {
            @Override
            public void onChannelChecked(int position, boolean isChecked) {
                mChannels.get(position).setSubscribe(isChecked);
            }
        };

        mChannelRepository.getChannels(devKey, new ChannelDataSource.LoadChannelsCallback() {
            @Override
            public void onChannelsLoaded(List<Channel> channels) {
                for (Channel channel : channels) {
                    if (channel.isSubscribe()) {
                        mTags.add(channel.getDevKey() + "_" + channel.getName());
                    }
                }
                mListAdapter = new SubChannelListAdapter(channels, channelCheckedListener);
                mListView.setAdapter(mListAdapter);
            }

            @Override
            public void onDataNotAvailable() {

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

        mTags = new HashSet<>();
        for (Channel c : mChannels) {
           if (c.isSubscribe()) {
               mTags.add(c.getDevKey() + "_" + c.getName());
           }
        }

        // TODO: 将数据保存到数据库中，并打上 TAG。
        if (!mTags.isEmpty()) {
            JPushInterface.setTags(this, mTags, new TagAliasCallback() {
                @Override
                public void gotResult(int result, String desc, Set<String> set) {
                    if (result == 0) {
                        Toast.makeText(getApplicationContext(), "订阅成功", Toast.LENGTH_SHORT).show();
                        // TODO：将数据保存到数据库中。

                    } else {
                        Toast.makeText(getApplicationContext(), "订阅失败", Toast.LENGTH_SHORT).show();
                    }
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
}

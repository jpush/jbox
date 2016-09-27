package com.jiguang.jbox.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.Message;
import com.jiguang.jbox.data.source.ChannelDataSource;
import com.jiguang.jbox.data.source.ChannelRepository;
import com.jiguang.jbox.data.source.MessageDataSource;
import com.jiguang.jbox.data.source.MessageRepository;
import com.jiguang.jbox.data.source.local.ChannelLocalDataSource;
import com.jiguang.jbox.data.source.local.MessagesLocalDataSource;
import com.jiguang.jbox.util.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private Toolbar mTopBar;

    private ListView mMsgListView;

    private MessageListAdapter mAdapter;

    private MessageRepository mMessagesRepository;

    private List<Developer> mDevList;

    private List<Channel> mChannelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // init data.
        ChannelLocalDataSource channelLocalDataSource = ChannelLocalDataSource.getInstance(this);
        ChannelRepository channelRepository = ChannelRepository.getInstance(channelLocalDataSource);
        channelRepository.getChannels(true, new ChannelDataSource.LoadChannelsCallback() {
            @Override
            public void onChannelsLoaded(List<Channel> channels) {
                mChannelList = channels;
                navigationDrawerFragment.initData(channels);
                if (channels != null && !channels.isEmpty()) {
                    // 初始化侧边栏 Channel 列表数据。
                    navigationDrawerFragment.initData(channels);
                }
            }

            @Override
            public void onDataNotAvailable() {
                mChannelList = new ArrayList<>();
            }
        });

        MessagesLocalDataSource msgLocalDataSource = MessagesLocalDataSource.getInstance(this);
        mMessagesRepository = MessageRepository.getInstance(msgLocalDataSource);

        mTopBar = (Toolbar) findViewById(R.id.toolbar);
        mTopBar.setNavigationIcon(R.drawable.ic_navigation);

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mTopBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        mMsgListView = (ListView) findViewById(R.id.lv_msg);
        mAdapter = new MessageListAdapter(new ArrayList<Message>(0));
        mMsgListView.setAdapter(mAdapter);

        View emptyView = findViewById(R.id.tv_hint);
        mMsgListView.setEmptyView(emptyView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (mChannelList != null) {
            Channel channel = mChannelList.get(position);
            mTopBar.setTitle(channel.getName());
            // 加载指定 Channel 的 message 数据。
            mMessagesRepository.getMessages(channel.getDevKey(), channel.getName(),
                    new MessageDataSource.LoadMessagesCallback() {
                        @Override
                        public void onMessagesLoaded(List<Message> messages) {
                            mAdapter.replaceData(messages);
                        }

                        @Override
                        public void onDataNotAvailable() {

                        }
                    });
        }
    }


    private static class MessageListAdapter extends BaseAdapter {

        private List<Message> mMessages;

        public MessageListAdapter(List<Message> list) {
            mMessages = list;
        }

        public void replaceData(List<Message> list) {
            if (list != null && !list.isEmpty()) {
                mMessages = list;
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return mMessages.size();
        }

        @Override
        public Object getItem(int position) {
            return mMessages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.item_msg, parent, false);
            }

            final Message msg = mMessages.get(position);

            ImageView ivIcon = ViewHolder.get(convertView, R.id.iv_icon);

            TextView tvTitle = ViewHolder.get(convertView, R.id.tv_title);
            tvTitle.setText(msg.getTitle());

            TextView tvContent = ViewHolder.get(convertView, R.id.tv_content);
            tvContent.setText(msg.getContent());

            TextView tvTime = ViewHolder.get(convertView, R.id.tv_time);

            long timeMillis = Long.parseLong(msg.getTime());
            String formatTime = DateUtils.formatDateTime(parent.getContext(), timeMillis,
                    DateUtils.FORMAT_SHOW_TIME);
            tvTime.setText(formatTime);

            return convertView;
        }
    }

}

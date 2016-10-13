package com.jiguang.jbox.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
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

    private List<Developer> mDevList;

    private String mCurrentDevKey;

    private List<Channel> mChannelList;

    private String mCurrentChannelName;

    private List<Message> mMessages;

    private NavigationDrawerFragment mDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(
                R.id.navigation_drawer);

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

//        onNavigationDrawerItemSelected(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);

        initData();
        mDrawerFragment.initData(mChannelList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    /**
     * 侧边栏 Channel 点击事件。
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (mChannelList != null) {
            Channel channel = mChannelList.get(position);

            mCurrentChannelName = channel.name;
            mCurrentDevKey = channel.devKey;

            mTopBar.setTitle(channel.name);

            mMessages = new Select().from(Message.class)
                    .where("DevKey = ? AND Channel = ?", channel.devKey, channel.name)
                    .execute();

            mAdapter.replaceData(mMessages);
        }
    }

    private void initData() {
        mDevList = new Select().from(Developer.class).execute();

        if (mDevList != null && !mDevList.isEmpty()) {
            String devKey = mDevList.get(0).key;

            mChannelList = new Select().from(Channel.class)
                    .where("DevKey = ? AND IsSubscribe = ?", devKey, true)
                    .execute();
        }
    }


    private static class MessageListAdapter extends BaseAdapter {

        private List<Message> mMessages;

        MessageListAdapter(List<Message> list) {
            mMessages = list;
        }

        void replaceData(List<Message> list) {
            if (list != null && !list.isEmpty()) {
                mMessages = list;
            }
        }

        void addMessage(Message msg) {
            if (mMessages != null) {
                mMessages.add(0, msg);
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
            tvTitle.setText(msg.title);

            TextView tvContent = ViewHolder.get(convertView, R.id.tv_content);
            tvContent.setText(msg.content);

            long timeMillis = msg.time;
            String formatTime = DateUtils.formatDateTime(parent.getContext(), timeMillis,
                    DateUtils.FORMAT_SHOW_TIME);

            TextView tvTime = ViewHolder.get(convertView, R.id.tv_time);
            tvTime.setText(formatTime);

            return convertView;
        }
    }


    /**
     * 收到消息的监听器。
     */
    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {
                Bundle bundle = intent.getBundleExtra(JPushInterface.EXTRA_MESSAGE);

                String title = bundle.getString("title");
                String content = bundle.getString("content");
                String devKey = bundle.getString("dev_key");
                String channel = bundle.getString("channel");

                // 保存 msg 到本地，并刷新页面数据。
                Message msg = new Message();
                msg.devKey = devKey;
                msg.title = title;
                msg.content = content;
                msg.channelName = channel;
                msg.time = System.currentTimeMillis();
                msg.save();

                // 如果收到的是当前 Channel 的消息就更新界面，否则。
                if (mCurrentDevKey.equals(devKey) && mCurrentChannelName.equals(channel)) {
                    mAdapter.addMessage(msg);
                } else {
                    Channel c = new Select().from(Channel.class)
                            .where("DevKey = ? AND ChannelName = ?", devKey, channel)
                            .executeSingle();

                    if (c != null) {
                        new Update(Channel.class)
                                .set("UnreadCount = ? AND ", c.unreadCount + 1)
                                .where("DevKey = ? AND ChannelName = ?", devKey, channel)
                                .execute();
                    }
                }
            }
        }
    }

}

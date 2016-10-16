package com.jiguang.jbox.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.Message;
import com.jiguang.jbox.util.LogUtil;
import com.jiguang.jbox.util.ViewHolder;
import com.jiguang.jbox.view.TopBar;

import java.util.HashMap;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends FragmentActivity
        implements ChannelListFragment.OnListFragmentInteractionListener {
    private final String TAG = "MainActivity";

    private TopBar mTopBar;

    private ListView mMsgListView;

    private MessageListAdapter mAdapter;

    private List<Developer> mDevList;

    private String mCurrentDevKey;

    private int mCurrentDev;

    private List<Channel> mChannelList;

    private String mCurrentChannelName;

    private HashMap<String, List<Message>> mMessages;

    private NavigationDrawerFragment mDrawerFragment;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(
                R.id.navigation_drawer);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mTopBar = (TopBar) findViewById(R.id.topBar);
        mTopBar.setTitle(AppApplication.currentChannelName);
        mTopBar.setLeftClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(mDrawerFragment.getView());
            }
        });

        mMsgListView = (ListView) findViewById(R.id.lv_msg);

        List<Message> messages = new Select().from(Message.class)
                .where("DevKey=? AND Channel=?", AppApplication.currentDevKey,
                        AppApplication.currentChannelName)
                .execute();
        mAdapter = new MessageListAdapter(messages);
        mMsgListView.setAdapter(mAdapter);

        View emptyView = findViewById(R.id.tv_hint);
        mMsgListView.setEmptyView(emptyView);

//        onNavigationDrawerItemSelected(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);

        if (AppApplication.shouldUpdateData) {
            mDrawerFragment.updateData();
            AppApplication.shouldUpdateData = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    public void onListItemClick(Channel channel) {
        mTopBar.setTitle(channel.name);

        List<Message> messages = new Select().from(Message.class)
                .where("DevKey=? AND Channel=?", AppApplication.currentDevKey,
                        AppApplication.currentChannelName)
                .execute();
        mAdapter.replaceData(messages);

        mDrawerLayout.closeDrawers();
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

                LogUtil.LOGI(TAG, bundle.toString());

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

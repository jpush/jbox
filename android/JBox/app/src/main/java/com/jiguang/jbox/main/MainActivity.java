package com.jiguang.jbox.main;

import android.Manifest;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.Message;
import com.jiguang.jbox.drawer.ChannelListFragment;
import com.jiguang.jbox.drawer.NavigationDrawerFragment;
import com.jiguang.jbox.main.adapter.MessageListAdapter;
import com.jiguang.jbox.util.PermissionUtil;
import com.jiguang.jbox.view.TopBar;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends FragmentActivity
        implements ChannelListFragment.OnListFragmentInteractionListener {
    public static final int MSG_WHAT_RECEIVE_MSG_CURRENT = 0;
    public static final int MSG_WHAT_RECEIVE_MSG = 1;
    public static final int MSG_WHAT_UPDATE_DEV = 2;

    private final int QUERY_MESSAGE_COUNT = 10;

    private TopBar mTopBar;

    private ListView mMsgListView;

    private MessageListAdapter mAdapter;

    private List<Message> mMessages;

    private NavigationDrawerFragment mDrawerFragment;

    private DrawerLayout mDrawerLayout;

    private MessageReceiver mReceiver;
    private IntentFilter mIntentFilter;

    private int mCurrentOffset = 0; // 当前加载的消息数。

    public static Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        mDrawerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mDrawerLayout.closeDrawers();
                        break;
                }
                return false;
            }
        });

        mDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mDrawerFragment.setDrawerLayout(mDrawerLayout);

        mTopBar = (TopBar) findViewById(R.id.topBar);
        mTopBar.setTitle(AppApplication.currentChannelName);

        ImageView ivMenu = (ImageView) findViewById(R.id.iv_menu);
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerFragment.getView() != null) {
                    mDrawerLayout.openDrawer(mDrawerFragment.getView());
                }
            }
        });

        if (!PermissionUtil.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PermissionUtil.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 0);
        }

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(android.os.Message message) {
                Bundle data = message.getData();
                if (message.what == MSG_WHAT_RECEIVE_MSG_CURRENT) {
                    // 收到的是当前 Channel 的消息，更新界面。
                    Message msg = (Message) data.getSerializable("message");
                    mAdapter.addMessage(msg);
                    mAdapter.notifyDataSetChanged();
                } else if (message.what == MSG_WHAT_RECEIVE_MSG) {
                    String devKey = data.getString("DevKey");
                    mDrawerFragment.mChannelListFragment.updateData(devKey);
                } else if (message.what == MSG_WHAT_UPDATE_DEV) {
                    String devKey = data.getString("DevKey");
                    mDrawerFragment.mDevListFragment.updateData();
                    mDrawerFragment.mChannelListFragment.updateData(devKey);
                }
                return false;
            }
        });

        mReceiver = new MessageReceiver(handler);
        mIntentFilter = new IntentFilter("cn.jpush.android.intent.MESSAGE_RECEIVED");
        mIntentFilter.addAction("cn.jpush.android.intent.NOTIFICATION_RECEIVED");
        mIntentFilter.addCategory("com.jiguang.jbox");

        mMsgListView = (ListView) findViewById(R.id.lv_msg);
        mMsgListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // 如果滑动到了最后，加载更多数据。
                if (mMsgListView.getLastVisiblePosition() + 1 == totalItemCount) {
                    if (mMessages != null) {
                        List<Message> newMessageList = queryMessages(AppApplication.currentDevKey,
                                AppApplication.currentChannelName, mCurrentOffset,
                                QUERY_MESSAGE_COUNT);
                        if (newMessageList != null && !newMessageList.isEmpty()) {
                            mMessages.addAll(newMessageList);
                            mCurrentOffset += newMessageList.size();
                            mAdapter.replaceData(mMessages);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

        View emptyView = findViewById(R.id.tv_hint);
        mMsgListView.setEmptyView(emptyView);

        mMessages = queryMessages(AppApplication.currentDevKey, AppApplication.currentChannelName,
                0, QUERY_MESSAGE_COUNT);
        mCurrentOffset += mMessages.size();
        mAdapter = new MessageListAdapter(mMessages);
        mMsgListView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        registerReceiver(mReceiver, mIntentFilter);

        if (AppApplication.shouldUpdateData) {
            mMessages = queryMessages(AppApplication.currentDevKey, AppApplication
                    .currentChannelName, 0, mCurrentOffset + QUERY_MESSAGE_COUNT);
            mAdapter.replaceData(mMessages);
            mTopBar.setTitle(AppApplication.currentChannelName);

            mDrawerFragment.updateData();
            AppApplication.shouldUpdateData = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerFragment.getView() != null) {
            if (mDrawerLayout.isDrawerOpen(mDrawerFragment.getView())) {
                mDrawerLayout.closeDrawers();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onChannelListItemClick(Channel channel) {
        mTopBar.setTitle(channel.name);

        channel.unreadCount = 0;
        channel.save();
        mDrawerFragment.mChannelListFragment.updateData(channel.devKey);

        List<Message> messages = queryMessages(channel.devKey, channel.name, 0,
                mCurrentOffset + QUERY_MESSAGE_COUNT);
        mAdapter.replaceData(messages);
        mAdapter.notifyDataSetChanged();

        if (mDrawerFragment.getView() != null) {
            mDrawerLayout.closeDrawer(mDrawerFragment.getView());
        }
    }

    private List<Message> queryMessages(String devKey, String channelName, int offSet, int limit) {
        return new Select().from(Message.class)
                .where("DevKey=? AND Channel=?", devKey, channelName)
                .offset(offSet)
                .limit(limit)
                .orderBy("time DESC")
                .execute();
    }

}

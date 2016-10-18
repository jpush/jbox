package com.jiguang.jbox.main;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.bumptech.glide.Glide;
import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.Message;
import com.jiguang.jbox.util.LogUtil;
import com.jiguang.jbox.util.PermissionUtil;
import com.jiguang.jbox.util.ViewHolder;
import com.jiguang.jbox.view.TopBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends FragmentActivity
        implements ChannelListFragment.OnListFragmentInteractionListener {
    private final String TAG = "MainActivity";

    private final int QUERY_MESSAGE_COUNT = 10;

    private TopBar mTopBar;

    private ListView mMsgListView;

    private MessageListAdapter mAdapter;

    private List<Message> mMessages;

    private NavigationDrawerFragment mDrawerFragment;

    private DrawerLayout mDrawerLayout;

    private MyReceiver mReceiver;
    private IntentFilter mIntentFilter;

    private int mCurrentItems = 10; // 当前加载的消息数。

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

        if (!PermissionUtil.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PermissionUtil.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 0);
        }

        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(android.os.Message message) {
                if (message.what == 0) {
                    // 收到的是当前 Channel 的消息，更新界面。
                    Bundle data = message.getData();
                    Message msg = (Message) data.getSerializable("message");
                    mAdapter.addMessage(msg);
                } else if (message.what == 1) {
                    mDrawerFragment.mChannelListFragment.updateData();
                }
                return false;
            }
        });

        mReceiver = new MyReceiver(handler);
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
                if (mMsgListView.getLastVisiblePosition() == totalItemCount) {
                    mMessages.addAll(queryMessages(AppApplication.currentDevKey,
                            AppApplication.currentChannelName, mCurrentItems,
                            mCurrentItems += QUERY_MESSAGE_COUNT));
                    mAdapter.replaceData(mMessages);
                }
            }
        });

        mMessages = queryMessages(AppApplication.currentDevKey, AppApplication
                .currentChannelName, QUERY_MESSAGE_COUNT, QUERY_MESSAGE_COUNT);

        mAdapter = new MessageListAdapter(mMessages);
        mMsgListView.setAdapter(mAdapter);

        View emptyView = findViewById(R.id.tv_hint);
        mMsgListView.setEmptyView(emptyView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        registerReceiver(mReceiver, mIntentFilter);

        if (AppApplication.shouldUpdateData) {
            mMessages = queryMessages(AppApplication.currentDevKey, AppApplication
                    .currentChannelName, QUERY_MESSAGE_COUNT, QUERY_MESSAGE_COUNT);
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
    public void onChannelListItemClick(Channel channel) {
        mTopBar.setTitle(channel.name);
        mCurrentItems = QUERY_MESSAGE_COUNT;

        channel.unreadCount = 0;
        channel.save();

        List<Message> messages = new Select().from(Message.class)
                .where("DevKey=? AND Channel=?", channel.devKey, channel.name)
                .limit(QUERY_MESSAGE_COUNT)
                .execute();
        mAdapter.replaceData(messages);
        mDrawerLayout.closeDrawer(mDrawerFragment.getView());
    }

    private List<Message> queryMessages(String devKey, String channelName, int limit, int offSet) {
        return new Select().from(Message.class)
                .where("DevKey=? AND Channel=?", devKey, channelName)
                .limit(limit)
                .offset(offSet)
                .execute();
    }


    private static class MessageListAdapter extends BaseAdapter {
        private List<Message> mMessages;

        MessageListAdapter(List<Message> list) {
            mMessages = list;
        }

        void replaceData(List<Message> list) {
            mMessages = list;
            notifyDataSetChanged();
        }

        void addMessage(Message msg) {
            if (mMessages != null) {
                mMessages.add(0, msg);
            }
        }

        void addMessages(List<Message> messages) {
            mMessages.addAll(messages);
            notifyDataSetChanged();
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
            TextView tvIcon = ViewHolder.get(convertView, R.id.tv_icon);
            ImageView ivIcon = ViewHolder.get(convertView, R.id.iv_icon);

            if (TextUtils.isEmpty(msg.iconUrl)) {
                ivIcon.setVisibility(View.INVISIBLE);
                tvIcon.setVisibility(View.VISIBLE);

                String firstChar = msg.channelName.substring(0, 1).toUpperCase();
                tvIcon.setText(firstChar);
            } else {
                ivIcon.setVisibility(View.VISIBLE);
                tvIcon.setVisibility(View.INVISIBLE);

                Glide.with(AppApplication.getAppContext())
                        .load(msg.iconUrl)
                        .centerCrop()
                        .crossFade()
                        .into(ivIcon);
            }

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
    public static class MyReceiver extends BroadcastReceiver {
        private Handler mHandler;

        public MyReceiver(Handler handler) {
            mHandler = handler;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {
                Bundle bundle = intent.getExtras();

                String extraJson = bundle.getString(JPushInterface.EXTRA_EXTRA);

                LogUtil.LOGI("MyReceiver", extraJson);

                try {
                    JSONObject jsonObject = new JSONObject(extraJson);

                    String title = bundle.getString(JPushInterface.EXTRA_TITLE);
                    String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);

                    String devKey = jsonObject.getString("dev_key");
                    String channelName = jsonObject.getString("channel");
                    String iconUrl = jsonObject.getString("icon");   // 集成的图标 url。
                    long timeMillis = Long.parseLong(jsonObject.getString("datetime"));

                    // 保存 msg 到本地，并刷新页面数据。
                    Message msg = new Message();
                    msg.title = title;
                    msg.content = content;
                    msg.devKey = devKey;
                    msg.channelName = channelName;
                    msg.iconUrl = iconUrl;
                    msg.time = timeMillis;
                    msg.save();

                    Bundle data = new Bundle();

                    android.os.Message handlerMsg = new android.os.Message();
                    // 如果收到的是当前 Channel 的消息就更新界面，否则保存到数据库并更新界面。
                    if (AppApplication.currentDevKey.equals(devKey) &&
                            AppApplication.currentChannelName.equals(channelName)) {
                        handlerMsg.what = 0;
                        data.putSerializable("message", msg);
                        handlerMsg.setData(data);
                        mHandler.sendMessage(handlerMsg);
                    } else {
                        Channel c = new Select().from(Channel.class)
                                .where("DevKey = ? AND Name = ?", devKey, channelName)
                                .executeSingle();

                        if (c != null) {
                            new Update(Channel.class)
                                    .set("UnreadCount = ?", c.unreadCount + 1)
                                    .where("DevKey = ? AND Name = ?", devKey, channelName)
                                    .execute();

                            handlerMsg.what = 1;
                            mHandler.sendMessage(handlerMsg);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

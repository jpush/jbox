package com.jiguang.jbox.main;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateUtils;
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
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, MessagesContract.View {

    private Toolbar mTopBar;

    private TextView mTvHint;

    private ListView mMsgListView;

    private MessageListAdapter mAdapter;

    private MessagesPresenter mMessagesPresenter;

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

                }
            }

            @Override
            public void onDataNotAvailable() {
                mChannelList = new ArrayList<>();
            }
        });

        MessagesLocalDataSource msgLocalDataSource = MessagesLocalDataSource.getInstance(this);
        MessageRepository msgRepository = MessageRepository.getInstance(msgLocalDataSource);
        mMessagesPresenter = new MessagesPresenter(msgRepository, this);

        mTopBar = (Toolbar) findViewById(R.id.toolbar);

        mTvHint = (TextView) findViewById(R.id.tv_hint);

        mMsgListView = (ListView) findViewById(R.id.lv_msg);
        mAdapter = new MessageListAdapter(new ArrayList<Message>(0));
        mMsgListView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMessagesPresenter.start(); // 初始化数据。
        JPushInterface.onPause(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onResume(this);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (mChannelList != null) {
            mTopBar.setTitle(mChannelList.get(position).getName());
        }
    }

    @Override
    public void showMessages(List<Message> messages) {
        if (messages != null && !messages.isEmpty()) {
            mAdapter.replaceData(messages);

            mTvHint.setVisibility(View.GONE);
            mMsgListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void setPresenter(Object presenter) {

    }

    private void initPresenter() {
        MessageDataSource localDataSource = MessagesLocalDataSource.getInstance(this);
        MessageRepository repository = MessageRepository.getInstance(localDataSource);
        mMessagesPresenter = new MessagesPresenter(repository, this);
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

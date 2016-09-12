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
import com.jiguang.jbox.data.Message;
import com.jiguang.jbox.data.source.MessageDataSource;
import com.jiguang.jbox.data.source.MessageRepository;
import com.jiguang.jbox.data.source.local.MessagesLocalDataSource;
import com.jiguang.jbox.util.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, MessagesContract.View {

    private TextView mTvHint;

    private ListView mMsgListView;

    private MessageListAdapter mAdapter;

    private MessagesPresenter mMessagesPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // TODO:测试
        toolbar.setTitle("Channel 1");

        mTvHint = (TextView) findViewById(R.id.tv_hint);

        mMsgListView = (ListView) findViewById(R.id.lv_msg);
        mAdapter = new MessageListAdapter(new ArrayList<Message>(0));
        mMsgListView.setAdapter(mAdapter);

        initPresenter("channel1");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMessagesPresenter.start(); // 初始化数据。
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

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

    private void initPresenter(String channelId) {
        MessageDataSource localDataSource = MessagesLocalDataSource.getInstance(this);
        MessageRepository repository = MessageRepository.getInstance(localDataSource);
        mMessagesPresenter = new MessagesPresenter(repository, this);
        mMessagesPresenter.setChannelId(channelId);
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

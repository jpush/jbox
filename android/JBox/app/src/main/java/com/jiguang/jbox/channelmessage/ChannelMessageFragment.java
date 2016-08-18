package com.jiguang.jbox.channelmessage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.util.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


public class ChannelMessageFragment extends Fragment implements ChannelMessageContract.View,
        AdapterView.OnItemClickListener {

    private static ChannelMessageFragment INSTANCE;

    private ChannelMessageAdapter mAdapter;

    private ChannelMessageContract.Presenter mPresenter;

    public static ChannelMessageFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChannelMessageFragment();
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ChannelMessageAdapter(new ArrayList<Channel>(0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_channel_message, container, false);

        ListView listView = (ListView) root.findViewById(R.id.lv_message_channel);
        listView.setOnItemClickListener(this);
        listView.setAdapter(mAdapter);

        return root;
    }

    @Override
    public void showMessagesUi(String channelName) {

    }

    @Override
    public void setPresenter(ChannelMessageContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // 进入消息列表界面。

    }

    private static class ChannelMessageAdapter extends BaseAdapter {

        private List<Channel> mChannels;

        public ChannelMessageAdapter(List<Channel> channels) {
            mChannels = channels;
        }

        public void replaceData(List<Channel> channels) {
            mChannels = checkNotNull(channels);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mChannels.size();
        }

        @Override
        public Object getItem(int i) {
            return mChannels.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                view = inflater.inflate(R.layout.list_item_channel, viewGroup, false);
            }

            final Channel channel = (Channel) getItem(i);

            TextView tvHead = ViewHolder.get(view, R.id.tv_head);
            tvHead.setText(channel.getDeveloper().getHeadStr());

            if (channel.getUnReadMessageCount() != 0) {
                TextView tvBadge = ViewHolder.get(view, R.id.tv_badge);
                tvBadge.setVisibility(View.VISIBLE);
                tvBadge.setText(channel.getUnReadMessageCount());
            }

            TextView tvName = ViewHolder.get(view, R.id.tv_name);
            tvName.setText(channel.getName());

            TextView tvLatestMsg = ViewHolder.get(view, R.id.tv_latest_msg);
            tvLatestMsg.setText(channel.getLatestMessage().getContent());

            return view;
        }
    }

}

package com.jiguang.jbox.channel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.util.ViewHolder;

import java.util.List;

public class SubChannelListAdapter extends BaseAdapter {
    private List<Channel> mChannels;

    private OnChannelCheckedListener mChannelCheckedListener;

    public SubChannelListAdapter(List<Channel> channels, OnChannelCheckedListener listener) {
        mChannels = channels;
        mChannelCheckedListener = listener;
    }

    public void replaceData(List<Channel> channels) {
        mChannels = channels;
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

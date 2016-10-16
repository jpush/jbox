package com.jiguang.jbox.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.util.LogUtil;

import java.util.List;

public class ChannelDrawerRecyclerViewAdapter extends
        RecyclerView.Adapter<ChannelDrawerRecyclerViewAdapter.ViewHolder> {

    private List<Channel> mValues;
    private final ChannelListFragment.OnListFragmentInteractionListener mListener;

    public ChannelDrawerRecyclerViewAdapter(List<Channel> items,
                                            ChannelListFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_channel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = mValues.get(position);
        holder.tvChannel.setText(mValues.get(position).name);

        if (holder.item.unreadCount != 0) {
            holder.tvUnreadCount.setText(holder.item.unreadCount);
            holder.tvUnreadCount.setVisibility(View.VISIBLE);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    LogUtil.LOGI("JBox", "Channel click " + holder.item.name);
                    AppApplication.currentChannelName = holder.item.name;
                    mListener.onListItemClick(holder.item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void updateData(List<Channel> channels) {
        mValues = channels;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final ImageView ivRemove;
        public final TextView tvChannel;
        public final TextView tvUnreadCount;
        public Channel item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ivRemove = (ImageView) view.findViewById(R.id.iv_remove);
            tvChannel = (TextView) view.findViewById(R.id.tv_channel);
            tvUnreadCount = (TextView) view.findViewById(R.id.tv_unread_count);
        }

    }
}

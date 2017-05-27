package com.jiguang.jbox.drawer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.drawer.ChannelListFragment;

import java.util.ArrayList;
import java.util.List;

public class ChannelDrawerRecyclerViewAdapter extends
        RecyclerView.Adapter<ChannelDrawerRecyclerViewAdapter.ViewHolder> {

    private List<Channel> mValues = new ArrayList<>();
    private final ChannelListFragment.OnListFragmentInteractionListener mListener;

    public ChannelDrawerRecyclerViewAdapter(List<Channel> items,
                                            ChannelListFragment.OnListFragmentInteractionListener listener) {
        if (items != null) {
            mValues = items;
        }
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
            holder.tvUnreadCount.setText(String.valueOf(holder.item.unreadCount));
            holder.tvUnreadCount.setVisibility(View.VISIBLE);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    AppApplication.currentChannelName = holder.item.name;
                    mListener.onChannelListItemClick(holder.item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvChannel;
        public final TextView tvUnreadCount;
        public Channel item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvChannel = (TextView) view.findViewById(R.id.tv_channel);
            tvUnreadCount = (TextView) view.findViewById(R.id.tv_unread_count);
        }

    }
}

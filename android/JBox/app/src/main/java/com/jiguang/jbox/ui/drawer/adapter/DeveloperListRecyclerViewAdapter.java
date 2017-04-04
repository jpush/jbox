package com.jiguang.jbox.ui.drawer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.model.Developer;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.jiguang.jbox.ui.drawer.DeveloperListFragment.OnListFragmentInteractionListener;


public class DeveloperListRecyclerViewAdapter extends
        RecyclerView.Adapter<DeveloperListRecyclerViewAdapter.ViewHolder> {
    private List<Developer> mValues = new ArrayList<>();
    private final OnListFragmentInteractionListener mListener;

    public DeveloperListRecyclerViewAdapter(List<Developer> items,
                                            OnListFragmentInteractionListener listener) {
        if (items != null) {
            mValues = items;
        }
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_developer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.item = mValues.get(position);

        if (holder.item.isSelected) {
            holder.mView.setAlpha(1);
        } else {
            holder.mView.setAlpha((float) 0.5);
        }

        Glide.with(AppApplication.getAppContext())
                .load(holder.item.avatarUrl)
                .placeholder(R.drawable.ic_avatar_default)
                .dontAnimate()
                .into(holder.mIvAvatar);

        if (holder.item.unreadCount == 0) {
            holder.mTvUnreadCount.setVisibility(View.INVISIBLE);
        } else {
            holder.mTvUnreadCount.setVisibility(View.VISIBLE);
            holder.mTvUnreadCount.setText(String.valueOf(holder.item.unreadCount));
        }

        holder.mTvName.setText(holder.item.name);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppApplication.currentDevKey = holder.item.key;

                if (!holder.item.isSelected) {
                    for (Developer d : mValues) {
                        if (d != holder.item && d.isSelected) {
                            d.isSelected = false;
                            d.save();
                            break;
                        }
                    }
                    holder.item.isSelected = true;
                    holder.item.save();
                    notifyDataSetChanged();
                }

                if (null != mListener) {
                    mListener.onDevListItemClick(holder.item.key);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final CircleImageView mIvAvatar;
        final TextView mTvUnreadCount;
        final TextView mTvName;
        Developer item;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mIvAvatar = (CircleImageView) view.findViewById(R.id.iv_avatar);
            mTvUnreadCount = (TextView) view.findViewById(R.id.tv_unread_count);
            mTvName = (TextView) view.findViewById(R.id.tv_name);
        }
    }
}

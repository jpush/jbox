package com.jiguang.jbox.drawer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;
import static com.jiguang.jbox.drawer.DeveloperListFragment.OnListFragmentInteractionListener;


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
            holder.view.setAlpha(1);
        } else {
            holder.view.setAlpha((float) 0.5);
        }

        Glide.with(AppApplication.getAppContext())
                .load(holder.item.avatarUrl)
                .placeholder(R.drawable.ic_avatar_default)
                .dontAnimate()
                .into(holder.avatar);

        holder.name.setText(holder.item.name);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.LOGI(TAG, holder.item.isSelected + "");
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

    public void updateData(List<Developer> devs) {
        mValues = devs;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View view;
        final CircleImageView avatar;
        final TextView name;
        Developer item;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            avatar = (CircleImageView) view.findViewById(R.id.iv_avatar);
            name = (TextView) view.findViewById(R.id.tv_name);
        }

    }
}

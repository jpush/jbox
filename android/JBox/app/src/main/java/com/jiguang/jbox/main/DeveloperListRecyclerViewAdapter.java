package com.jiguang.jbox.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Developer;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.jiguang.jbox.main.DeveloperListFragment.OnListFragmentInteractionListener;


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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Developer item = mValues.get(position);

        holder.item = mValues.get(position);

        Bitmap bitmap = BitmapFactory.decodeFile(item.avatarPath);
        if (bitmap != null) {
//            bitmap = Bitmap.createScaledBitmap(bitmap, holder.avatar.getWidth(),
//                    holder.avatar.getHeight(), true);
            holder.avatar.setImageBitmap(bitmap);
        }

        holder.name.setText(item.name);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Developer 列表的点击事件。
                    mListener.onDevListItemClick(holder.item.key);
                    AppApplication.currentDevKey = item.key;
                    // 更新 Channel 列表。
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

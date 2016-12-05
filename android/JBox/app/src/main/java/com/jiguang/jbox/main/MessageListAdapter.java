package com.jiguang.jbox.main;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Message;

import java.util.List;


class MessageListAdapter extends BaseAdapter {
    private List<Message> mMessages;

    MessageListAdapter(List<Message> list) {
        mMessages = list;
    }

    void replaceData(List<Message> list) {
        mMessages = list;
    }

    void addMessage(Message msg) {
        if (mMessages != null) {
            mMessages.add(0, msg);
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
    public View getView(int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.item_msg, parent, false);
            holder = new ViewHolder();
            holder.tvIcon = (TextView) convertView.findViewById(R.id.tv_icon);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Message msg = mMessages.get(position);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(msg.url)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(msg.url));
                    parent.getContext().startActivity(intent);
                }
            }
        });

        if (TextUtils.isEmpty(msg.iconUrl)) {
            if (holder.ivIcon.getVisibility() == View.VISIBLE) {
                holder.ivIcon.setVisibility(View.INVISIBLE);
                holder.tvIcon.setVisibility(View.VISIBLE);
            }

            String firstChar;
            if (TextUtils.isEmpty(msg.integrationName)) {
                firstChar = msg.channelName.substring(0, 1).toUpperCase();
            } else {
                firstChar = msg.integrationName.substring(0, 1).toLowerCase();
            }
            holder.tvIcon.setText(firstChar);
        } else {
            if (holder.ivIcon.getVisibility() == View.INVISIBLE) {
                holder.ivIcon.setVisibility(View.VISIBLE);
                holder.tvIcon.setVisibility(View.GONE);
            }
            Glide.with(AppApplication.getAppContext())
                    .load("http://" + msg.iconUrl)
                    .centerCrop()
                    .placeholder(R.drawable.default_avatar)
                    .into(holder.ivIcon);
        }

        if (TextUtils.isEmpty(msg.title)) {
            holder.tvTitle.setVisibility(View.GONE);
        } else {
            if (holder.tvTime.getVisibility() == View.GONE) {
                holder.tvTitle.setVisibility(View.VISIBLE);
            }
            holder.tvTitle.setText(msg.title);
        }

        holder.tvContent.setText(msg.content);

        String formatTime = DateUtils.formatDateTime(parent.getContext(),
                msg.time * 1000, DateUtils.FORMAT_SHOW_TIME);
        holder.tvTime.setText(formatTime);

        return convertView;
    }

    private static class ViewHolder {
        TextView tvIcon;
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvContent;
        TextView tvTime;
    }
}

package com.jiguang.jbox.main.adapter;

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
import com.jiguang.jbox.util.ViewHolder;

import java.util.List;


public class MessageListAdapter extends BaseAdapter {
    private List<Message> mMessages;

    public MessageListAdapter(List<Message> list) {
        mMessages = list;
    }

    public void replaceData(List<Message> list) {
        mMessages = list;
    }

    public void addMessage(Message msg) {
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
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.item_msg, parent, false);
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

        TextView tvIcon = ViewHolder.get(convertView, R.id.tv_icon);
        ImageView ivIcon = ViewHolder.get(convertView, R.id.iv_icon);

        if (TextUtils.isEmpty(msg.iconUrl)) {
            if (ivIcon.getVisibility() == View.VISIBLE) {
                ivIcon.setVisibility(View.INVISIBLE);
                tvIcon.setVisibility(View.VISIBLE);
            }

            String firstChar = msg.channelName.substring(0, 1).toUpperCase();
            tvIcon.setText(firstChar);
        } else {
            if (ivIcon.getVisibility() == View.INVISIBLE) {
                ivIcon.setVisibility(View.VISIBLE);
                tvIcon.setVisibility(View.GONE);
            }

            Glide.with(AppApplication.getAppContext())
                    .load("http://" + msg.iconUrl)
                    .centerCrop()
                    .placeholder(R.drawable.default_avatar)
                    .into(ivIcon);
        }

        TextView tvTitle = ViewHolder.get(convertView, R.id.tv_title);
        tvTitle.setText(msg.title);

        TextView tvContent = ViewHolder.get(convertView, R.id.tv_content);
        tvContent.setText(msg.content);

        String formatTime = DateUtils.formatDateTime(parent.getContext(),
                msg.time * 1000, DateUtils.FORMAT_SHOW_TIME);

        TextView tvTime = ViewHolder.get(convertView, R.id.tv_time);
        tvTime.setText(formatTime);

        return convertView;
    }
}

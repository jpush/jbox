package com.jiguang.jbox.message;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Message;
import com.jiguang.jbox.util.ViewHolder;

import java.util.List;

/**
 * 显示 Channel 中的消息列表，从本地数据库读取。
 */
public class MessageActivity extends Activity {
    public static final String EXTRA_CHANNEL_ID = "CHANNEL_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        ListView lvMsg = (ListView) findViewById(R.id.lv_msg);
        // TODO: 填充数据。

    }

    private class MessageListAdapter extends BaseAdapter {

        private List<Message> mMessages;

        public MessageListAdapter(List<Message> messages) {
            mMessages = messages;
        }

        public void replaceData(List<Message> messages) {
            mMessages = messages;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mMessages.size();
        }

        @Override
        public Object getItem(int i) {
            return mMessages.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                view = inflater.inflate(R.layout.list_item_message, viewGroup, false);
            }

            Message msg = (Message) getItem(i);

            TextView tvTitle = ViewHolder.get(view, R.id.tv_title);
            tvTitle.setText(msg.getTitle());

            TextView tvTime = ViewHolder.get(view, R.id.tv_time);
            tvTime.setText(msg.getTime());

            TextView tvContent = ViewHolder.get(view, R.id.tv_content);
            tvContent.setText(msg.getContent());

            return view;
        }
    }

}

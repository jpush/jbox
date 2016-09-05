package com.jiguang.jbox.channel;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;

import java.util.List;

/**
 * 扫描二维码后的 channel 展示界面。
 */
public class ChannelActivity extends Activity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        mListView = (ListView) findViewById(R.id.lv_channel);
        View headView = getLayoutInflater().inflate(R.layout.view_subscribe_channel, mListView, false);
        mListView.addHeaderView(headView);
    }

    private static class SubChannelListAdapter extends BaseAdapter {

        private Context mContext;

        private List<Channel> mChannels;

        public SubChannelListAdapter(Context context, List<Channel> channels) {

        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                view = inflater.inflate(R.layout.item_subscribe_channel, viewGroup, false);
            }

            return view;
        }
    }
}

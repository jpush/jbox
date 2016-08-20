package com.jiguang.jbox.channel;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;

import java.util.ArrayList;
import java.util.List;

public class ChannelFragment extends Fragment {

    private ChannelAdapter mListAdapter;

    public ChannelFragment() {
    }

    public static ChannelFragment newInstance() {
        return new ChannelFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_channel, container, false);

        SwipeMenuListView listView = (SwipeMenuListView) v.findViewById(R.id.lv_channel);
        // 设置侧滑选项。
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem removeItem = new SwipeMenuItem(getActivity().getApplicationContext());
                removeItem.setWidth(48);
                removeItem.setTitle(R.string.channel_remove);
                removeItem.setTitleColor(Color.WHITE);
                removeItem.setBackground(android.R.color.holo_red_light);

                menu.addMenuItem(removeItem);
            }
        };

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (position == 0) {    // 删除。

                }
                return false;
            }
        });

        listView.setMenuCreator(creator);
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);

        mListAdapter = new ChannelAdapter(new ArrayList<Channel>(0));
        listView.setAdapter(mListAdapter);

        return v;
    }

    public void showChannels(List<Channel> channels) {
        mListAdapter.replaceData(channels);
    }

    private static class ChannelAdapter extends BaseAdapter {

        private List<Channel> mChannels;

        public ChannelAdapter(List<Channel> channels) {
            mChannels = channels;
        }

        public void replaceData(List<Channel> data) {
            setList(data);
        }

        private void setList(List<Channel> data) {
            mChannels = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mChannels == null ? 0 : mChannels.size();
        }

        @Override
        public Object getItem(int position) {
            return mChannels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Channel channel = (Channel) getItem(position);

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.list_item_channel, parent, false);
            }


            return convertView;
        }
    }
}

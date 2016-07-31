package com.jiguang.jbox.channel;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.jiguang.jbox.R;

public class ChannelFragment extends Fragment {
    public ChannelFragment() {
    }

    public static ChannelFragment newInstance() {
        ChannelFragment fragment = new ChannelFragment();
        return fragment;
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

        return v;
    }
}

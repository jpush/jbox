package com.jiguang.jbox.account;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.jiguang.jbox.R;

public class AccountFragment extends Fragment {
    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        SwipeMenuListView listView = (SwipeMenuListView) v.findViewById(R.id.lv_accounts);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem removeItem = new SwipeMenuItem(getActivity().getApplicationContext());
                removeItem.setWidth(48);
                removeItem.setTitle(R.string.account_remove);
                removeItem.setTitleColor(Color.WHITE);
                removeItem.setBackground(android.R.color.holo_red_light);

                menu.addMenuItem(removeItem);
            }
        };

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (position == 0) { // Remove button.

                }
                return false;
            }
        });

        listView.setMenuCreator(creator);
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);

        return v;
    }

}

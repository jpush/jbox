package com.jiguang.jbox.main;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.util.ViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * 侧滑界面。
 */
public class NavigationDrawerFragment extends Fragment
        implements DeveloperListFragment.OnListFragmentInteractionListener {
    private final String TAG = "NavigationDrawerFragment";

    private NavigationDrawerCallbacks mCallbacks;

    private DrawerLayout mDrawerLayout;

    private SearchView mSearchView;

    private ListView mDrawerListView;

    private DrawerListAdapter mDrawerListAdapter;

    private View mFragmentContainerView;

    private boolean mIsEditChannels = false;

    private ImageView mIvCircleFirst;
    private ImageView mIvCircleSecond;

    private DeveloperListFragment mDevListFragment;
    private ChannelListFragment mChannelListFragment;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        mCallbacks = (NavigationDrawerCallbacks) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.drawer_main, container, false);

        mIvCircleFirst = (ImageView) v.findViewById(R.id.iv_circle_first);
        mIvCircleFirst.setSelected(true);
        mIvCircleSecond = (ImageView) v.findViewById(R.id.iv_circle_second);

        List<android.support.v4.app.Fragment> fragmentList = new ArrayList<>();

        mDevListFragment = new DeveloperListFragment();
        mDevListFragment.setListener(this);
        fragmentList.add(mDevListFragment);

        mChannelListFragment = new ChannelListFragment();
        fragmentList.add(mChannelListFragment);

        FragmentManager fm = getActivity().getSupportFragmentManager();

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyViewPagerAdapter(fm, fragmentList));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mIvCircleFirst.setSelected(true);
                    mIvCircleSecond.setSelected(false);
                } else {
                    mIvCircleFirst.setSelected(false);
                    mIvCircleSecond.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onDevListItemClick(String devKey) {
        mChannelListFragment.updateData(devKey);
    }

    public void updateData() {
        mDevListFragment.updateData();
        mChannelListFragment.updateData(AppApplication.currentDevKey);
    }


    static class MyViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragmentList;

        MyViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragmentList = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

    void editChannels() {
        mIsEditChannels = !mIsEditChannels;
        mDrawerListAdapter.editChannels(mIsEditChannels);
    }

    public interface NavigationDrawerCallbacks {
        void onDrawerChannelListItemSelected(Channel channel);
    }

    private static class DrawerListAdapter extends BaseAdapter {

        private List<Channel> mChannels;

        private boolean mIsEdited;

        DrawerListAdapter(List<Channel> channels) {
            mChannels = channels;
        }

        void editChannels(boolean isEdited) {
            mIsEdited = isEdited;
        }

        void replaceData(List<Channel> channels) {
            mChannels = channels;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mChannels.size();
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
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.item_channel, parent, false);
            }

            Channel channel = (Channel) getItem(position);

            ImageView ivRemove = ViewHolder.get(convertView, R.id.iv_remove);
            if (mIsEdited && ivRemove.getVisibility() == View.GONE) {
                ivRemove.setVisibility(View.VISIBLE);
            } else if (!mIsEdited && ivRemove.getVisibility() == View.VISIBLE) {
                ivRemove.setVisibility(View.GONE);
            }

            TextView tvChannelName = ViewHolder.get(convertView, R.id.tv_channel);
            tvChannelName.setText(channel.name);

            TextView tvUnread = ViewHolder.get(convertView, R.id.tv_unread_count);

            if (channel.unreadCount != 0 && tvUnread.getVisibility() == View.INVISIBLE) {
                tvUnread.setVisibility(View.VISIBLE);
                tvUnread.setText(channel.unreadCount);

            } else if (channel.unreadCount == 0 && tvUnread.getVisibility() == View.VISIBLE) {
                tvUnread.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }
}

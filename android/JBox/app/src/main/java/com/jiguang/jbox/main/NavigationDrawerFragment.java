package com.jiguang.jbox.main;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.util.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 侧滑界面。
 */
public class NavigationDrawerFragment extends Fragment {

    private NavigationDrawerCallbacks mCallbacks;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private DrawerListAdapter mChannelListAdapter;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private boolean mIsEditChannels = false;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (NavigationDrawerCallbacks) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.drawer_main, container, false);

        List<Channel> channels = new ArrayList<>();
        channels.add(new Channel("channel 1"));
        channels.add(new Channel("channel 2"));
        channels.add(new Channel("channel 3"));
        channels.add(new Channel("channel 4"));

        mChannelListAdapter = new DrawerListAdapter(getActivity(), new ArrayList<>(channels));

        mDrawerListView = (ListView) v.findViewById(R.id.lv_channel);
        mDrawerListView.setAdapter(mChannelListAdapter);

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    void editChannels() {
        mIsEditChannels = true;
        mChannelListAdapter.editChannels(mIsEditChannels);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    public interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(int position);
    }

    private static class DrawerListAdapter extends BaseAdapter {

        private Context mContext;

        private List<Channel> mChannels;

        private boolean mIsEdited;

        public DrawerListAdapter(Context context, List<Channel> channels) {
            mContext = context;
            mChannels = channels;
        }

        public void editChannels(boolean isEdited) {
            mIsEdited = isEdited;
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
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.item_channel, parent, false);
            }

            Channel channel = (Channel) getItem(position);

            TextView tvChannelName = ViewHolder.get(convertView, R.id.tv_channel);
            tvChannelName.setText(channel.getName());

            ImageView ivRemove = ViewHolder.get(convertView, R.id.iv_edit);
            if (mIsEdited && ivRemove.getVisibility() == View.GONE) {
                ivRemove.setVisibility(View.VISIBLE);
            } else if (!mIsEdited && ivRemove.getVisibility() == View.VISIBLE) {
                ivRemove.setVisibility(View.GONE);
            }

//            TextView tvUnread = ViewHolder.get(convertView, R.id.tv_unread_count);
//            tvUnread.setText(channel.getUnReadMessageCount());

            return convertView;
        }
    }
}

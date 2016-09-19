package com.jiguang.jbox.main;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.util.ViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * 侧滑界面。
 */
public class NavigationDrawerFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    private NavigationDrawerCallbacks mCallbacks;

    private DrawerLayout mDrawerLayout;

    private SearchView mSearchView;

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

    public void initData(List<Channel> data) {
        if (mChannelListAdapter != null) {
            mChannelListAdapter.replaceData(data);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.drawer_main, container, false);
        mFragmentContainerView = v;

        mDrawerLayout = (DrawerLayout) v.findViewById(R.id.drawer_layout);

        // TODO：搜索功能。
        mSearchView = (SearchView) v.findViewById(R.id.search_view);

        ImageView ivEdit = (ImageView) v.findViewById(R.id.iv_edit);
        ivEdit.setOnClickListener(this);

        mDrawerListView = (ListView) v.findViewById(R.id.lv_channel);
        mChannelListAdapter = new DrawerListAdapter(new ArrayList<Channel>());
        mDrawerListView.setAdapter(mChannelListAdapter);
        mDrawerListView.setOnItemClickListener(this);

        ImageButton btnAddChannel = (ImageButton) v.findViewById(R.id.btn_add_channel);
        btnAddChannel.setOnClickListener(this);

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    void editChannels() {
        mIsEditChannels = !mIsEditChannels;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_edit:
                editChannels();
                break;
            case R.id.btn_add_channel:
                // 进入二维码扫描界面。
                startActivity(new Intent(getActivity(), ScanActivity.class));
                break;
            default:
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        selectItem(i);
    }

    public interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(int position);
    }

    private static class DrawerListAdapter extends BaseAdapter {

        private List<Channel> mChannels;

        private boolean mIsEdited;

        public DrawerListAdapter(List<Channel> channels) {
            mChannels = channels;
        }

        public void editChannels(boolean isEdited) {
            mIsEdited = isEdited;
            notifyDataSetChanged();
        }

        public void replaceData(List<Channel> channels) {
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
            tvChannelName.setText(channel.getName());

            TextView tvUnread = ViewHolder.get(convertView, R.id.tv_unread_count);

            if (channel.getUnReadMessageCount() != 0 &&
                    tvUnread.getVisibility() == View.INVISIBLE) {

                tvUnread.setVisibility(View.VISIBLE);
                tvUnread.setText(channel.getUnReadMessageCount());

            } else if (channel.getUnReadMessageCount() == 0 &&
                    tvUnread.getVisibility() == View.VISIBLE) {

                tvUnread.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }
}

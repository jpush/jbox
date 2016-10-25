package com.jiguang.jbox.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import com.activeandroid.query.Select;
import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.channel.ChannelActivity;
import com.jiguang.jbox.data.Channel;

import java.util.List;

/**
 * 侧边栏 Channel list.
 */
public class ChannelListFragment extends Fragment {
    private static final String TAG = "ChannelListFragment";

    private OnListFragmentInteractionListener mListener;
    private List<Channel> mChannels;

    private RecyclerView mRecyclerView;
    private ChannelDrawerRecyclerViewAdapter mAdapter;

    public ChannelListFragment() {
        if (!TextUtils.isEmpty(AppApplication.currentDevKey)) {
            mChannels = new Select().from(Channel.class)
                    .where("DevKey=? AND IsSubscribe=?", AppApplication.currentDevKey, true)
                    .execute();

            if (mChannels != null && !mChannels.isEmpty()) {
                AppApplication.currentChannelName = mChannels.get(0).name;
            }
        }
    }

    @SuppressWarnings("unused")
    public static ChannelListFragment newInstance(int columnCount) {
        ChannelListFragment fragment = new ChannelListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer_channel, container, false);

        SearchView searchView = (SearchView) view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mChannels = new Select().from(Channel.class)
                        .where("DevKey=? AND IsSubscribe=? AND Name LIKE ?",
                                AppApplication.currentDevKey, true, '%' + newText + '%')
                        .execute();
                mAdapter.replaceData(mChannels);
                mAdapter.notifyDataSetChanged();
                return false;
            }
        });

        ImageView ivEdit = (ImageView) view.findViewById(R.id.iv_edit);
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChannelActivity.class);
                intent.putExtra(ChannelActivity.EXTRA_DEV_KEY, AppApplication.currentDevKey);
                startActivity(intent);
            }
        });

        // Set the adapter
        Context context = view.getContext();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_channel);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new ChannelDrawerRecyclerViewAdapter(mChannels, mListener);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateData(String devKey) {
        mChannels = new Select().from(Channel.class)
                .where("DevKey=? AND IsSubscribe=?", devKey, true)
                .execute();
//        mAdapter.replaceData(mChannels);
//        mAdapter.notifyDataSetChanged();

        mAdapter = new ChannelDrawerRecyclerViewAdapter(mChannels, mListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    public interface OnListFragmentInteractionListener {
        void onChannelListItemClick(Channel channel);
    }

}

package com.jiguang.jbox.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.activeandroid.query.Select;
import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Developer;

import java.util.List;

/**
 * 侧边栏 Developer List Fragment.
 */
public class DeveloperListFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;

    private List<Developer> mDevs;

    private RecyclerView mRecyclerView;

    private DeveloperListRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DeveloperListFragment() {
        mDevs = new Select().from(Developer.class).execute();
        if (mDevs != null && !mDevs.isEmpty()){
            AppApplication.currentDevKey = mDevs.get(0).key;
        }
    }

    @SuppressWarnings("unused")
    public static DeveloperListFragment newInstance() {
        DeveloperListFragment fragment = new DeveloperListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer_developer, container, false);

        ImageView ivAdd = (ImageView) view.findViewById(R.id.iv_add);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ScanActivity.class));
            }
        });

        // Set the adapter
        Context context = view.getContext();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_dev);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new DeveloperListRecyclerViewAdapter(mDevs, mListener);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setListener(OnListFragmentInteractionListener listener) {
        mListener = listener;
    }

    public void updateData() {
        mDevs = new Select().from(Developer.class).execute();
        mAdapter = new DeveloperListRecyclerViewAdapter(mDevs, mListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 点击 Developer list 的事件。
     */
    public interface OnListFragmentInteractionListener {
        void onDevListItemClick(String devKey);
    }
}

package com.jiguang.jbox.account;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.util.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class AccountFragment extends Fragment implements DeveloperContract.View,
        AdapterView.OnItemClickListener {

    private DeveloperContract.Presenter mPresenter;

    private DevelopersAdapter mListAdapter;

    private List<Developer> mDevelopers;

    public AccountFragment() {
        // Required empty public constructor.
    }

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        SwipeMenuListView listView = (SwipeMenuListView) root.findViewById(R.id.lv_accounts);
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
                    Developer dev = (Developer) mListAdapter.getItem(index);
                    mPresenter.deleteDeveloper(dev.getDevKey());
                }
                return false;
            }
        });

        listView.setMenuCreator(creator);
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
        listView.setOnItemClickListener(this);

        mListAdapter = new DevelopersAdapter(new ArrayList<Developer>(0));
        listView.setAdapter(mListAdapter);

        return root;
    }

    public void setPresenter(@NonNull DeveloperContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();     // Load data.
    }

    @Override
    public void showDevelopers(List<Developer> developers) {
        mDevelopers = developers;
        mListAdapter.replaceData(developers);
    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public void showAddDeveloper() {
        Intent intent = new Intent(getContext(), ScanDeveloperActivity.class);
        startActivityForResult(intent, ScanDeveloperActivity.REQUEST_ADD_DEVELOPER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode);
    }

    @Override
    public void showLoadingError() {

    }

    @Override
    public void showSuccessfullySavedMessage() {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Developer dev = mDevelopers.get(i);

    }


    private static class DevelopersAdapter extends BaseAdapter {

        private List<Developer> mDevelopers;

        public DevelopersAdapter(List<Developer> developers) {
            mDevelopers = developers;
        }

        public void replaceData(List<Developer> developers) {
            setList(developers);
        }

        private void setList(List<Developer> developers) {
            mDevelopers = developers;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mDevelopers != null ? mDevelopers.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mDevelopers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.list_item_dev, parent, false);
            }

            Developer dev = (Developer) getItem(position);

            TextView tvHead = ViewHolder.get(convertView, R.id.tv_head);
            String devName = dev.getDevName();
            tvHead.setText(devName.substring(0, 1));

            TextView tvName = ViewHolder.get(convertView, R.id.tv_username);
            tvName.setText(dev.getDevName());

            TextView tvDevKey = ViewHolder.get(convertView, R.id.tv_dev_key);
            tvDevKey.setText(dev.getDevKey());

            return convertView;
        }
    }

}

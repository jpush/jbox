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
import android.widget.BaseAdapter;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.source.DeveloperRepository;
import com.jiguang.jbox.data.source.local.DeveloperLocalDataSource;
import com.jiguang.jbox.databinding.FragmentAccountBinding;
import com.jiguang.jbox.databinding.ListItemDevBinding;

import java.util.ArrayList;
import java.util.List;

import static android.databinding.DataBindingUtil.getBinding;
import static com.google.common.base.Preconditions.checkNotNull;

public class AccountFragment extends Fragment implements DeveloperContract.View {

    private DeveloperContract.Presenter mPresenter;

    private DevelopersAdapter mListAdapter;

    private DeveloperViewModel mViewModel;

    public AccountFragment() {
        // Required empty public constructor.
    }

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the presenter.
        DeveloperRemoteDataSource remoteDataSource = DeveloperRemoteDataSource.getInstance();
        DeveloperLocalDataSource localDataSource = DeveloperLocalDataSource.getInstance(getContext());
        DeveloperRepository repository = DeveloperRepository.getInstance(remoteDataSource, localDataSource);
        mPresenter = new DeveloperPresenter(repository, this);
        mViewModel = new DeveloperViewModel(getContext(), mPresenter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentAccountBinding accountBinding = FragmentAccountBinding.inflate(inflater, container, false);

        accountBinding.setActionHandler(mPresenter);

        SwipeMenuListView listView = accountBinding.lvAccounts;
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

        mListAdapter = new DevelopersAdapter(new ArrayList<Developer>(0), mPresenter);

        return accountBinding.getRoot();
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


    private static class DevelopersAdapter extends BaseAdapter {

        private List<Developer> mDevelopers;

        private DeveloperContract.Presenter mPresenter;

        public DevelopersAdapter(List<Developer> developers, DeveloperContract.Presenter presenter) {
            mDevelopers = developers;
            mPresenter = presenter;
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
            Developer dev = (Developer) getItem(position);
            ListItemDevBinding binding;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                binding = ListItemDevBinding.inflate(inflater, parent, false);
            } else {
                binding = getBinding(convertView);
            }

            binding.setDeveloper(dev);
            binding.executePendingBindings();

            return binding.getRoot();
        }
    }
}

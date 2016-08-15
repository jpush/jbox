package com.jiguang.jbox.account;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.source.DeveloperDataSource;
import com.jiguang.jbox.data.source.DeveloperRepository;

import java.util.List;

/**
 * Listens to user actions from the UI ({@link AccountFragment}), retrieves the data and update the
 * UI as required.
 */
import static com.google.common.base.Preconditions.checkNotNull;

public class DeveloperPresenter implements DeveloperContract.Presenter {

    private final DeveloperRepository mDeveloperRepository;

    private final DeveloperContract.View mDevelopersView;

    private boolean mFirstLoad = true;

    public DeveloperPresenter(@NonNull DeveloperRepository developerRepository,
                              @NonNull DeveloperContract.View developersView) {
        mDeveloperRepository = developerRepository;
        mDevelopersView = developersView;

        mDevelopersView.setPresenter(this);
    }

    @Override
    public void result(int requestCode, int resultCode) {

    }

    @Override
    public void loadDevelopers(boolean forceUpdate) {
        loadDevelopers(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    @Override
    public void addNewDeveloper() {
        mDevelopersView.showAddDeveloper();
    }

    @Override
    public void start() {
        loadDevelopers(false);
    }

    @Override
    public void deleteDeveloper(@NonNull String devKey) {
        checkNotNull(devKey);
        mDeveloperRepository.deleteDeveloper(devKey);
    }

    private void loadDevelopers(boolean forceUpdate, final boolean showLoadingUI) {
        if (forceUpdate) {
            mDeveloperRepository.refreshDevelopers();
        }

        mDeveloperRepository.getDevelopers(new DeveloperDataSource.LoadDevelopersCallback() {
            @Override
            public void onDevelopersLoaded(List<Developer> developers) {
                if (!mDevelopersView.isActive()) {
                    return;
                }

                mDevelopersView.showDevelopers(developers);
            }

            @Override
            public void onDataNotAvailable() {
                if (!mDevelopersView.isActive()) {
                    return;
                }
                mDevelopersView.showLoadingError();
            }
        });
    }

}

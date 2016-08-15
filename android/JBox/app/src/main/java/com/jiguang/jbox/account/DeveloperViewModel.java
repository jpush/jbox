package com.jiguang.jbox.account;

import android.content.Context;

import com.jiguang.jbox.util.BaseObservable;

/**
 * Exposes the data to be used in the {@link com.jiguang.jbox.account.DeveloperContract.View}.
 */
public class DeveloperViewModel extends BaseObservable {

    private final DeveloperContract.Presenter mPresenter;

    private Context mContext;

    public DeveloperViewModel(Context context, DeveloperContract.Presenter presenter) {
        mContext = context;
        mPresenter = presenter;
    }

}

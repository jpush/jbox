package com.jiguang.jbox.message;

import android.content.Context;

import com.jiguang.jbox.util.BaseObservable;

public class MessageViewModel extends BaseObservable {

    private final MessageContract.Presenter mPresenter;

    private Context mContext;

    public MessageViewModel(Context context, MessageContract.Presenter presenter) {
        mContext = context;
        mPresenter = presenter;
    }


}

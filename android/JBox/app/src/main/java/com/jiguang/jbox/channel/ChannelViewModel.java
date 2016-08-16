package com.jiguang.jbox.channel;

import android.content.Context;

import com.jiguang.jbox.util.BaseObservable;

public class ChannelViewModel extends BaseObservable {

    int mChannelListSize = 0;

    private Context mContext;

    private final ChannelsContract.Presenter mPresenter;

    public ChannelViewModel(Context context, ChannelsContract.Presenter presenter) {
        mContext = context;
        mPresenter = presenter;
    }



}

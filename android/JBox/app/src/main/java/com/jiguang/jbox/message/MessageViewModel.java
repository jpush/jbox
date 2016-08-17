package com.jiguang.jbox.message;

import android.content.Context;

import com.jiguang.jbox.util.BaseObservable;

/**
 * 绑定操作到视图上。
 */
public class MessageViewModel extends BaseObservable {

    private final MessageContract.Presenter mPresenter;

    private Context mContext;

    public MessageViewModel(Context context, MessageContract.Presenter presenter) {
        mContext = context;
        mPresenter = presenter;
    }

    /**
     * 打开详细消息列表界面。
     */
    public void openChannelDetail() {

    }

}

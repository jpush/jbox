package com.jiguang.jbox.main;

import android.support.annotation.NonNull;

import com.jiguang.jbox.BasePresenter;
import com.jiguang.jbox.BaseView;
import com.jiguang.jbox.data.Message;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public class MessagesContract {

    interface View extends BaseView {

        void showMessages(List<Message> messages);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void loadMessages(String devKey, String channelName, boolean forceUpdate);

        void saveMessage(@NonNull Message msg);
    }

}

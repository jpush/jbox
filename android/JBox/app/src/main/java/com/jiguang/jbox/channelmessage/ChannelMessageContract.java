package com.jiguang.jbox.channelmessage;

import android.support.annotation.NonNull;

import com.jiguang.jbox.BasePresenter;
import com.jiguang.jbox.BaseView;
import com.jiguang.jbox.data.Channel;

public interface ChannelMessageContract {

    interface View extends BaseView<Presenter> {

        void showMessagesUi(String channelName);
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadChannels(boolean forceUpdate);

        void openChannelMessages(@NonNull Channel channel);
    }

}

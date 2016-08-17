package com.jiguang.jbox.message;

import android.support.annotation.NonNull;

import com.jiguang.jbox.BasePresenter;
import com.jiguang.jbox.BaseView;
import com.jiguang.jbox.data.Channel;

import java.util.List;

/**
 * 控制主界面中的消息界面。
 */
public class MessageContract {

    interface Presenter extends BasePresenter {

        void loadChannels(boolean forceUpdate);

        void openChannelDetail(@NonNull Channel channel);
    }

    interface View extends BaseView<Presenter> {

        void showChannels(List<Channel> channels);

        void showChannelMessages(String channelName);
    }
}

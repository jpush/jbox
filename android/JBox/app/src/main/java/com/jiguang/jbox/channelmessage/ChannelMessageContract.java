package com.jiguang.jbox.channelmessage;

import android.support.annotation.NonNull;

import com.jiguang.jbox.BasePresenter;
import com.jiguang.jbox.BaseView;
import com.jiguang.jbox.data.Channel;

import java.util.List;

public interface ChannelMessageContract {

    interface View extends BaseView<Presenter> {

        void showMessagesUi(String channelName);

        void showChannels(List<Channel> channels);
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        // 加载目前绑定的 devKey 的所有 channel。
        void loadChannels(List<String> devKeys, boolean forceUpdate);

        void loadChannels(String devKey, boolean forceUpdate);

        void openChannelMessages(@NonNull Channel channel);

        void saveChannel(@NonNull Channel channel);

        void updateUnreadCount(@NonNull Channel channel);
    }

}

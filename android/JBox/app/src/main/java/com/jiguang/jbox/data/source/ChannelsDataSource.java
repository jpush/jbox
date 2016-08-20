package com.jiguang.jbox.data.source;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Channel;

import java.util.List;

public interface ChannelsDataSource {

    interface LoadChannelsCallback {

        void onChannelsLoaded(List<Channel> channels);

        void onDataNotAvailable();
    }

    interface GetChannelCallback {

        void onChannelLoaded(Channel channel);

        void onDataNotAvailable();
    }

    void getChannels(String devKey, @NonNull LoadChannelsCallback callback);

    // 读取所有订阅了的 Channel。
    void getSubscribedChannels(@NonNull LoadChannelsCallback callback);

    void refreshChannels();

}

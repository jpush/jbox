package com.jiguang.jbox.data.source;

import com.jiguang.jbox.data.Channel;

import java.util.List;

public interface ChannelDataSource {

    interface LoadChannelsCallback {
        void onChannelsLoaded(List<Channel> channels);

        void onDataNotAvailable();
    }

}

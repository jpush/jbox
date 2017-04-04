package com.jiguang.jbox.data.source;

import com.jiguang.jbox.data.model.Channel;

import java.util.List;

public interface ChannelDataSource {

    interface LoadChannelsCallback {
        void onChannelsLoaded(List<Channel> channels);

        void onDataNotAvailable();
    }
}

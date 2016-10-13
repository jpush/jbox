package com.jiguang.jbox.data.source;

import com.jiguang.jbox.data.Channel;

import java.util.List;

/**
 * Channel 数据源
 */
public interface ChannelDataSource {

    interface LoadChannelsCallback {

        void onChannelsLoaded(List<Channel> channels);

        void onDataNotAvailable();
    }

    void save(List<Channel> channels);

    void save(Channel channel);

    void load(String devKey, LoadChannelsCallback callback);

    void delete(String devKey);

}

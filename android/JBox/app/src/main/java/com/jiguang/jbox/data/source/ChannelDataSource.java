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

    interface LoadChannelsNameCallback {

        void onChannelsNameLoaded(List<String> channels);

        void onDataNotAvailable();
    }

    interface GetChannelCallback {

        void onChannelLoaded(Channel channel);

        void onDataNotAvailable();
    }

    /**
     * 根据订阅状态取得 Channel。
     *
     * @param isSubscribe 是否订阅。
     */
    void getChannels(String devKey, boolean isSubscribe, LoadChannelsCallback callback);

    /**
     * 取得所有的 Channel。
     */
    void getChannels(String devKey, LoadChannelsCallback callback);

    void getChannels(LoadChannelsCallback callback);

    void getChannels(boolean isSubscribe, LoadChannelsCallback callback);

    /**
     * 将订阅的 Channel 保存到本地数据库中。
     */
    void saveChannel(Channel channel);

    void saveChannels(List<Channel> channels);

    void updateChannel(Channel channel);

    void updateChannels(List<Channel> channels);

    void deleteChannels(String devKey);

    void deleteAllChannels();

    void refresh();

}

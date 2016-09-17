package com.jiguang.jbox.data.source;

import com.jiguang.jbox.data.Channel;

import java.util.List;

/**
 * Channel 数据源
 */
public interface ChannelDataSource {

    interface LoadChannelsCallback {

        void onMessagesLoaded(List<Channel> messages);

        void onDataNotAvailable();
    }

    interface GetChannelCallback {

        void onMessageLoaded(Channel msg);

        void onDataNotAvailable();
    }

    /**
     * 根据订阅状态取得 Channel。
     *
     * @param isSubscribe 是否订阅。
     */
    void getChannels(String devKey, boolean isSubscribe);

    /**
     * 取得所有的 Channel。
     */
    void getChannels(String devKey);

    /**
     * 将订阅的 Channel 保存到本地数据库中。
     *
     * @param channel
     */
    void saveChannel(Channel channel);

    void saveChannels(String devKey, List<Channel> channels);

}

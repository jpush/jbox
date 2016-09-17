package com.jiguang.jbox.data.source;

import com.jiguang.jbox.data.Channel;

public class ChannelRepository implements ChannelDataSource {

    private static ChannelRepository INSTANCE;

    public static ChannelRepository getInstance() {
        if (INSTANCE == null) {

        }
        return INSTANCE;
    }

    @Override
    public void getChannels(boolean isSubscribe) {

    }

    @Override
    public void getChannels() {

    }

    @Override
    public void saveChannel(Channel channel) {

    }
}

package com.jiguang.jbox.data.source.remote;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.source.ChannelsDataSource;

import java.util.LinkedHashMap;
import java.util.Map;

public class ChannelRemoteDataSource implements ChannelsDataSource {

    private static ChannelRemoteDataSource INSTANCE;

    private final static Map<String, Channel> CHANNEL_SERVICE_DATA;

    static {
        CHANNEL_SERVICE_DATA = new LinkedHashMap<String, Channel>();
    }

    public static ChannelRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChannelRemoteDataSource();
        }
        return INSTANCE;
    }

    private ChannelRemoteDataSource() {}

    @Override
    public void saveChannel(@NonNull Channel channel) {
        CHANNEL_SERVICE_DATA.put(channel.getName(), channel);
    }

    @Override
    public void getChannels(@NonNull LoadChannelsCallback callback) {

    }

    @Override
    public void getChannel(@NonNull String name, @NonNull GetChannelCallback callback) {
        Channel channel = CHANNEL_SERVICE_DATA.get(name);

        // 从网络获取数据。
    }

    @Override
    public void refreshChannels() {

    }

    @Override
    public void deleteChannel(@NonNull String name) {
        CHANNEL_SERVICE_DATA.remove(name);
    }

    @Override
    public void deleteAllChannels() {
        CHANNEL_SERVICE_DATA.clear();
    }
}

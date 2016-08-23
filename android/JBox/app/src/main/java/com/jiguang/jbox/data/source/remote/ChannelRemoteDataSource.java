package com.jiguang.jbox.data.source.remote;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.source.ChannelsDataSource;
import com.jiguang.jbox.util.HttpUtil;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ChannelRemoteDataSource implements ChannelsDataSource {

    private static ChannelRemoteDataSource INSTANCE;

    public static ChannelRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChannelRemoteDataSource();
        }
        return INSTANCE;
    }

    private ChannelRemoteDataSource() {
    }

    @Override
    public void getChannels(@NonNull String devKey, @NonNull LoadChannelsCallback callback) {
        checkNotNull(devKey);
        checkNotNull(callback);

        List<Channel> channels = HttpUtil.getInstance().requestChannels(devKey);
        callback.onChannelsLoaded(channels);
    }

    @Override
    public void getSubscribedChannels(@NonNull LoadChannelsCallback callback) {

    }

    @Override
    public void refreshChannels() {

    }

    @Override
    public void saveChannel(@NonNull Channel channel) {

    }

}

package com.jiguang.jbox.data.source.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.source.ChannelsDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

public class ChannelLocalDataSource implements ChannelsDataSource {

    private static ChannelLocalDataSource INSTANCE;

    private ChannelDBHelper mDBHelper;

    private ChannelLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDBHelper = new ChannelDBHelper(context);
    }

    public static ChannelLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ChannelLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void saveChannel(@NonNull Channel channel) {

    }

    @Override
    public void getChannels(String devKey, @NonNull LoadChannelsCallback callback) {

    }

    @Override
    public void getSubscribedChannels(String devKey, @NonNull LoadChannelsCallback callback) {

    }

    @Override
    public void getChannel(@NonNull String name, @NonNull GetChannelCallback callback) {

    }

    @Override
    public void refreshChannels() {

    }

    @Override
    public void deleteChannel(@NonNull String name) {

    }

    @Override
    public void deleteAllChannels() {

    }
}

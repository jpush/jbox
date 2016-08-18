package com.jiguang.jbox.data.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jiguang.jbox.data.source.ChannelsDataSource;
import com.jiguang.jbox.util.HttpUtil;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ChannelRemoteDataSource implements ChannelsDataSource {

    private static ChannelRemoteDataSource INSTANCE;

    private Context mContext;

    public static ChannelRemoteDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ChannelRemoteDataSource(context);
        }
        return INSTANCE;
    }

    private ChannelRemoteDataSource(Context context) {
        mContext = context;
    }

    @Override
    public void getChannels(@NonNull LoadChannelsCallback callback) {

    }

    @Override
    public void getChannels(@NonNull String devKey, @NonNull LoadChannelsCallback callback) {
        checkNotNull(devKey);
        checkNotNull(callback);

        Map<String, List> channels = HttpUtil.getInstance(mContext).requestChannels(devKey);
    }

    @Override
    public void getSubscribedChannels(@NonNull LoadChannelsCallback callback) {

    }

    @Override
    public void refreshChannels() {

    }

    @Override
    public void subscribeChannel(@NonNull String name) {

    }

    @Override
    public void unSubscribeChannels() {

    }

}

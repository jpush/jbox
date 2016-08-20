package com.jiguang.jbox.data.source;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.source.local.ChannelLocalDataSource;
import com.jiguang.jbox.data.source.remote.ChannelRemoteDataSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

import static com.google.common.base.Preconditions.checkNotNull;

public class ChannelsRepository implements ChannelsDataSource {

    private static ChannelsRepository INSTANCE = null;

    private final ChannelsDataSource mChannelsRemoteDataSource;

    private final ChannelsDataSource mChannelsLocalDataSource;

    Map<String, List<Channel>> mCachedChannels; // key: devKey.

    boolean mCacheIsDirty = false;

    private ChannelsRepository(@NonNull ChannelsDataSource channelsRemoteDataSource,
                               @NonNull ChannelsDataSource channelsLocalDataSource) {
        mChannelsRemoteDataSource = channelsRemoteDataSource;
        mChannelsLocalDataSource = channelsLocalDataSource;
    }

    public static ChannelsRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChannelsRepository(ChannelRemoteDataSource.getInstance(),
                    ChannelLocalDataSource.getInstance());
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * 获取指定 devKey 下的所有 Channel。
     * @param devKey
     * @param callback
     */
    @Override
    public void getChannels(final String devKey, @NonNull final LoadChannelsCallback callback) {
        checkNotNull(callback);

        // 如果缓存可用，就直接从缓存中获取数据。
        if (mCachedChannels != null && !mCacheIsDirty) {
            callback.onChannelsLoaded(mCachedChannels.get(devKey));
            return;
        }

        if (mCacheIsDirty) {
            // 如果缓存中数据过期，就从服务器重新获取。
            getChannelsFromRemoteDataSource(devKey, callback);
        } else {
            // 从本地数据库读取。如果不行，就从服务器查询。
            mChannelsLocalDataSource.getChannels(devKey, new LoadChannelsCallback() {
                @Override
                public void onChannelsLoaded(List<Channel> channels) {
                    refreshCache(channels);
                    callback.onChannelsLoaded(new ArrayList<Channel>(mCachedChannels.get(devKey)));
                }

                @Override
                public void onDataNotAvailable() {
                    getChannelsFromRemoteDataSource(devKey, callback);
                }
            });
        }
    }

    /**
     * 获取所有 devKey 已订阅的 channel。
     * @param callback
     */
    @Override
    public void getSubscribedChannels(@NonNull LoadChannelsCallback callback) {
        checkNotNull(callback);

    }

    @Override
    public void refreshChannels() {
        mCacheIsDirty = true;
    }

    @Override
    public void subscribeChannel(@NonNull String devKey, @NonNull String channelName) {
        List<Channel> channels = mCachedChannels.get(devKey);
        for (Channel channel : channels) {
            if (channel.getName().equals(channelName)) {

            }
        }
    }

    @Override
    public void unSubscribeChannel(@NonNull String devKey, @NonNull String channelName) {

    }

    private void getChannelsFromRemoteDataSource(final LoadChannelsCallback callback) {
        mChannelsRemoteDataSource.getChannels(new LoadChannelsCallback() {
            @Override
            public void onChannelsLoaded(List<Channel> channels) {

            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    private void getChannelsFromRemoteDataSource(String devKey, @NonNull final LoadChannelsCallback callback) {
        mChannelsRemoteDataSource.getChannels(devKey, new LoadChannelsCallback() {
            @Override
            public void onChannelsLoaded(List<Channel> channels) {
                refreshCache(channels);
                refreshLocalDataSource(channels);
                callback.onChannelsLoaded(new ArrayList<Channel>(mCachedChannels.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(@NonNull List<Channel> list) {
        checkNotNull(list);
        if (!list.isEmpty()) {
            String devKey = list.get(0).getDeveloper().getDevKey();
            if (mCachedChannels.containsKey(devKey)) {
                mCachedChannels.get(devKey).addAll(list);
            } else {
                mCachedChannels.put(devKey, list);
            }
        }
    }

    private void refreshLocalDataSource(@NonNull List<Channel> channels) {
        checkNotNull(channels);
        mChannelsLocalDataSource.deleteAllChannels();
        for (Channel channel : channels) {
            mChannelsLocalDataSource.saveChannel(channel);
        }
    }

}

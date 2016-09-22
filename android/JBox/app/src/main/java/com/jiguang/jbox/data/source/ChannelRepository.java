package com.jiguang.jbox.data.source;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ChannelRepository implements ChannelDataSource {

    private static ChannelRepository INSTANCE;

    private ChannelDataSource mLocalDataSource;

    private Map<String, List<Channel>> mCacheChannels;

    private boolean mCacheIsDirty = false;  // 是否需要更新缓存。

    private ChannelRepository(ChannelDataSource dataSource) {
        mLocalDataSource = dataSource;
    }

    public static ChannelRepository getInstance(ChannelDataSource localDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new ChannelRepository(localDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getChannels(String devKey, boolean isSubscribe, final LoadChannelsCallback callback) {
        if (mCacheChannels != null && !mCacheIsDirty && mCacheChannels.containsKey(devKey)) {
            List<Channel> channels = new ArrayList<>();

            for (Channel channel : mCacheChannels.get(devKey)) {
                if (channel.isSubscribe() == isSubscribe) {
                    channels.add(channel);
                }
            }

            callback.onChannelsLoaded(channels);
            return;
        }

        if (mCacheIsDirty) {    // 从本地数据库中获取数据。
            mLocalDataSource.getChannels(devKey, isSubscribe, new LoadChannelsCallback() {
                @Override
                public void onChannelsLoaded(List<Channel> channels) {
                    // 更新缓存。
                    refreshCache(channels);
                    callback.onChannelsLoaded(channels);
                }

                @Override
                public void onDataNotAvailable() {
                    callback.onDataNotAvailable();
                }
            });
        }
    }

    @Override
    public void getChannels(String devKey, final LoadChannelsCallback callback) {
        if (mCacheChannels != null && !mCacheIsDirty && mCacheChannels.containsKey(devKey)) {
            callback.onChannelsLoaded(mCacheChannels.get(devKey));
            return;
        }

        if (mCacheIsDirty) {
            mLocalDataSource.getChannels(devKey, new LoadChannelsCallback() {
                @Override
                public void onChannelsLoaded(List<Channel> channels) {
                    refreshCache(channels);
                    callback.onChannelsLoaded(channels);
                }

                @Override
                public void onDataNotAvailable() {
                    callback.onDataNotAvailable();  // 如果查询结果为空,会走这个回调。
                }
            });
        }
    }

    @Override
    public void getChannels(LoadChannelsCallback callback) {
        mLocalDataSource.getChannels(callback);
    }

    @Override
    public void getChannels(boolean isSubscribe, LoadChannelsCallback callback) {
        mLocalDataSource.getChannels(isSubscribe, callback);
    }

    @Override
    public void saveChannel(@NonNull Channel channel) {
        checkNotNull(channel);
        mLocalDataSource.saveChannel(channel);
    }

    /**
     * 要保证 channels 中的所有 channel 有相同的 dev_key。
     * @param channels
     */
    @Override
    public void saveChannels(@NonNull List<Channel> channels) {
        checkNotNull(channels);

        if (mCacheChannels == null) {
            mCacheChannels = new HashMap<>();
        }

        if (!channels.isEmpty()) {
            String devKey = channels.get(0).getDevKey();
            if (mCacheChannels.containsKey(devKey)) {
                mCacheChannels.get(devKey).addAll(channels);
            }
        }
        mLocalDataSource.saveChannels(channels);
    }

    @Override
    public void updateChannel(@NonNull Channel channel) {
        checkNotNull(channel);
        mLocalDataSource.updateChannel(channel);
    }

    @Override
    public void updateChannels(@NonNull List<Channel> channels) {
        checkNotNull(channels);
        mLocalDataSource.updateChannels(channels);
    }

    @Override
    public void refresh() {
        mCacheIsDirty = true;
    }

    private void refreshCache(List<Channel> channels) {
        if (channels == null || channels.isEmpty()) {
            return;
        }

        if (mCacheChannels == null) {
            mCacheChannels = new HashMap<>();
        }

        String devKey = channels.get(0).getDevKey();
        mCacheChannels.put(devKey, channels);
        mCacheIsDirty = false;
    }

}

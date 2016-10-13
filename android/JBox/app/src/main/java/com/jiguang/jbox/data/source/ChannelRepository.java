package com.jiguang.jbox.data.source;

import com.jiguang.jbox.data.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelRepository implements ChannelDataSource {

    private static ChannelRepository INSTANCE;

    private ChannelDataSource mLocalDataSource;

    private Map<String, List<Channel>> mCacheChannels;  // key: devKey, value: channel list.

    private boolean mIsNeedRefresh = false;  // 是否需要更新缓存。

    private ChannelRepository(ChannelDataSource dataSource) {
        mLocalDataSource = dataSource;
        if (mCacheChannels == null) {
            mCacheChannels = new HashMap<>();
        }
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
    public void save(List<Channel> channels) {
        if (channels == null || channels.isEmpty()) {
            return;
        }

        String devKey = channels.get(0).devKey;

        if (mCacheChannels.containsKey(devKey)) {
            mCacheChannels.remove(devKey);
        }
        mCacheChannels.put(devKey, channels);

        mLocalDataSource.save(channels);
    }

    @Override
    public void save(Channel channel) {
        if (channel == null) {
            return;
        }

        String devKey = channel.devKey;

        if (mCacheChannels.containsKey(devKey)) {
            mCacheChannels.get(devKey).add(channel);
        } else {
            List<Channel> channels = new ArrayList<>();
            channels.add(channel);
            mCacheChannels.put(devKey, channels);
        }

        mLocalDataSource.save(channel);
    }

    @Override
    public void load(final String devKey, final LoadChannelsCallback callback) {
        if (mCacheChannels.containsKey(devKey) && !mIsNeedRefresh) {
            callback.onChannelsLoaded(mCacheChannels.get(devKey));
            return;
        }

        mLocalDataSource.load(devKey, new LoadChannelsCallback() {
            @Override
            public void onChannelsLoaded(List<Channel> channels) {
                // 更新缓存。
                if (mCacheChannels.containsKey(devKey)) {
                    mCacheChannels.remove(devKey);
                }
                mCacheChannels.put(devKey, channels);

                callback.onChannelsLoaded(channels);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void delete(String devKey) {
        mCacheChannels.remove(devKey);
        mLocalDataSource.delete(devKey);
    }

    public void needRefresh(boolean isNeedRefresh) {
        mIsNeedRefresh = isNeedRefresh;
    }
}

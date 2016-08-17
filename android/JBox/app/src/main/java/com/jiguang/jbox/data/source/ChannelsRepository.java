package com.jiguang.jbox.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jiguang.jbox.data.Channel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ChannelsRepository implements ChannelsDataSource {

    private static ChannelsRepository INSTANCE = null;

    private final ChannelsDataSource mChannelsRemoteDataSource;

    private final ChannelsDataSource mChannelsLocalDataSource;

    Map<String, Channel> mCachedChannels;

    boolean mCacheIsDirty = false;

    private ChannelsRepository(@NonNull ChannelsDataSource channelsRemoteDataSource,
                               @NonNull ChannelsDataSource channelsLocalDataSource) {
        mChannelsRemoteDataSource = channelsRemoteDataSource;
        mChannelsLocalDataSource = channelsLocalDataSource;
    }

    public static ChannelsRepository getInstance(ChannelsDataSource channelsRemoteDataSource,
                                                 ChannelsDataSource channelsLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new ChannelsRepository(channelsRemoteDataSource, channelsLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void saveChannel(@NonNull Channel channel) {
        checkNotNull(channel);
        mChannelsRemoteDataSource.saveChannel(channel);
        mChannelsLocalDataSource.saveChannel(channel);

        if (mCachedChannels == null) {
            mCachedChannels = new LinkedHashMap<String, Channel>();
        }
        mCachedChannels.put(channel.getName(), channel);
    }

    @Override
    public void getChannels(final String devKey, @NonNull final LoadChannelsCallback callback) {
        checkNotNull(callback);

        // 如果缓存可用，就直接从缓存中获取数据。
        if (mCachedChannels != null && !mCacheIsDirty) {
            callback.onChannelsLoaded(new ArrayList<Channel>(mCachedChannels.values()));
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
                    callback.onChannelsLoaded(new ArrayList<Channel>(mCachedChannels.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getChannelsFromRemoteDataSource(devKey, callback);
                }
            });
        }
    }

    @Override
    public void getSubscribedChannels(String devKey, @NonNull LoadChannelsCallback callback) {

    }

    @Override
    public void getChannel(@NonNull String name, @NonNull final GetChannelCallback callback) {
        checkNotNull(name);
        checkNotNull(callback);

        Channel cacheChannel = getChannelWithName(name);

        if (cacheChannel != null) {
            callback.onChannelLoaded(cacheChannel);
            return;
        }

        // 如果缓存中不存在。
        mChannelsLocalDataSource.getChannel(name, new GetChannelCallback() {
            @Override
            public void onChannelLoaded(Channel channel) {
                callback.onChannelLoaded(channel);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void refreshChannels() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteChannel(@NonNull String channelName) {
        mChannelsLocalDataSource.deleteChannel(checkNotNull(channelName));
        mChannelsRemoteDataSource.deleteChannel(checkNotNull(channelName));

        mCachedChannels.remove(channelName);
    }

    @Override
    public void deleteAllChannels() {
        mChannelsRemoteDataSource.deleteAllChannels();
        mChannelsLocalDataSource.deleteAllChannels();

        if (mCachedChannels == null) {
            mCachedChannels = new LinkedHashMap<String, Channel>();
        }
        mCachedChannels.clear();
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

    private void refreshCache(List<Channel> channels) {
        if (mCachedChannels == null) {
            mCachedChannels = new LinkedHashMap<String, Channel>();
        }
        mCachedChannels.clear();
        for (Channel channel : channels) {
            mCachedChannels.put(channel.getName(), channel);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Channel> channels) {
        mChannelsLocalDataSource.deleteAllChannels();
        for (Channel channel : channels) {
            mChannelsLocalDataSource.saveChannel(channel);
        }
    }

    @Nullable
    private Channel getChannelWithName(@NonNull String name) {
        checkNotNull(name);
        if (mCachedChannels == null || mCachedChannels.isEmpty()) {
            return null;
        } else {
            return mCachedChannels.get(name);
        }
    }
}

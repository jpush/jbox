package com.jiguang.jbox.data.source;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Developer;

import java.util.List;

public class DeveloperRepository implements DeveloperDataSource {

    private static DeveloperRepository INSTANCE;

    private DeveloperDataSource mLocalDataSource;
    private DeveloperDataSource mRemoteDataSource;

    List<Developer> mCachedDevelopers;

    boolean mCacheIsDirty = false;

    private DeveloperRepository(DeveloperDataSource localDataSource,
                                DeveloperDataSource remoteDataSource) {
        mLocalDataSource = localDataSource;
        mRemoteDataSource = remoteDataSource;
    }

    public static DeveloperRepository getInstance(DeveloperDataSource localDataSource,
                                                  DeveloperDataSource remoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new DeveloperRepository(localDataSource, remoteDataSource);
        }
        return INSTANCE;
    }

    @Override
    public void getDeveloper(@NonNull final String devKey, @NonNull final LoadDevCallback callback) {
        if (mCachedDevelopers != null && !mCacheIsDirty) {
            for (Developer dev : mCachedDevelopers) {
                if (dev.getDevKey().equals(devKey)) {
                    callback.onDevLoaded(dev);
                    return;
                }
            }
            return;
        }

        if (mCacheIsDirty) {
            // 先从本地获取，如果没有再从服务器获取。
            mLocalDataSource.getDeveloper(devKey, new LoadDevCallback() {
                @Override
                public void onDevLoaded(Developer dev) {
                    callback.onDevLoaded(dev);
                }

                @Override
                public void onDataNotAvailable() {
                    mRemoteDataSource.getDeveloper(devKey, callback);
                }
            });
        }
    }

    @Override
    public void getDevelopers(@NonNull final LoadDevsCallback callback) {
        if (mCachedDevelopers != null && !mCacheIsDirty) {
            callback.onDevsLoaded(mCachedDevelopers);
            return;
        }

        if (mCacheIsDirty) {
            mLocalDataSource.getDevelopers(new LoadDevsCallback() {
                @Override
                public void onDevsLoaded(List<Developer> devList) {
                    refreshCache(devList);
                    callback.onDevsLoaded(devList);
                }

                @Override
                public void onDataNotAvailable() {
                    callback.onDataNotAvailable();
                }
            });
        }
    }

    @Override
    public void saveDeveloper(@NonNull Developer dev) {
        mCachedDevelopers.add(dev);
        mLocalDataSource.saveDeveloper(dev);
    }

    @Override
    public void updateDeveloper(@NonNull Developer dev) {
        mLocalDataSource.updateDeveloper(dev);
    }

    @Override
    public void refresh() {
        mCacheIsDirty = true;
    }

    private void refreshCache(List<Developer> developers) {
        if (mCachedDevelopers == null) {
            mCachedDevelopers = developers;
        } else {
            mCachedDevelopers.clear();
            mCachedDevelopers = developers;
        }
        mCacheIsDirty = false;
    }

}

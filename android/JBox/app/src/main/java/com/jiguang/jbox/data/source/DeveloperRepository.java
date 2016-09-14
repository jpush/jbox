package com.jiguang.jbox.data.source;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Developer;

public class DeveloperRepository implements DeveloperDataSource {

    private static DeveloperRepository INSTANCE;

    private DeveloperDataSource mLocalDataSource;
    private DeveloperDataSource mRemoteDataSource;

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
        // 先从本地查询，如果没有再从服务器获取。
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

    @Override
    public void saveDeveloper(@NonNull Developer dev) {
        mLocalDataSource.saveDeveloper(dev);
    }



}

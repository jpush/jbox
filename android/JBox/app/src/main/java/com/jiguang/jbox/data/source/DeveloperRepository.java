package com.jiguang.jbox.data.source;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Developer;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DeveloperRepository implements DeveloperDataSource {

    private static DeveloperRepository INSTANCE;

    private DeveloperDataSource mLocalDataSource;
    private DeveloperDataSource mRemoteDataSource;

    List<Developer> mCachedDevs;

    private boolean mIsNeedRefresh = false;

    private DeveloperRepository(DeveloperDataSource localDataSource,
                                DeveloperDataSource remoteDataSource) {
        mLocalDataSource = localDataSource;
        mRemoteDataSource = remoteDataSource;

        mCachedDevs = new ArrayList<>();
    }

    public static DeveloperRepository getInstance(DeveloperDataSource localDataSource,
                                                  DeveloperDataSource remoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new DeveloperRepository(localDataSource, remoteDataSource);
        }
        return INSTANCE;
    }


    @Override
    public void load(@NonNull String devKey, @NonNull final LoadDevCallback callback) {
        checkNotNull(devKey);
        checkNotNull(callback);

        if (!mIsNeedRefresh) {
            for (int i = 0; i < mCachedDevs.size(); i++) {
                Developer dev = mCachedDevs.get(i);
                if (dev.key.equals(devKey)) {
                    callback.onDevLoaded(dev);
                    return;
                }
            }
        } else {
            mLocalDataSource.load(devKey, new LoadDevCallback() {
                @Override
                public void onDevLoaded(Developer dev) {

                    callback.onDevLoaded(dev);
                }

                @Override
                public void onDataNotAvailable() {
                    callback.onDataNotAvailable();
                }
            });
        }
    }

    @Override
    public void load(@NonNull final LoadDevsCallback callback) {
        checkNotNull(callback);

        if (!mIsNeedRefresh) {
            callback.onDevsLoaded(mCachedDevs);
        } else {
            mLocalDataSource.load(new LoadDevsCallback() {
                @Override
                public void onDevsLoaded(List<Developer> devList) {
                    mCachedDevs = devList;

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
    public void save(@NonNull Developer dev) {
        checkNotNull(dev);

        mCachedDevs.add(dev);
        mLocalDataSource.save(dev);
    }

    @Override
    public void update(@NonNull Developer dev) {
        checkNotNull(dev);

        if (mCachedDevs.contains(dev)) {
            mCachedDevs.remove(dev);
        }

        mCachedDevs.add(dev);
        mLocalDataSource.update(dev);
    }

    @Override
    public void delete(@NonNull String devKey) {
        checkNotNull(devKey);

        for (int i = 0; i < mCachedDevs.size(); i++) {
            if (mCachedDevs.get(i).key.equals(devKey)) {
                mCachedDevs.remove(i);
            }
        }

        mLocalDataSource.delete(devKey);
    }

}

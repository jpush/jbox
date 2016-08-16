package com.jiguang.jbox.data.source;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Developer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class DeveloperRepository implements DeveloperDataSource {

    private static DeveloperRepository INSTANCE = null;

    private final DeveloperDataSource mDevelopersRemoteDataSource;

    private final DeveloperDataSource mDevelopersLocalDataSource;

    Map<String, Developer> mCachedDevelopers;

    boolean mCacheIsDirty = false;

    private DeveloperRepository(@NonNull DeveloperDataSource remoteDataSource,
                                @NonNull DeveloperDataSource localDataSource) {
        mDevelopersRemoteDataSource = checkNotNull(remoteDataSource);
        mDevelopersLocalDataSource = checkNotNull(localDataSource);
    }

    public static DeveloperRepository getInstance(DeveloperDataSource remoteDataSource,
                                                  DeveloperDataSource localDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new DeveloperRepository(remoteDataSource, localDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getDevelopers(@NonNull final LoadDevelopersCallback callback) {
        checkNotNull(callback);

        if (mCachedDevelopers != null && !mCacheIsDirty) {
            callback.onDevelopersLoaded(new ArrayList<>(mCachedDevelopers.values()));
            return;
        }

        if (mCacheIsDirty) {
            getDevelopersFromRemoteDataSource(callback);
        } else {
            mDevelopersLocalDataSource.getDevelopers(new LoadDevelopersCallback() {
                @Override
                public void onDevelopersLoaded(List<Developer> developers) {
                    refreshDevelopers();
                    callback.onDevelopersLoaded(new ArrayList<>(mCachedDevelopers.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getDevelopersFromRemoteDataSource(callback);
                }
            });
        }
    }

    @Override
    public void getDeveloper(@NonNull String devKey, @NonNull final GetDeveloperCallback callback) {
        checkNotNull(devKey);
        checkNotNull(callback);

        Developer cachedDev = getDeveloperByKey(devKey);

        if (cachedDev != null) {
            callback.onDeveloperLoaded(cachedDev);
            return;
        }

        mDevelopersLocalDataSource.getDeveloper(devKey, new GetDeveloperCallback() {
            @Override
            public void onDeveloperLoaded(Developer developer) {
                callback.onDeveloperLoaded(developer);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveDeveloper(@NonNull Developer dev) {
        checkNotNull(dev);
        mDevelopersLocalDataSource.saveDeveloper(dev);
        mDevelopersRemoteDataSource.saveDeveloper(dev);

        if (mCachedDevelopers == null) {
            mCachedDevelopers = new LinkedHashMap<>();
        }
        mCachedDevelopers.put(dev.getDevKey(), dev);
    }

    @Override
    public void refreshDevelopers() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllDevelopers() {
        mDevelopersRemoteDataSource.deleteAllDevelopers();
        mDevelopersLocalDataSource.deleteAllDevelopers();

        mCachedDevelopers.clear();
    }

    @Override
    public void deleteDeveloper(@NonNull String devKey) {
        mDevelopersRemoteDataSource.deleteDeveloper(devKey);
        mDevelopersLocalDataSource.deleteDeveloper(devKey);

        mCachedDevelopers.remove(devKey);
    }

    public boolean isEmpty() {
        return mCachedDevelopers == null || mCachedDevelopers.isEmpty();
    }

    private void getDevelopersFromRemoteDataSource(@NonNull final LoadDevelopersCallback callback) {
        mDevelopersRemoteDataSource.getDevelopers(new LoadDevelopersCallback() {
            @Override
            public void onDevelopersLoaded(List<Developer> developers) {
                refreshCache(developers);
                refreshLocalDataSource(developers);
                callback.onDevelopersLoaded(new ArrayList<>(mCachedDevelopers.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Developer> developers) {
        if (mCachedDevelopers == null) {
            mCachedDevelopers = new LinkedHashMap<>();
        }
        mCachedDevelopers.clear();
        for (Developer dev : developers) {
            mCachedDevelopers.put(dev.getDevKey(), dev);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Developer> developers) {
        mDevelopersLocalDataSource.deleteAllDevelopers();
        for (Developer dev : developers) {
            mDevelopersLocalDataSource.saveDeveloper(dev);
        }
    }

    private Developer getDeveloperByKey(@NonNull String devKey) {
        checkNotNull(devKey);

        if (mCachedDevelopers == null || mCachedDevelopers.isEmpty()) {
            return null;
        }
        return mCachedDevelopers.get(devKey);
    }
}

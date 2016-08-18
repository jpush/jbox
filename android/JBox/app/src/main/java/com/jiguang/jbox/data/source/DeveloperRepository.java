package com.jiguang.jbox.data.source;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.source.local.DeveloperLocalDataSource;
import com.jiguang.jbox.util.LogUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class DeveloperRepository implements DeveloperDataSource {

    private static DeveloperRepository INSTANCE = null;

    private final DeveloperDataSource mDevelopersLocalDataSource;

    Map<String, Developer> mCachedDevelopers;

    boolean mCacheIsDirty = false;

    private DeveloperRepository(@NonNull DeveloperDataSource localDataSource) {
        mDevelopersLocalDataSource = checkNotNull(localDataSource);
    }

    /**
     * 暂时没有远程接口，只能从本地获取 dev 历史数据。
     */
    public static DeveloperRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DeveloperRepository(DeveloperLocalDataSource.getInstance(context));
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

        if (mCacheIsDirty) {    // 需要更新。
            mDevelopersLocalDataSource.getDevelopers(new LoadDevelopersCallback() {
                @Override
                public void onDevelopersLoaded(List<Developer> developers) {
                    refreshDevelopers();
                    callback.onDevelopersLoaded(new ArrayList<>(mCachedDevelopers.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    LogUtil.LOGI("DeveloperRepository", "No data available.");
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
        mDevelopersLocalDataSource.deleteAllDevelopers();

        mCachedDevelopers.clear();
    }

    @Override
    public void deleteDeveloper(@NonNull String devKey) {
        mDevelopersLocalDataSource.deleteDeveloper(devKey);

        mCachedDevelopers.remove(devKey);
    }

    public boolean isEmpty() {
        return mCachedDevelopers == null || mCachedDevelopers.isEmpty();
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

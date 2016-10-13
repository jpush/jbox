package com.jiguang.jbox.data.source.remote;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.source.DeveloperDataSource;
import com.jiguang.jbox.util.HttpUtil;

import static com.google.common.base.Preconditions.checkNotNull;

public class DeveloperRemoteDataSource implements DeveloperDataSource {

    private static DeveloperRemoteDataSource INSTANCE;

    public static DeveloperRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DeveloperRemoteDataSource();
        }
        return INSTANCE;
    }

    private DeveloperRemoteDataSource() {}


    @Override
    public void load(@NonNull String devKey, @NonNull LoadDevCallback callback) {
        checkNotNull(devKey);
        HttpUtil.getInstance().requestDeveloper(devKey, callback);
    }

    @Override
    public void load(@NonNull LoadDevsCallback callback) {

    }

    @Override
    public void save(@NonNull Developer dev) {

    }

    @Override
    public void update(@NonNull Developer dev) {

    }

    @Override
    public void delete(@NonNull String devKey) {

    }
}

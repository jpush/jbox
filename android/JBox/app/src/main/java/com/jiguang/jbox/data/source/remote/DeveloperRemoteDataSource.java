package com.jiguang.jbox.data.source.remote;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.source.DeveloperDataSource;

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
    public void getDevelopers(@NonNull LoadDevelopersCallback callback) {

    }

    @Override
    public void getDeveloper(@NonNull String devKey, @NonNull GetDeveloperCallback callback) {

    }

    @Override
    public void saveDeveloper(@NonNull Developer dev) {

    }

    @Override
    public void refreshDevelopers() {

    }

    @Override
    public void deleteAllDevelopers() {

    }

    @Override
    public void deleteDeveloper(@NonNull String devKey) {

    }
}

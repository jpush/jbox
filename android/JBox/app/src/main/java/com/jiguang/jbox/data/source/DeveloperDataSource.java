package com.jiguang.jbox.data.source;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Developer;

import java.util.List;

public interface DeveloperDataSource {

    interface LoadDevelopersCallback {

        void onDevelopersLoaded(List<Developer> developers);

        void onDataNotAvailable();
    }

    interface GetDeveloperCallback {

        void onDeveloperLoaded(Developer developer);

        void onDataNotAvailable();
    }

    void getDevelopers(@NonNull LoadDevelopersCallback callback);

    void getDeveloper(@NonNull String devKey, @NonNull GetDeveloperCallback callback);

    void saveDeveloper(@NonNull Developer dev);

    void refreshDevelopers();

    void deleteAllDevelopers();

    void deleteDeveloper(@NonNull String devKey);

}

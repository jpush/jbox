package com.jiguang.jbox.data.source;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Developer;

import java.util.List;

public interface DeveloperDataSource {

    interface LoadDevCallback {

        void onDevLoaded(Developer dev);

        void onDataNotAvailable();
    }

    interface LoadDevsCallback {

        void onDevsLoaded(List<Developer> devList);

        void onDataNotAvailable();
    }

    void getDeveloper(@NonNull String devKey, @NonNull LoadDevCallback callback);

    void getDevelopers(@NonNull LoadDevsCallback callback);

    void saveDeveloper(@NonNull Developer dev);

    void updateDeveloper(@NonNull Developer dev);

    void refresh();
}

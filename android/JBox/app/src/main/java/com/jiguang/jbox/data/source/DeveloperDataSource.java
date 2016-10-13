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

    void load(@NonNull String devKey, @NonNull LoadDevCallback callback);

    void load(@NonNull LoadDevsCallback callback);

    void save(@NonNull Developer dev);

    void update(@NonNull Developer dev);

    void delete(@NonNull String devKey);
}

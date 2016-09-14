package com.jiguang.jbox.data.source;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Developer;

public interface DeveloperDataSource {

    interface LoadDevCallback {

        void onDevLoaded(Developer dev);

        void onDataNotAvailable();
    }

    void getDeveloper(@NonNull String devKey, @NonNull LoadDevCallback callback);

    void saveDeveloper(@NonNull Developer dev);
}

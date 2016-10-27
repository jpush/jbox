package com.jiguang.jbox.data.source;

import com.jiguang.jbox.data.Developer;

public interface DeveloperDataSource {

    interface LoadDevCallback {
        void onDevLoaded(Developer dev);

        void onDataNotAvailable();
    }
}

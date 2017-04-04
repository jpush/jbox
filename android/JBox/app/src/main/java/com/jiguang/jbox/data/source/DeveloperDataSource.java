package com.jiguang.jbox.data.source;

import com.jiguang.jbox.data.model.Developer;

public interface DeveloperDataSource {

    interface LoadDevCallback {
        void onDevLoaded(Developer dev);

        void onDataNotAvailable();
    }
}

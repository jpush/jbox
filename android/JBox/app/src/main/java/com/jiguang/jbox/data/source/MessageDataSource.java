package com.jiguang.jbox.data.source;


import android.support.annotation.NonNull;

import com.jiguang.jbox.data.model.Message;

import java.util.List;

public interface MessageDataSource {

    interface LoadMessagesCallback {

        void onLoadMessages(List<Message> messages);

        void onDataNotAvailable();
    }

    void loadMessages(@NonNull String devKey, @NonNull String channelName, int offSet,
                      int limit, @NonNull LoadMessagesCallback callback);

    void clearMessages(@NonNull String devKey, @NonNull String channelName);

}

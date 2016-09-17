package com.jiguang.jbox.data.source;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Message;

import java.util.List;

public interface MessageDataSource {

    interface LoadMessagesCallback {

        void onMessagesLoaded(List<Message> messages);

        void onDataNotAvailable();
    }

    interface GetMessageCallback {

        void onMessageLoaded(Message msg);

        void onDataNotAvailable();
    }

    void getMessages(@NonNull String devKey, @NonNull String channelName,
                     @NonNull LoadMessagesCallback callback);

    void saveMessage(@NonNull Message message);

    void refreshMessages(@NonNull String devKey, @NonNull String channelName);

}

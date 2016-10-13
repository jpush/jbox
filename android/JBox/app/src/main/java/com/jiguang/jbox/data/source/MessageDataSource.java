package com.jiguang.jbox.data.source;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Message;

import java.util.List;

public interface MessageDataSource {

    interface LoadMessagesCallback {

        void onMessagesLoaded(List<Message> messages);

        void onDataNotAvailable();
    }

    void load(@NonNull String devKey, @NonNull String channelName,
                     @NonNull LoadMessagesCallback callback);

    void save(@NonNull Message message);

    void delete(@NonNull String devKey, @NonNull String channelName);

}

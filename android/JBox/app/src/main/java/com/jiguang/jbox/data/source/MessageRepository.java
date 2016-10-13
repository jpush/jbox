package com.jiguang.jbox.data.source;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class MessageRepository implements MessageDataSource {

    private static MessageRepository INSTANCE = null;

    private MessageDataSource mLocalDataSource;

    Map<String, List<Message>> mCachedMessages; // key: devKey_channelName, value: message list.

    private boolean isCacheDirty = false;

    private MessageRepository(MessageDataSource localDataSource) {
        mLocalDataSource = localDataSource;
        mCachedMessages = new HashMap<>();
    }

    public static MessageRepository getInstance(MessageDataSource localDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new MessageRepository(localDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public void load(@NonNull String devKey, @NonNull String channelName,
                     @NonNull final LoadMessagesCallback callback) {
        final String key = devKey + "_" + channelName;

        if (mCachedMessages.containsKey(key) && !isCacheDirty) {
            callback.onMessagesLoaded(mCachedMessages.get(key));
            return;
        }

        mLocalDataSource.load(devKey, channelName, new LoadMessagesCallback() {
            @Override
            public void onMessagesLoaded(List<Message> messages) {
                if (mCachedMessages.containsKey(key)) {
                    mCachedMessages.remove(key);
                }
                mCachedMessages.put(key, messages);

                callback.onMessagesLoaded(messages);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void save(@NonNull Message message) {
        checkNotNull(message);

        String key = message.devKey + "_" + message.channelName;

        if (mCachedMessages.containsKey(key)) {
            mCachedMessages.get(key).add(0, message);
        } else {
            List<Message> msgList = new ArrayList<>();
            msgList.add(message);
            mCachedMessages.put(key, msgList);
        }

        mLocalDataSource.save(message);
    }

    @Override
    public void delete(@NonNull String devKey, @NonNull String channelName) {
        String key = devKey + "_" + channelName;
        if (mCachedMessages.containsKey(key)) {
            mCachedMessages.remove(key);
        }
        mLocalDataSource.delete(devKey, channelName);
    }

}

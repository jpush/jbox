package com.jiguang.jbox.data.source;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Message;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class MessageRepository implements MessageDataSource {

    private static MessageRepository INSTANCE = null;

    private MessageDataSource mMessagesLocalDataSource;

    Map<String, List<Message>> mCachedMessages;

    boolean mCacheIsDirty = false;

    private MessageRepository(MessageDataSource localDataSource) {
        mMessagesLocalDataSource = localDataSource;
    }

    public static MessageRepository getInstance(MessageDataSource localDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new MessageRepository(localDataSource);
        }
        return INSTANCE;
    }

    public static void destoryInstance() {
        INSTANCE = null;
    }

    @Override
    public void getMessages(@NonNull final String devKey, @NonNull final String channelName,
                            @NonNull final LoadMessagesCallback callback) {
        checkNotNull(devKey);
        checkNotNull(channelName);

        final String mapKey = devKey + channelName;

        if (mCachedMessages != null && mCachedMessages.containsKey(mapKey) && !mCacheIsDirty) {
            callback.onMessagesLoaded(mCachedMessages.get(channelName));
            return;
        }

        mMessagesLocalDataSource.getMessages(devKey, channelName, new LoadMessagesCallback() {
            @Override
            public void onMessagesLoaded(List<Message> messages) {
                refreshCache(devKey, channelName, messages);
                callback.onMessagesLoaded(messages);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveMessage(@NonNull Message message) {
        checkNotNull(message);
        mMessagesLocalDataSource.saveMessage(message);

        String channelName = message.getChannelName();

        if (mCachedMessages == null) {
            mCachedMessages = new LinkedHashMap<>();
        }
        if (!mCachedMessages.containsKey(channelName)) {
            mCachedMessages.put(channelName, new ArrayList<Message>());
        }
        mCachedMessages.get(channelName).add(message);
    }

    @Override
    public void refreshMessages(@NonNull String devKey, @NonNull String channelName) {

    }


    private void refreshCache(String devKey, String channelName, List<Message> msgs) {
        if (mCachedMessages == null) {
            mCachedMessages = new LinkedHashMap<>();
        }
        String mapKey = devKey + channelName;

        if (mCachedMessages.get(mapKey) != null && !mCachedMessages.get(mapKey).isEmpty()) {
            mCachedMessages.get(mapKey).clear();
            mCachedMessages.remove(mapKey);
        }
        mCachedMessages.put(mapKey, msgs);
        mCacheIsDirty = false;
    }

}

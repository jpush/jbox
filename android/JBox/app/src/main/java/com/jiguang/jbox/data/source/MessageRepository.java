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
    public void getMessages(@NonNull final String channelId, @NonNull final LoadMessagesCallback callback) {
        checkNotNull(channelId);

        if (mCachedMessages != null && mCachedMessages.containsKey(channelId) && !mCacheIsDirty) {
            callback.onMessagesLoaded(mCachedMessages.get(channelId));
            return;
        }

        mMessagesLocalDataSource.getMessages(channelId, new LoadMessagesCallback() {
            @Override
            public void onMessagesLoaded(List<Message> messages) {
                refreshCache(channelId, messages);
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

        String channelId = message.getChannelId();

        if (mCachedMessages == null) {
            mCachedMessages = new LinkedHashMap<>();
        }
        if (!mCachedMessages.containsKey(channelId)) {
            mCachedMessages.put(channelId, new ArrayList<Message>());
        }
        mCachedMessages.get(channelId).add(message);
    }

    @Override
    public void refreshMessages(@NonNull String channelId) {

    }

    private void refreshCache(String channelId, List<Message> msgs) {
        if (mCachedMessages == null) {
            mCachedMessages = new LinkedHashMap<>();
        }
        mCachedMessages.get(channelId).clear();
        mCachedMessages.remove(channelId);
        mCachedMessages.put(channelId, msgs);
        mCacheIsDirty = false;
    }

}

package com.jiguang.jbox.data.local;

import android.support.annotation.NonNull;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.jiguang.jbox.data.model.Message;
import com.jiguang.jbox.data.source.MessageDataSource;

import java.util.List;

import javax.inject.Singleton;


@Singleton
public class MessageLocalDataSource implements MessageDataSource {

    private static MessageLocalDataSource INSTANCE;

    public MessageLocalDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MessageLocalDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void loadMessages(@NonNull String devKey, @NonNull String channelName, int offSet,
                             int limit, @NonNull LoadMessagesCallback callback) {
        List<Message> messageList = new Select().from(Message.class)
                .where("DevKey=? AND Channel=?", devKey, channelName)
                .offset(offSet)
                .limit(limit)
                .orderBy("time DESC")
                .execute();

        if (messageList == null) {
            callback.onDataNotAvailable();
        } else {
            callback.onLoadMessages(messageList);
        }
    }

    @Override
    public void clearMessages(@NonNull String devKey, @NonNull String channelName) {
        new Delete().from(Message.class)
                .where("DevKey=? AND Channel=?", devKey, channelName)
                .execute();
    }
}

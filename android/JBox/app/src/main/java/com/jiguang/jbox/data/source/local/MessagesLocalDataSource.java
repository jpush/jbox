package com.jiguang.jbox.data.source.local;

import android.support.annotation.NonNull;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.jiguang.jbox.data.Message;
import com.jiguang.jbox.data.source.MessageDataSource;

import java.nio.channels.Selector;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 从本地数据库中获取数据。
 */
public class MessagesLocalDataSource implements MessageDataSource {

    private static MessagesLocalDataSource INSTANCE;


    public static MessagesLocalDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MessagesLocalDataSource();
        }
        return INSTANCE;
    }


    @Override
    public void load(@NonNull String devKey, @NonNull String channelName,
                     @NonNull LoadMessagesCallback callback) {
        checkNotNull(devKey);
        checkNotNull(channelName);
        checkNotNull(callback);

        List<Message> messages = new Select().from(Message.class)
                .where("DevKey = ? AND ChannelName = ?", devKey, channelName)
                .execute();

        if (messages != null) {
            callback.onMessagesLoaded(messages);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void save(@NonNull Message message) {
        checkNotNull(message);

        message.save();
    }

    @Override
    public void delete(@NonNull String devKey, @NonNull String channelName) {
        checkNotNull(devKey);
        checkNotNull(channelName);

        new Delete().from(Message.class)
                .where("DevKey = ? AND ChannelName = ?", devKey, channelName)
                .execute();
    }

}

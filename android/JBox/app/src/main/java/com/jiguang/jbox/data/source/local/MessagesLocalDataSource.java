package com.jiguang.jbox.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Message;
import com.jiguang.jbox.data.source.MessageDataSource;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 从本地数据库中获取数据。
 */
public class MessagesLocalDataSource implements MessageDataSource {

    private static MessagesLocalDataSource INSTANCE;

    private MessagesDbHelper mDbHelper;

    private MessagesLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDbHelper = new MessagesDbHelper(context);
    }

    public static MessagesLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new MessagesLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void getMessages(@NonNull String devKey, @NonNull String channelName,
                            @NonNull LoadMessagesCallback callback) {
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                MessagesPersistenceContract.MessageEntry.COLUMN_NAME_ID,
                MessagesPersistenceContract.MessageEntry.COLUMN_NAME_CHANNEL_NAME,
                MessagesPersistenceContract.MessageEntry.COLUMN_NAME_DEV_KEY,
                MessagesPersistenceContract.MessageEntry.COLUMN_NAME_TITLE,
                MessagesPersistenceContract.MessageEntry.COLUMN_NAME_CONTENT,
                MessagesPersistenceContract.MessageEntry.COLUMN_NAME_TIME
        };

        String selection = MessagesPersistenceContract.MessageEntry.COLUMN_NAME_DEV_KEY +
                " = ? AND " + MessagesPersistenceContract.MessageEntry.COLUMN_NAME_CHANNEL_NAME +
                " = ?";

        String[] selectionArgs = {devKey, channelName};

        Cursor c = db.query(MessagesPersistenceContract.MessageEntry.TABLE_NAME, projection,
                selection, selectionArgs, null, null, null);

        Message msg;
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(
                        MessagesPersistenceContract.MessageEntry.COLUMN_NAME_ID));
                String title = c.getString(c.getColumnIndexOrThrow(
                        MessagesPersistenceContract.MessageEntry.COLUMN_NAME_TITLE));
                String content = c.getString(c.getColumnIndexOrThrow(
                        MessagesPersistenceContract.MessageEntry.COLUMN_NAME_CONTENT));
                String time = c.getString(c.getColumnIndexOrThrow(
                        MessagesPersistenceContract.MessageEntry.COLUMN_NAME_TIME));

                msg = new Message(id, title, content);
                msg.setChannelName(channelName);
                msg.setDevKey(devKey);
                msg.setTime(time);

                messages.add(msg);
            }
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (messages.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onMessagesLoaded(messages);
        }
    }

    @Override
    public void saveMessage(@NonNull Message message) {
        checkNotNull(message);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MessagesPersistenceContract.MessageEntry.COLUMN_NAME_ID, message.getId());
        values.put(MessagesPersistenceContract.MessageEntry.COLUMN_NAME_DEV_KEY, message.getDevKey());
        values.put(MessagesPersistenceContract.MessageEntry.COLUMN_NAME_CHANNEL_NAME,
                message.getChannelName());
        values.put(MessagesPersistenceContract.MessageEntry.COLUMN_NAME_TITLE, message.getTitle());
        values.put(MessagesPersistenceContract.MessageEntry.COLUMN_NAME_CONTENT, message.getContent());
        values.put(MessagesPersistenceContract.MessageEntry.COLUMN_NAME_TIME, message.getTime());

        db.insert(MessagesPersistenceContract.MessageEntry.TABLE_NAME, null, values);

        db.close();
    }

    @Override
    public void refreshMessages(@NonNull String devKey, @NonNull String channelName) {

    }


}

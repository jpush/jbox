package com.jiguang.jbox.data.source.local;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.source.ChannelsDataSource;
import com.jiguang.jbox.data.source.local.ChannelPersistenceContract.ChannelEntry;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


public class ChannelLocalDataSource implements ChannelsDataSource {

    private static ChannelLocalDataSource INSTANCE;

    private ChannelDBHelper mDBHelper;

    private ChannelLocalDataSource() {
        mDBHelper = new ChannelDBHelper();
    }

    public static ChannelLocalDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChannelLocalDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void getChannels(String devKey, @NonNull LoadChannelsCallback callback) {
        List<Channel> channels = new ArrayList<>();
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        String[] projection = {
                ChannelEntry.COLUMN_NAME_ENTRY_ID,
                ChannelEntry.COLUMN_NAME_DEV_KEY,
                ChannelEntry.COLUMN_NAME_NAME
        };

        String selection = ChannelEntry.COLUMN_NAME_DEV_KEY + " = ?";

        Cursor c = db.query(ChannelEntry.TABLE_NAME, projection, selection, new String[]{devKey},
                null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String name = c.getString(c.getColumnIndexOrThrow(ChannelEntry.COLUMN_NAME_NAME));
                int unreadCount = c.getInt(c.getColumnIndexOrThrow(ChannelEntry.COLUMN_NAME_UNREAD_COUNT));
                Channel channel = new Channel(name);
                channel.setDevKey(devKey);
                channel.setUnReadMessageCount(unreadCount);
                channels.add(channel);
            }

        }

        if (c != null) {
            c.close();
        }

        db.close();

        if (channels.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onChannelsLoaded(channels);
        }
    }

    public void updateUnreadCount(@NonNull Channel channel) {

    }

    @Override
    public void getSubscribedChannels(@NonNull LoadChannelsCallback callback) {

    }

    @Override
    public void refreshChannels() {

    }

}

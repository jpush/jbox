package com.jiguang.jbox.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.source.ChannelDataSource;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.jiguang.jbox.data.source.local.ChannelPersistenceContract.ChannelEntry;

/**
 * TODO：查询部分。
 */
public class ChannelLocalDataSource implements ChannelDataSource {

    private static ChannelLocalDataSource INSTANCE;

    private ChannelDbHelper mDbHelper;

    private ChannelLocalDataSource(Context context) {
        mDbHelper = new ChannelDbHelper(context);
    }

    public static ChannelLocalDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ChannelLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void getChannels(String devKey, boolean isSubscribe, LoadChannelsCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                ChannelEntry.COLUMN_NAME_ID,
                ChannelEntry.COLUMN_NAME_NAME,
                ChannelEntry.COLUMN_NAME_DEV_KEY,
                ChannelEntry.COLUMN_NAME_IS_SUBSCRIBE,
                ChannelEntry.COLUMN_NAME_ICON,
                ChannelEntry.COLUMN_NAME_UNREAD
        };

        String selection = ChannelEntry.COLUMN_NAME_DEV_KEY + " = ? AND " +
                ChannelEntry.COLUMN_NAME_IS_SUBSCRIBE + " = ?";

        String isSubscribeStr = isSubscribe ? "1" : "0";
        String[] selectionArgs = {devKey, isSubscribeStr};

        Cursor c = db.query(ChannelEntry.TABLE_NAME, projection, selection, selectionArgs, null,
                null, null);
        if (c != null && c.getCount() > 0) {
            List<Channel> channels = new ArrayList<>();
            Channel channel = null;
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(ChannelEntry.COLUMN_NAME_ID));
                String name = c.getString(c.getColumnIndexOrThrow(ChannelEntry.COLUMN_NAME_NAME));
                String icon = c.getString(c.getColumnIndexOrThrow(ChannelEntry.COLUMN_NAME_ICON));
                int unreadCount = c.getInt(c.getColumnIndexOrThrow(
                        ChannelEntry.COLUMN_NAME_UNREAD));
                boolean subscribe = c.getInt(c.getColumnIndexOrThrow(
                        ChannelEntry.COLUMN_NAME_IS_SUBSCRIBE)) == 1;

                channel = new Channel(id, name);
                channel.setDevKey(devKey);
                channel.setIconPath(icon);
                channel.setUnReadMessageCount(unreadCount);
                channel.setSubscribe(subscribe);

                channels.add(channel);
            }
            if (!channels.isEmpty()) {
                callback.onChannelsLoaded(channels);
            } else {
                callback.onDataNotAvailable();
            }
        }
    }

    @Override
    public void getChannels(@NonNull String devKey, LoadChannelsCallback callback) {
        checkNotNull(devKey);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                ChannelEntry.COLUMN_NAME_ID,
                ChannelEntry.COLUMN_NAME_NAME,
                ChannelEntry.COLUMN_NAME_DEV_KEY,
                ChannelEntry.COLUMN_NAME_IS_SUBSCRIBE,
                ChannelEntry.COLUMN_NAME_ICON,
                ChannelEntry.COLUMN_NAME_UNREAD
        };

        String selection = ChannelEntry.COLUMN_NAME_DEV_KEY + " = ?";
        String[] selectionArgs = {devKey};

        Cursor c = db.query(ChannelEntry.TABLE_NAME, projection, selection, selectionArgs, null,
                null, null);
        if (c != null && c.getCount() > 0) {
            List<Channel> channels = new ArrayList<>();
            Channel channel = null;
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(ChannelEntry.COLUMN_NAME_ID));
                String name = c.getString(c.getColumnIndexOrThrow(ChannelEntry.COLUMN_NAME_NAME));
                String icon = c.getString(c.getColumnIndexOrThrow(ChannelEntry.COLUMN_NAME_ICON));
                int unreadCount = c.getInt(c.getColumnIndexOrThrow(
                        ChannelEntry.COLUMN_NAME_UNREAD));
                boolean isSubscribe = c.getInt(c.getColumnIndexOrThrow(
                        ChannelEntry.COLUMN_NAME_IS_SUBSCRIBE)) == 1;

                channel = new Channel(id, name);
                channel.setDevKey(devKey);
                channel.setIconPath(icon);
                channel.setUnReadMessageCount(unreadCount);
                channel.setSubscribe(isSubscribe);

                channels.add(channel);
            }
            if (!channels.isEmpty()) {
                callback.onChannelsLoaded(channels);
            } else {
                callback.onDataNotAvailable();
            }
        }
    }

    @Override
    public void getChannels(ChannelDataSource.LoadChannelsCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                ChannelEntry.COLUMN_NAME_ID,
                ChannelEntry.COLUMN_NAME_NAME,
                ChannelEntry.COLUMN_NAME_DEV_KEY,
                ChannelEntry.COLUMN_NAME_IS_SUBSCRIBE,
                ChannelEntry.COLUMN_NAME_ICON,
                ChannelEntry.COLUMN_NAME_UNREAD
        };

        Cursor c = db.query(ChannelEntry.TABLE_NAME, projection, null, null,
                ChannelEntry.COLUMN_NAME_DEV_KEY, null, null);

        List<Channel> channels = null;
        if (c != null && c.getCount() > 0) {
            channels = new ArrayList<>();
            Channel channel = null;
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(ChannelEntry.COLUMN_NAME_ID));
                String name = c.getString(c.getColumnIndexOrThrow(ChannelEntry.COLUMN_NAME_NAME));
                String icon = c.getString(c.getColumnIndexOrThrow(ChannelEntry.COLUMN_NAME_ICON));
                String devKey = c.getString(c.getColumnIndexOrThrow(
                        ChannelEntry.COLUMN_NAME_DEV_KEY));
                int unreadCount = c.getInt(c.getColumnIndexOrThrow(
                        ChannelEntry.COLUMN_NAME_UNREAD));
                boolean isSubscribe = c.getInt(c.getColumnIndexOrThrow(
                        ChannelEntry.COLUMN_NAME_IS_SUBSCRIBE)) == 1;

                channel = new Channel(id, name);
                channel.setDevKey(devKey);
                channel.setIconPath(icon);
                channel.setUnReadMessageCount(unreadCount);
                channel.setSubscribe(isSubscribe);

                channels.add(channel);
            }
        }
        if (channels != null) {
            callback.onChannelsLoaded(channels);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void getChannels(boolean isSubscribe, ChannelDataSource.LoadChannelsCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                ChannelEntry.COLUMN_NAME_ID,
                ChannelEntry.COLUMN_NAME_NAME,
                ChannelEntry.COLUMN_NAME_DEV_KEY,
                ChannelEntry.COLUMN_NAME_IS_SUBSCRIBE,
                ChannelEntry.COLUMN_NAME_ICON,
                ChannelEntry.COLUMN_NAME_UNREAD
        };

        String selection = ChannelEntry.COLUMN_NAME_IS_SUBSCRIBE + " = ?";
        String isSubscribeStr = isSubscribe ? "1" : "0";

        Cursor c = db.query(ChannelEntry.TABLE_NAME, projection, selection,
                new String[]{isSubscribeStr}, ChannelEntry.COLUMN_NAME_DEV_KEY, null, null);

        List<Channel> channels = null;
        if (c != null && c.getCount() > 0) {
            channels = new ArrayList<>();
            Channel channel = null;
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(ChannelEntry.COLUMN_NAME_ID));
                String name = c.getString(c.getColumnIndexOrThrow(ChannelEntry.COLUMN_NAME_NAME));
                String icon = c.getString(c.getColumnIndexOrThrow(ChannelEntry.COLUMN_NAME_ICON));
                String devKey = c.getString(c.getColumnIndexOrThrow(
                        ChannelEntry.COLUMN_NAME_DEV_KEY));
                int unreadCount = c.getInt(c.getColumnIndexOrThrow(
                        ChannelEntry.COLUMN_NAME_UNREAD));

                channel = new Channel(id, name);
                channel.setDevKey(devKey);
                channel.setIconPath(icon);
                channel.setUnReadMessageCount(unreadCount);
                channel.setSubscribe(isSubscribe);

                channels.add(channel);
            }
        }
        if (channels != null) {
            callback.onChannelsLoaded(channels);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveChannel(Channel channel) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(ChannelEntry.COLUMN_NAME_ID, channel.getId());
        value.put(ChannelEntry.COLUMN_NAME_NAME, channel.getName());
        value.put(ChannelEntry.COLUMN_NAME_DEV_KEY, channel.getDevKey());
        value.put(ChannelEntry.COLUMN_NAME_ICON, channel.getIconPath());
        value.put(ChannelEntry.COLUMN_NAME_UNREAD, channel.getUnReadMessageCount());
        value.put(ChannelEntry.COLUMN_NAME_IS_SUBSCRIBE, channel.isSubscribe());

        db.insert(ChannelEntry.TABLE_NAME, null, value);
        db.close();
    }

    @Override
    public void saveChannels(List<Channel> channels) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues value = null;
        for (Channel channel : channels) {
            value = new ContentValues();
            value.put(ChannelEntry.COLUMN_NAME_ID, channel.getId());
            value.put(ChannelEntry.COLUMN_NAME_NAME, channel.getName());
            value.put(ChannelEntry.COLUMN_NAME_DEV_KEY, channel.getDevKey());
            value.put(ChannelEntry.COLUMN_NAME_ICON, channel.getIconPath());
            value.put(ChannelEntry.COLUMN_NAME_UNREAD, channel.getUnReadMessageCount());
            value.put(ChannelEntry.COLUMN_NAME_IS_SUBSCRIBE, channel.isSubscribe());

            db.insert(ChannelEntry.TABLE_NAME, null, value);
        }

        db.close();
    }

    @Override
    public void updateChannel(Channel channel) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(ChannelEntry.COLUMN_NAME_NAME, channel.getName());
        value.put(ChannelEntry.COLUMN_NAME_UNREAD, channel.getUnReadMessageCount());
        value.put(ChannelEntry.COLUMN_NAME_ICON, channel.getIconPath());
        value.put(ChannelEntry.COLUMN_NAME_IS_SUBSCRIBE, channel.isSubscribe());

        String selection = ChannelEntry.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {channel.getId()};

        db.update(ChannelEntry.TABLE_NAME, value, selection, selectionArgs);

        db.close();
    }

    @Override
    public void updateChannels(List<Channel> channels) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues value = null;
        String selection = ChannelEntry.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = null;

        for (Channel channel : channels) {
            selectionArgs = new String[]{channel.getId()};
            value = new ContentValues();

            value.put(ChannelEntry.COLUMN_NAME_NAME, channel.getName());
            value.put(ChannelEntry.COLUMN_NAME_UNREAD, channel.getUnReadMessageCount());
            value.put(ChannelEntry.COLUMN_NAME_ICON, channel.getIconPath());
            value.put(ChannelEntry.COLUMN_NAME_IS_SUBSCRIBE, channel.isSubscribe());

            db.update(ChannelEntry.TABLE_NAME, value, selection, selectionArgs);
        }

        db.close();
    }

    @Override
    public void refresh() {

    }

}

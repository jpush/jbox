package com.jiguang.jbox.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.source.ChannelDataSource;

import java.util.List;

import static com.jiguang.jbox.data.source.local.ChannelPersistenceContract.ChannelEntry;

/**
 * TODO：查询部分。
 */
public class ChannelLocalDataSource implements ChannelDataSource{

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
    public void getChannels(String devKey, boolean isSubscribe) {

    }

    @Override
    public void getChannels(String devKey) {

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
    public void saveChannels(String devKey, List<Channel> channels) {

    }

}

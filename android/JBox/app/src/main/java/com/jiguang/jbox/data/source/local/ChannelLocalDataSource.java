package com.jiguang.jbox.data.source.local;

import android.text.TextUtils;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.source.ChannelDataSource;

import java.util.List;

/**
 * TODO：查询部分。
 */
public class ChannelLocalDataSource implements ChannelDataSource {

    private static ChannelLocalDataSource INSTANCE;

    private ChannelLocalDataSource() {
    }

    public static ChannelLocalDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChannelLocalDataSource();
        }
        return INSTANCE;
    }


    @Override
    public void save(List<Channel> channels) {
        ActiveAndroid.beginTransaction();
        try {
            for (Channel c : channels) {
                c.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    @Override
    public void save(Channel channel) {
        channel.save();
    }

    @Override
    public void load(String devKey, LoadChannelsCallback callback) {
        if (TextUtils.isEmpty(devKey)) {
            return;
        }
        List<Channel> channels = new Select().from(Channel.class)
                .where("DevKey = ?", devKey)
                .execute();

        if (channels == null) {
            callback.onDataNotAvailable();
        } else {
            callback.onChannelsLoaded(channels);
        }
    }

    @Override
    public void delete(String devKey) {
        new Delete().from(Channel.class).where("DevKey = ?", devKey).execute();
    }
}

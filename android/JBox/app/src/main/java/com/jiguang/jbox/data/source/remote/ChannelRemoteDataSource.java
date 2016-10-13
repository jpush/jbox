package com.jiguang.jbox.data.source.remote;

import android.text.TextUtils;

import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.source.ChannelDataSource;
import com.jiguang.jbox.util.HttpUtil;

import java.util.List;


public class ChannelRemoteDataSource implements ChannelDataSource {

    private static ChannelRemoteDataSource INSTANCE;

    public static ChannelRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChannelRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void save(List<Channel> channels) {

    }

    @Override
    public void save(Channel channel) {

    }

    @Override
    public void load(String devKey, LoadChannelsCallback callback) {
        if (TextUtils.isEmpty(devKey)) {
            return;
        }
        HttpUtil.getInstance().requestChannels(devKey, callback);
    }

    @Override
    public void delete(String devKey) {

    }

}

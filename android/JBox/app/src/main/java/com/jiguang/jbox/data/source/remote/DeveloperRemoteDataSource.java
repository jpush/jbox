package com.jiguang.jbox.data.source.remote;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.source.DeveloperDataSource;
import com.jiguang.jbox.util.HttpUtil;

import static com.google.common.base.Preconditions.checkNotNull;

public class DeveloperRemoteDataSource implements DeveloperDataSource {

    private static DeveloperRemoteDataSource INSTANCE;

    public static DeveloperRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DeveloperRemoteDataSource();
        }
        return INSTANCE;
    }

    /**
     * 从服务器获取 developer 信息。
     * @param devKey：扫描二维码后获得。
     * @param callback：回调事件。
     */
    @Override
    public void getDeveloper(@NonNull String devKey, @NonNull LoadDevCallback callback) {
        checkNotNull(devKey);
        checkNotNull(callback);

        Developer dev = HttpUtil.getInstance().requestDevelopers(devKey);
        if (dev != null) {
            callback.onDevLoaded(dev);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void getDevelopers(@NonNull LoadDevsCallback callback) {

    }

    @Override
    public void saveDeveloper(@NonNull Developer dev) {

    }

    @Override
    public void updateDeveloper(@NonNull Developer dev) {

    }

    @Override
    public void refresh() {

    }

}

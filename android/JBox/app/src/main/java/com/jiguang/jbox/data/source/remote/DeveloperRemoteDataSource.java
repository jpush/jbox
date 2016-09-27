package com.jiguang.jbox.data.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.source.DeveloperDataSource;
import com.jiguang.jbox.util.HttpUtil;

import static com.google.common.base.Preconditions.checkNotNull;

public class DeveloperRemoteDataSource implements DeveloperDataSource {

    private static DeveloperRemoteDataSource INSTANCE;

    private Context mContext;

    public static DeveloperRemoteDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DeveloperRemoteDataSource(context);
        }
        return INSTANCE;
    }

    private DeveloperRemoteDataSource(Context context) {
        mContext = context;
    }

    /**
     * 从服务器获取 developer 信息。
     *
     * @param devKey：扫描二维码后获得。
     * @param callback：回调事件。
     */
    @Override
    public void getDeveloper(@NonNull final String devKey, @NonNull final LoadDevCallback callback) {
        checkNotNull(devKey);
        checkNotNull(callback);

        HttpUtil.getInstance(mContext).requestDevelopers(devKey, callback);
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

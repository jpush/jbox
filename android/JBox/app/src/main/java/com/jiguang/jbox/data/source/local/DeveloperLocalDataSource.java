package com.jiguang.jbox.data.source.local;

import android.support.annotation.NonNull;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.source.DeveloperDataSource;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DeveloperLocalDataSource implements DeveloperDataSource {

    private static DeveloperLocalDataSource INSTANCE;

    public static DeveloperLocalDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DeveloperLocalDataSource();
        }
        return INSTANCE;
    }


    @Override
    public void load(@NonNull String devKey, @NonNull LoadDevCallback callback) {
        checkNotNull(devKey);
        List<Developer> devs = new Select().from(Developer.class)
                .where("DevKey = ?", devKey)
                .execute();
        if (devs == null) {
            callback.onDataNotAvailable();
        } else if (!devs.isEmpty()) {
            callback.onDevLoaded(devs.get(0));
        }
    }

    @Override
    public void load(@NonNull LoadDevsCallback callback) {
        new Select().from(Developer.class).execute();
    }

    @Override
    public void save(@NonNull Developer dev) {
        checkNotNull(dev);
        dev.save();
    }

    @Override
    public void update(@NonNull Developer dev) {
        checkNotNull(dev);
        delete(dev.key);
        dev.save();
    }

    @Override
    public void delete(@NonNull String devKey) {
        checkNotNull(devKey);
        new Delete().from(Developer.class).where("DevKey = ?", devKey).execute();
    }
}

package com.jiguang.jbox.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.source.DeveloperDataSource;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.jiguang.jbox.data.source.local.DeveloperPersistenceContract.DevEntry;

public class DeveloperLocalDataSource implements DeveloperDataSource {

    private static DeveloperLocalDataSource INSTANCE;

    private DeveloperDbHelper mDbHelper;

    private DeveloperLocalDataSource(Context context) {
        mDbHelper = new DeveloperDbHelper(context);
    }

    public static DeveloperLocalDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DeveloperLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void getDeveloper(@NonNull String devKey, @NonNull LoadDevCallback callback) {
        checkNotNull(devKey);
        checkNotNull(callback);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                DevEntry.COLUMN_NAME_KEY,
                DevEntry.COLUMN_NAME_NAME,
                DevEntry.COLUMN_NAME_DESC,
                DevEntry.COLUMN_NAME_PLATFORM,
                DevEntry.COLUMN_NAME_AVATAR
        };

        String selection = DevEntry.COLUMN_NAME_KEY + " = ?";
        String[] selectionArgs = {devKey};

        Cursor c = db.query(DevEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        Developer dev = null;

        if (c != null && c.getCount() > 0) {
            c.moveToNext();

            String key = c.getString(c.getColumnIndexOrThrow(DevEntry.COLUMN_NAME_KEY));
            String name = c.getString(c.getColumnIndexOrThrow(DevEntry.COLUMN_NAME_NAME));
            String platform = c.getString(c.getColumnIndexOrThrow(DevEntry.COLUMN_NAME_PLATFORM));
            String desc = c.getString(c.getColumnIndexOrThrow(DevEntry.COLUMN_NAME_DESC));
            String avatar = c.getString(c.getColumnIndexOrThrow(DevEntry.COLUMN_NAME_AVATAR));

            dev = new Developer(key, name, platform);
            dev.setDesc(desc);
            dev.setAvatarPath(avatar);

            c.close();
        }

        db.close();

        if (dev == null) {
            callback.onDataNotAvailable();
        } else {
            callback.onDevLoaded(dev);
        }

    }

    @Override
    public void saveDeveloper(@NonNull Developer dev) {
        checkNotNull(dev);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DevEntry.COLUMN_NAME_KEY, dev.getDevKey());
        values.put(DevEntry.COLUMN_NAME_NAME, dev.getDevName());
        values.put(DevEntry.COLUMN_NAME_DESC, dev.getDesc());
        values.put(DevEntry.COLUMN_NAME_PLATFORM, dev.getPlatform());
        values.put(DevEntry.COLUMN_NAME_AVATAR, dev.getAvatarPath());

        db.insert(DevEntry.TABLE_NAME, null, values);

        db.close();
    }


}

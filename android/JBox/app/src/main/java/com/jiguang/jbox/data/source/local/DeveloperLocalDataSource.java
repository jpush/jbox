package com.jiguang.jbox.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.source.DeveloperDataSource;
import com.jiguang.jbox.data.source.local.DeveloperPersistenceContract.DeveloperEntry;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DeveloperLocalDataSource implements DeveloperDataSource {

    private static DeveloperLocalDataSource INSTANCE;

    private DeveloperDBHelper mDBHelper;

    private DeveloperLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDBHelper = new DeveloperDBHelper(context);
    }

    public static DeveloperLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DeveloperLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void getDevelopers(@NonNull LoadDevelopersCallback callback) {
        List<Developer> developers = new ArrayList<>();
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        String[] projection = {
                DeveloperEntry.COLUMN_NAME_KEY,
                DeveloperEntry.COLUMN_NAME_NAME,
                DeveloperEntry.COLUMN_NAME_PLATFORM
        };

        Cursor c = db.query(DeveloperEntry.TABLE_NAME, projection, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String devKey = c.getString(c.getColumnIndexOrThrow(DeveloperEntry.COLUMN_NAME_KEY));
                String devName = c.getString(c.getColumnIndexOrThrow(DeveloperEntry.COLUMN_NAME_NAME));
                String platform = c.getString(c.getColumnIndexOrThrow(DeveloperEntry.COLUMN_NAME_PLATFORM));

                Developer dev = new Developer(devKey, devName, platform);
                developers.add(dev);
            }
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (developers.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onDevelopersLoaded(developers);
        }
    }

    @Override
    public void getDeveloper(@NonNull String devKey, @NonNull GetDeveloperCallback callback) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        String[] projection = {
                DeveloperEntry.COLUMN_NAME_KEY,
                DeveloperEntry.COLUMN_NAME_NAME,
                DeveloperEntry.COLUMN_NAME_PLATFORM
        };

        String selection = DeveloperEntry.COLUMN_NAME_KEY + " LIKE ?";
        String[] selectionArgs = { devKey };

        Cursor c = db.query(DeveloperEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        Developer dev = null;

        if (c != null && c.getCount() > 0) {
            c.moveToNext();
            String key = c.getString(c.getColumnIndexOrThrow(DeveloperEntry.COLUMN_NAME_KEY));
            String name = c.getString(c.getColumnIndexOrThrow(DeveloperEntry.COLUMN_NAME_NAME));
            String platform = c.getString(c.getColumnIndexOrThrow(DeveloperEntry.COLUMN_NAME_PLATFORM));

            dev = new Developer(key, name, platform);
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (dev != null) {
            callback.onDeveloperLoaded(dev);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveDeveloper(@NonNull Developer dev) {
        checkNotNull(dev);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DeveloperEntry.COLUMN_NAME_KEY, dev.getDevKey());
        values.put(DeveloperEntry.COLUMN_NAME_NAME, dev.getDevName());
        values.put(DeveloperEntry.COLUMN_NAME_PLATFORM, dev.getPlatform());

        db.insert(DeveloperEntry.TABLE_NAME, null, values);

        db.close();
    }

    @Override
    public void refreshDevelopers() {

    }

    @Override
    public void deleteAllDevelopers() {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        db.delete(DeveloperEntry.TABLE_NAME, null, null);

        db.close();
    }

    @Override
    public void deleteDeveloper(@NonNull String devKey) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        String selection = DeveloperEntry.COLUMN_NAME_KEY + " LIKE ?";
        String[] selectionArgs = { devKey };

        db.delete(DeveloperEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

}

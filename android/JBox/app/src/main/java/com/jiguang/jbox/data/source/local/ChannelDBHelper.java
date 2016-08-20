package com.jiguang.jbox.data.source.local;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jiguang.jbox.AppApplication;

public class ChannelDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "JBox.db";

    private static final String TEXT_TYPE = " TEXT";

    private static final String INTEGER_TYPE = " INTEGER";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ChannelPersistenceContract.ChannelEntry.TABLE_NAME + " (" +
                    ChannelPersistenceContract.ChannelEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
                    ChannelPersistenceContract.ChannelEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    ChannelPersistenceContract.ChannelEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    ChannelPersistenceContract.ChannelEntry.COLUMN_NAME_DEV_KEY + TEXT_TYPE + COMMA_SEP +
                    ChannelPersistenceContract.ChannelEntry.COLUMN_NAME_UNREAD_COUNT + INTEGER_TYPE +
            " )";

    public ChannelDBHelper() {
        super(AppApplication.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

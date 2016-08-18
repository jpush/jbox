package com.jiguang.jbox.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DeveloperDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "JBox.db";

    private static final String TEXT_TYPE = " TEXT";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DeveloperPersistenceContract.DeveloperEntry.TABLE_NAME + " (" +
                    DeveloperPersistenceContract.DeveloperEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
                    DeveloperPersistenceContract.DeveloperEntry.COLUMN_NAME_KEY + TEXT_TYPE + COMMA_SEP +
                    DeveloperPersistenceContract.DeveloperEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    DeveloperPersistenceContract.DeveloperEntry.COLUMN_NAME_PLATFORM + TEXT_TYPE + COMMA_SEP +
            " )";

    public DeveloperDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

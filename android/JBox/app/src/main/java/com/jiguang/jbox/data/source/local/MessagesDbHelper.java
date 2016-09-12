package com.jiguang.jbox.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MessagesDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "JBox.db";

    private static final String TEXT_TYPE = " TEXT";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MessagesPersistenceContract.MessageEntry.TABLE_NAME + " (" +
                    MessagesPersistenceContract.MessageEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
                    MessagesPersistenceContract.MessageEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    MessagesPersistenceContract.MessageEntry.COLUMN_NAME_CONTENT + TEXT_TYPE + COMMA_SEP +
                    MessagesPersistenceContract.MessageEntry.COLUMN_NAME_TIME + TEXT_TYPE + COMMA_SEP +
                    " )";

    public MessagesDbHelper(Context context) {
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

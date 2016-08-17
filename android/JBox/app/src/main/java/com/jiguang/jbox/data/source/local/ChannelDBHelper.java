package com.jiguang.jbox.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ChannelDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "Channels.db";

    public ChannelDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

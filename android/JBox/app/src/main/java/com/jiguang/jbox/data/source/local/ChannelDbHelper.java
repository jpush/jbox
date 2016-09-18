package com.jiguang.jbox.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.jiguang.jbox.data.source.local.ChannelPersistenceContract.ChannelEntry;

public class ChannelDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "JBox.db";

    private static final String TEXT_TYPE = " TEXT";

    private static final String BOOLEAN_TYPE = " INTEGER";

    private static final String INT_TYPE = " INTEGER";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ChannelEntry.TABLE_NAME + " (" +
                    ChannelEntry.COLUMN_NAME_ID + TEXT_TYPE + " PRIMARY KEY," +
                    ChannelEntry.COLUMN_NAME_DEV_KEY + TEXT_TYPE + COMMA_SEP +
                    ChannelEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    ChannelEntry.COLUMN_NAME_ICON + TEXT_TYPE + COMMA_SEP +
                    ChannelEntry.COLUMN_NAME_UNREAD + INT_TYPE + COMMA_SEP +
                    ChannelEntry.COLUMN_NAME_IS_SUBSCRIBE + BOOLEAN_TYPE +
                    " )";

    public ChannelDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

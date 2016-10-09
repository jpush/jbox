package com.jiguang.jbox.data.source.local;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jiguang.jbox.AppApplication;

import static com.jiguang.jbox.data.source.local.DeveloperPersistenceContract.DevEntry;

public class DeveloperDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "JBox.db";

    private static final String TEXT_TYPE = " TEXT";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DevEntry.TABLE_NAME + " (" +
                    DevEntry.COLUMN_NAME_KEY + TEXT_TYPE + " PRIMARY KEY," +
                    DevEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    DevEntry.COLUMN_NAME_DESC + TEXT_TYPE + COMMA_SEP +
                    DevEntry.COLUMN_NAME_PLATFORM + TEXT_TYPE + COMMA_SEP +
                    DevEntry.COLUMN_NAME_AVATAR + TEXT_TYPE +
                    " )";

    DeveloperDbHelper() {
        super(AppApplication.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

package com.jiguang.jbox.data.source.local;

import android.provider.BaseColumns;

/**
 * The contract used for the db to save the devs locally.
 */
public class DeveloperPersistenceContract {

    public DeveloperPersistenceContract() {}

    public static abstract class DeveloperEntry implements BaseColumns {
        public static final String TABLE_NAME = "developer";
        public static final String COLUMN_NAME_KEY = "key";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PLATFORM = "platform";
    }
}

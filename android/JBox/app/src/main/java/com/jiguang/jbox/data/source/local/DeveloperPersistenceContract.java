package com.jiguang.jbox.data.source.local;

import android.provider.BaseColumns;

public class DeveloperPersistenceContract {

    public static abstract class DevEntry implements BaseColumns {
        public static final String TABLE_NAME = "developer";
        public static final String COLUMN_NAME_KEY = "key"; // 主键
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PLATFORM = "platform";
        public static final String COLUMN_NAME_AVATAR = "avatar";
        public static final String COLUMN_NAME_DESC = "description";
    }
}

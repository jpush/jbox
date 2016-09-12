package com.jiguang.jbox.data.source.local;

import android.provider.BaseColumns;

/**
 * The contract used for the db to save the messages locally.
 */
public class MessagesPersistenceContract {

    public MessagesPersistenceContract() {}

    public static abstract class MessageEntry implements BaseColumns {
        public static final String TABLE_NAME = "message";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_CHANNEL_ID = "channelId";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_TIME = "time";
    }
}

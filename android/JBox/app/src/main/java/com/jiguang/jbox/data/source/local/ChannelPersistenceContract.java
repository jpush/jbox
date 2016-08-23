package com.jiguang.jbox.data.source.local;

import android.provider.BaseColumns;

public final class ChannelPersistenceContract {

    public ChannelPersistenceContract() {}

    public static abstract class ChannelEntry implements BaseColumns {
        public static final String TABLE_NAME = "channel";

        public static final String COLUMN_NAME_ENTRY_ID = "entryId";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DEV_KEY = "devKey";
        public static final String COLUMN_NAME_UNREAD_COUNT = "unreadCount";
        public static final String COLUMN_NAME_IS_SUBSCRIBE = "isSubscribe";
    }

}

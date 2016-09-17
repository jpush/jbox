package com.jiguang.jbox.data.source.local;

import android.provider.BaseColumns;

public class ChannelPersistenceContract {

    public static abstract class ChannelEntry implements BaseColumns {
        public static final String TABLE_NAME = "channel";
        public static final String COLUMN_NAME_ID = "id"; // 主键
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ICON = "icon";
        public static final String COLUMN_NAME_DEV_KEY = "devKey";
        public static final String COLUMN_NAME_UNREAD = "unread";   // 未读消息数
        public static final String COLUMN_NAME_IS_SUBSCRIBE = "isSubscribe";   // 订阅状态
    }
}

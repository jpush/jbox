package com.jiguang.jbox.util;

import android.util.Log;

public class LogUtil {
    private static final String LOG_PREFIX = "jbox_";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;

    public static boolean LOGGING_ENABLED = true;

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }
        return LOG_PREFIX + str;
    }

    public static String makeLogTag(Class c) {
        return makeLogTag(c.getSimpleName());
    }

    public static void LOGD(String tag, String msg) {
        if (LOGGING_ENABLED) {
            if (Log.isLoggable(tag, Log.DEBUG)) {
                Log.d(tag, msg);
            }
        }
    }

    public static void LOGD(String tag, String msg, Throwable cause) {
        if (LOGGING_ENABLED) {
            if (Log.isLoggable(tag, Log.DEBUG)) {
                Log.d(tag, msg, cause);
            }
        }
    }

    public static void LOGV(String tag, String msg) {
        if (LOGGING_ENABLED) {
            if (Log.isLoggable(tag, Log.VERBOSE)) {
                Log.v(tag, msg);
            }
        }
    }

    public static void LOGV(String tag, String msg, Throwable cause) {
        if (LOGGING_ENABLED) {
            if (Log.isLoggable(tag, Log.VERBOSE)) {
                Log.v(tag, msg, cause);
            }
        }
    }

    public static void LOGI(String tag, String msg) {
        if (LOGGING_ENABLED) {
            Log.i(tag, msg);
        }
    }

    public static void LOGI(String tag, String msg, Throwable cause) {
        if (LOGGING_ENABLED) {
            Log.i(tag, msg, cause);
        }
    }

    public static void LOGW(String tag, String msg) {
        if (LOGGING_ENABLED) {
            Log.w(tag, msg);
        }
    }

    public static void LOGW(String tag, String msg, Throwable cause) {
        if (LOGGING_ENABLED) {
            Log.w(tag, msg, cause);
        }
    }

    public static void LOGE(String tag, String msg) {
        if (LOGGING_ENABLED) {
            Log.e(tag, msg);
        }
    }

    public static void LOGE(String tag, String msg, Throwable cause) {
        if (LOGGING_ENABLED) {
            Log.e(tag, msg, cause);
        }
    }

}

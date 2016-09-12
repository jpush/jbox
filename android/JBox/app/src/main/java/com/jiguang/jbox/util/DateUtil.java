package com.jiguang.jbox.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static String formatPattern = "hh:mm";

    public static String millis2DateString(long timeMillis, String pattern) {
        Date date = new Date(timeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

}

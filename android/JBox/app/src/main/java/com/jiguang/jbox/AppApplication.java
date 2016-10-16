package com.jiguang.jbox;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.app.Application;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.Developer;


public class AppApplication extends Application {

    private static String APP_KEY;

    private static Context mContext;

    public static boolean shouldUpdateData = false;

    public static String currentDevKey;
    public static String currentChannelName = "";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

//        JPushInterface.init(this);

        ActiveAndroid.initialize(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }

    public static String getAppKey() {
        return APP_KEY;
    }

    public static Context getAppContext() {
        return mContext;
    }

}

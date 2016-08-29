package com.jiguang.jbox;

import android.app.Application;
import android.content.Context;


public class AppApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
//        JPushInterface.init(this);
    }

    public static Context getContext() {
        return context;
    }
}

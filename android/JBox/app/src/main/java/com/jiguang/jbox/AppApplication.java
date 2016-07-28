package com.jiguang.jbox;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.init(this);
    }
}

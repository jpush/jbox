package com.jiguang.jbox;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.app.Application;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.Message;


public class AppApplication extends Application {

    private static String APP_KEY;

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

//        JPushInterface.init(this);

//        Configuration.Builder builder = new Configuration.Builder(this);
//        builder.addModelClass(Channel.class);
//        builder.addModelClass(Developer.class);
//        builder.addModelClass(Message.class);
//        ActiveAndroid.initialize(builder.create());

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

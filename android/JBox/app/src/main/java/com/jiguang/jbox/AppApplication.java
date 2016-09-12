package com.jiguang.jbox;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.jiguang.jbox.util.LogUtil;


public class AppApplication extends Application {

    private static Context context;

    private static String APP_KEY;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
//        JPushInterface.init(this);

        if (APP_KEY == null) {
            try {
                ApplicationInfo appInfo = getPackageManager().getApplicationInfo(
                        this.getPackageName(), PackageManager.GET_META_DATA);
                APP_KEY = appInfo.metaData.getString("JPUSH_APPKEY");
                LogUtil.LOGI("AppApplication", APP_KEY);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static Context getContext() {
        return context;
    }

    public static String getAppKey() {
        return APP_KEY;
    }

}

package com.jiguang.jbox;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.jiguang.jbox.util.LogUtil;


public class AppApplication extends Application {

    private static String APP_KEY;

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

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

    public static String getAppKey() {
        return APP_KEY;
    }

    public static Context getAppContext() {
        return mContext;
    }

}

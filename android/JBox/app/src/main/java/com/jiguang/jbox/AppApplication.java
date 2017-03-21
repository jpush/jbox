package com.jiguang.jbox;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.app.Application;

import cn.jpush.android.api.JPushInterface;

public class AppApplication extends Application {
  private static Context mContext;

  public static boolean shouldUpdateData = false;

  public static String currentDevKey = "";
  public static String currentChannelName = "";

  @Override public void onCreate() {
    super.onCreate();
    mContext = getApplicationContext();
    JPushInterface.init(this);
    ActiveAndroid.initialize(this);
  }

  @Override public void onTerminate() {
    super.onTerminate();
    ActiveAndroid.dispose();
  }

  public static Context getAppContext() {
    return mContext;
  }

  public static String getAppKey() {
    PackageManager pm = mContext.getPackageManager();
    ApplicationInfo appInfo;
    try {
      appInfo = pm.getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      return "";
    }
    return appInfo.metaData.getString("JPUSH_APPKEY");
  }
}

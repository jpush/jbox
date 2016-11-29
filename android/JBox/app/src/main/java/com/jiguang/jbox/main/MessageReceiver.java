package com.jiguang.jbox.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.Message;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;


public class MessageReceiver extends BroadcastReceiver {
    public MessageReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {
            Bundle bundle = intent.getExtras();

            String extraJson = bundle.getString(JPushInterface.EXTRA_EXTRA);

            JSONObject jsonObject;
            String title;
            String content;
            String devKey;
            String channelName;
            String iconUrl;
            long timeMillis;
            String url;

            try {
                jsonObject = new JSONObject(extraJson);
                title = bundle.getString(JPushInterface.EXTRA_TITLE);
                content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                devKey = jsonObject.getString("dev_key");
                channelName = jsonObject.getString("channel");
                iconUrl = jsonObject.getString("icon");         // 集成的图标 url。
                timeMillis = Long.parseLong(jsonObject.getString("datetime"));
                url = jsonObject.isNull("url") ? null : jsonObject.getString("url");
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            Message msg = new Message();
            msg.title = title;
            msg.content = content;
            msg.devKey = devKey;
            msg.channelName = channelName;
            msg.iconUrl = iconUrl;
            msg.time = timeMillis;
            msg.url = url;
            msg.save();

            Bundle data = new Bundle();

            android.os.Message handlerMsg = new android.os.Message();
            // 如果收到的是当前 Channel 的消息就更新界面，否则保存到数据库并更新界面。
            if (AppApplication.currentDevKey.equals(devKey) &&
                    AppApplication.currentChannelName.equals(channelName)) {
                handlerMsg.what = MainActivity.MSG_WHAT_RECEIVE_MSG_CURRENT;
                data.putSerializable("message", msg);
                handlerMsg.setData(data);
                MainActivity.handler.sendMessage(handlerMsg);
            } else {
                Channel c = new Select().from(Channel.class)
                        .where("DevKey = ? AND Name = ?", devKey, channelName)
                        .executeSingle();

                if (c != null) {
                    new Update(Channel.class)
                            .set("UnreadCount = ?", c.unreadCount + 1)
                            .where("DevKey = ? AND Name = ?", devKey, channelName)
                            .execute();

                    handlerMsg.what = MainActivity.MSG_WHAT_RECEIVE_MSG;
                    data.putString("DevKey", msg.devKey);
                    handlerMsg.setData(data);
                    MainActivity.handler.sendMessage(handlerMsg);
                }
            }
        }

        if (intent.getAction().equals(JPushInterface.ACTION_NOTIFICATION_OPENED)) {
//             TODO:点击通知跳转
//            Bundle bundle = intent.getExtras();
//            String extraJson = bundle.getString(JPushInterface.EXTRA_EXTRA);
//
//            JSONObject jsonObject;
//            String devKey;
//            String channelName;
//
//            try {
//                jsonObject = new JSONObject(extraJson);
//                devKey = jsonObject.getString("dev_key");
//                channelName = jsonObject.getString("channel");
//            } catch (JSONException e) {
//                e.printStackTrace();
//                return;
//            }
//
//            android.os.Message handlerMsg = new android.os.Message();
//            handlerMsg.what = MainActivity.MSG_WHAT_OPEN_MSG;
//            Bundle data = new Bundle();
//            data.putString("DevKey", devKey);
//            data.putString("ChannelName", channelName);
//            MainActivity.handler.sendMessage(handlerMsg);

            Intent launch = context.getPackageManager().getLaunchIntentForPackage(
                    context.getPackageName());
            launch.addCategory(Intent.CATEGORY_LAUNCHER);
            launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(launch);
        }
    }
}

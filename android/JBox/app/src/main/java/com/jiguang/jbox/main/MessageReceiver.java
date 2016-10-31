package com.jiguang.jbox.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.Message;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;


public class MessageReceiver extends BroadcastReceiver {
    private Handler mHandler;

    public MessageReceiver(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {
            Bundle bundle = intent.getExtras();

            String extraJson = bundle.getString(JPushInterface.EXTRA_EXTRA);

            try {
                JSONObject jsonObject = new JSONObject(extraJson);

                String title = bundle.getString(JPushInterface.EXTRA_TITLE);
                String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);

                String devKey = jsonObject.getString("dev_key");
                String channelName = jsonObject.getString("channel");
                String iconUrl = jsonObject.getString("icon");   // 集成的图标 url。
                long timeMillis = Long.parseLong(jsonObject.getString("datetime"));

                Message msg = new Message();
                msg.title = title;
                msg.content = content;
                msg.devKey = devKey;
                msg.channelName = channelName;
                msg.iconUrl = iconUrl;
                msg.time = timeMillis;
                msg.save();

                Bundle data = new Bundle();

                android.os.Message handlerMsg = new android.os.Message();
                // 如果收到的是当前 Channel 的消息就更新界面，否则保存到数据库并更新界面。
                if (AppApplication.currentDevKey.equals(devKey) &&
                        AppApplication.currentChannelName.equals(channelName)) {
                    handlerMsg.what = MainActivity.MSG_WHAT_RECEIVE_MSG_CURRENT;
                    data.putSerializable("message", msg);
                    handlerMsg.setData(data);
                    mHandler.sendMessage(handlerMsg);
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
                        mHandler.sendMessage(handlerMsg);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}

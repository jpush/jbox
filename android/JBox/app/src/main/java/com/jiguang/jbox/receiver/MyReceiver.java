package com.jiguang.jbox.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jiguang.jbox.data.Message;
import com.jiguang.jbox.data.source.MessageDataSource;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class MyReceiver extends BroadcastReceiver {

    private static final List<String> IGNORED_EXTRAS_KEYS =
            Arrays.asList(
                    "cn.jpush.android.TITLE",
                    "cn.jpush.android.MESSAGE",
                    "cn.jpush.android.APPKEY",
                    "cn.jpush.android.NOTIFICATION_CONTENT_TITLE"
            );

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
    }

    private void onReceiveMessage(Intent intent) {
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String devKey = intent.getExtras().getString("dev_key");
        String channel = intent.getExtras().getString("channel");

        Message msg = new Message(title, content);
        msg.setChannelId(channel);
        msg.setDevKey(devKey);

        msg.setTime(String.valueOf(System.currentTimeMillis()));

        //TODO: 保存 msg 到本地，并刷新数据。

    }

}

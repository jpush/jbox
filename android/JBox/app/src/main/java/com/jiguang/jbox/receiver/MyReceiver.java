package com.jiguang.jbox.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.data.Message;
import com.jiguang.jbox.data.source.ChannelsRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

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
        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
            onReceiveMessage(intent);
        }
    }

    private void onReceiveMessage(Intent intent) {
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String devKey = intent.getExtras().getString("dev_key");
        String channel = intent.getExtras().getString("channel");

        Message msg = new Message(title, content);
        msg.setChannel(channel);
        msg.setDevKey(devKey);

        Date currentDate = new Date(System.currentTimeMillis());
        msg.setTime(currentDate.toString());

        //TODO: 保存 msg 到本地，并刷新数据。
        ChannelsRepository.getInstance().refreshChannels();

    }

}

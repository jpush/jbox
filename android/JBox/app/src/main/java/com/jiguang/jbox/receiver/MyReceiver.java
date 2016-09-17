package com.jiguang.jbox.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jiguang.jbox.data.Message;
import com.jiguang.jbox.data.source.MessageRepository;
import com.jiguang.jbox.data.source.local.MessagesLocalDataSource;

import cn.jpush.android.api.JPushInterface;


public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {
            onReceiveMessage(context, intent.getBundleExtra(JPushInterface.EXTRA_MESSAGE));
        }
    }

    private void onReceiveMessage(Context context, Bundle bundle) {
        String title = bundle.getString("title");
        String content = bundle.getString("content");
        String devKey = bundle.getString("dev_key");
        String channel = bundle.getString("channel");

        Message msg = new Message(title, content);
        msg.setChannelName(channel);
        msg.setDevKey(devKey);
        msg.setTime(String.valueOf(System.currentTimeMillis()));

        // 保存 msg 到本地，并刷新数据。
        MessagesLocalDataSource localDataSource = MessagesLocalDataSource.getInstance(context);
        MessageRepository repository = MessageRepository.getInstance(localDataSource);
        repository.saveMessage(msg);
    }

}

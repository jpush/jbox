package com.jiguang.jbox.data;

import java.util.UUID;

public class Message {

    private final String mId;

    private final String mTitle;

    private final String mContent;

    private String mTime;

    private String mChannelName;

    private String mDevKey;

    public Message(String title, String content) {
        mId = UUID.randomUUID().toString();
        mTitle = title;
        mContent = content;
    }

    public Message(String id, String title, String content) {
        mId = id;
        mTitle = title;
        mContent = content;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getContent() {
        return mContent;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getChannelName() {
        return mChannelName;
    }

    public void setChannelName(String channelName) {
        mChannelName = channelName;
    }

    public String getDevKey() {
        return mDevKey;
    }

    public void setDevKey(String devKey) {
        mDevKey = devKey;
    }
}

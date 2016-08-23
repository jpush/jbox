package com.jiguang.jbox.data;

public class Message {

    private final String mTitle;

    private final String mContent;

    private String mTime;

    private String mChannel;

    private String mDevKey;

    public Message(String title, String content) {
        mTitle = title;
        mContent = content;
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

    public String getChannel() {
        return mChannel;
    }

    public void setChannel(String channel) {
        mChannel = channel;
    }

    public String getDevKey() {
        return mDevKey;
    }

    public void setDevKey(String devKey) {
        mDevKey = devKey;
    }
}

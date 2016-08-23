package com.jiguang.jbox.data;

import java.util.List;
import java.util.Stack;
import java.util.UUID;

public class Channel {

    private String mId;

    private final String mName;

    private String mDescription;

    private String mDevKey;

    private int unReadMessageCount; // 未读消息数。

    private Stack<Message> mMessages;

    private Developer mDeveloper;

    private boolean isSubscribe;

    public Channel(String name) {
        mId = UUID.randomUUID().toString();
        mName = name;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public Message getLatestMessage() {
        return mMessages.pop();
    }

    public int getUnReadMessageCount() {
        return unReadMessageCount;
    }

    public void setUnReadMessageCount(int unReadMessageCount) {
        this.unReadMessageCount = unReadMessageCount;
    }

    public List<Message> getMessages() {
        return mMessages;
    }

    public void setMessages(Stack<Message> messages) {
        mMessages = messages;
    }

    public String getDevKey() {
        return mDevKey;
    }

    public void setDevKey(String devKey) {
        mDevKey = devKey;
    }

    public Developer getDeveloper() {
        return mDeveloper;
    }

    public void setDeveloper(Developer developer) {
        mDeveloper = developer;
    }

    public boolean isSubscribe() {
        return isSubscribe;
    }

    public void setSubscribe(boolean subscribe) {
        isSubscribe = subscribe;
    }
}

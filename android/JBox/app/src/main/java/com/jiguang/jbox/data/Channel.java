package com.jiguang.jbox.data;

import java.util.List;

public class Channel {

    private final String mName;

    private String mDescription;

    private boolean isSubscription; // 是否被订阅。

    private Message mLatestMessage; // 最近收到的消息。

    private int unReadMessageCount; // 未读消息数。

    private List<Message> mMessages;

    public Channel(String name) {
        mName = name;
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

    public boolean isSubscription() {
        return isSubscription;
    }

    public void setSubscription(boolean subscription) {
        isSubscription = subscription;
    }

    public Message getLatestMessage() {
        return mLatestMessage;
    }

    public void setLatestMessage(Message latestMessage) {
        mLatestMessage = latestMessage;
    }

    public int getUnReadMessageCount() {
        return unReadMessageCount;
    }

    public void setUnReadMessageCount(int unReadMessageCount) {
        this.unReadMessageCount = unReadMessageCount;
    }

    public boolean isAllReaded() {
        return unReadMessageCount == 0;
    }

    public List<Message> getMessages() {
        return mMessages;
    }

    public void setMessages(List<Message> messages) {
        mMessages = messages;
    }
}

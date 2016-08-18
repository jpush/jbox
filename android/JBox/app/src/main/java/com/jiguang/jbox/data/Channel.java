package com.jiguang.jbox.data;

import java.util.List;
import java.util.Stack;

public class Channel {

    private final String mName;

    private String mDescription;

    private Developer mDeveloper; // 订阅的 developer。若为空，代表没有被订阅。

    private int unReadMessageCount; // 未读消息数。

    private Stack<Message> mMessages;

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

    public Developer getDeveloper() {
        return mDeveloper;
    }

    public void setDevKey(Developer dev) {
        mDeveloper = dev;
    }

}

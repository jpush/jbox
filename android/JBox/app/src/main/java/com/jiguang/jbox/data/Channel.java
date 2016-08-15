package com.jiguang.jbox.data;

public class Channel {

    private final String mName;

    private int mBadge;

    private String mDescription;

    private boolean isSubscription; // 是否被订阅。

    public Channel(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setBadge(int badge) {
        mBadge = badge;
    }

    public int getBadge() {
        return mBadge;
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
}

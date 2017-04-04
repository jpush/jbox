package com.jiguang.jbox.data.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Channel")
public class Channel extends Model {

    @Column(name = "DevKey")
    public String devKey;

    @Column(name = "Name")
    public String name;

    @Column(name = "UnreadCount")
    public int unreadCount; // 未读消息数。

    @Column(name = "IsSubscribe")
    public boolean isSubscribe;    // 默认为订阅。

    public Channel() {
        super();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Channel)) {
            return false;
        }
        Channel objChannel = (Channel) obj;
        return objChannel.name.equals(name) && objChannel.isSubscribe == this.isSubscribe;
    }
}

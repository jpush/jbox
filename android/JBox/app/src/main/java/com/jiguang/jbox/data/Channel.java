package com.jiguang.jbox.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Channel")
public class Channel extends Model {

    @Column(name = "DevKey")
    public String devKey;

    @Column(name = "Name")
    public String name;

    @Column(name = "IconPath")
    public String iconPath;     // 集成的 icon 路径。

    @Column(name = "IconUrl")
    public String iconUrl;      // 集成的 icon URL。

    @Column(name = "UnreadCount")
    public int unreadCount; // 未读消息数。

    @Column(name = "IsSubscribe")
    public boolean isSubscribe = true;    // 默认为订阅。

    public Channel() {
        super();
    }

    public Channel(String devKey, String name, String iconPath, String desc,
                   int unreadCount, boolean isSubscribe) {
        super();
        this.devKey = devKey;
        this.name = name;
        this.iconPath = iconPath;
        this.unreadCount = unreadCount;
        this.isSubscribe = isSubscribe;
    }

    @Override
    public boolean equals(Object obj) {
        Channel objChannel = (Channel) obj;
        return objChannel.name.equals(name) && (objChannel.isSubscribe == isSubscribe);
    }
}

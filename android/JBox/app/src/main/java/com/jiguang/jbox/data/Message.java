package com.jiguang.jbox.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

@Table(name = "Message")
public class Message extends Model implements Serializable {

    @Column(name = "Title")
    public String title;

    @Column(name = "Content")
    public String content;

    @Column(name = "Time")
    public long time;

    @Column(name = "DevKey")
    public String devKey;

    @Column(name = "Channel")
    public String channelName;

    @Column(name = "IconUrl")
    public String iconUrl;

    @Column(name = "IntegrationName")
    public String integrationName;

    @Column(name = "Url")
    public String url;

    public Message() {
        super();
    }

    public Message(String devKey, String channelName, String title, String content, long time) {
        super();
        this.devKey = devKey;
        this.channelName = channelName;
        this.title = title;
        this.content = content;
        this.time = time;
    }

}

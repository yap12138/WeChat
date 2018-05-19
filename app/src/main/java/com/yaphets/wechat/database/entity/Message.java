package com.yaphets.wechat.database.entity;

import org.litepal.crud.DataSupport;

public class Message extends DataSupport {
    public static final int TYPE_RECEIVE = 0;
    public static final int TYPE_SEND = 1;

    private Friend friend;
    private String msg;
    private long timestamp;
    private int type;

    public Message(Friend friend, String msg, long timestamp, int type) {
        this.friend = friend;
        this.msg = msg;
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTimestamp() {
        if (timestamp == 0) {
            return System.currentTimeMillis();
        }
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

package com.yaphets.wechat.database.entity;

public class Message {
    public static final int TYPE_RECEIVE = 0;
    public static final int TYPE_SEND = 1;

    private String msg;
    private long timestamp;

    public Message(String msg, long timestamp) {
        this.msg = msg;
        this.timestamp = timestamp;
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
}

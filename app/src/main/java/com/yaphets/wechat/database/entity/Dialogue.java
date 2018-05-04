package com.yaphets.wechat.database.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Dialogue {

    private Friend friend;
    private List<Message> msgList;

    public Dialogue(Friend friend, List<Message> msgList) {
        this.friend = friend;
        if (msgList == null) {
            this.msgList = new ArrayList<>();
        } else {
            this.msgList = msgList;
        }
    }

    public Dialogue(Friend friend) {
        this(friend, null);
    }

    public Friend getFriend() {
        return friend;
    }

    public List<Message> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<Message> msgList) {
        this.msgList = msgList;
    }

    public String getLastTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("a h:mm", Locale.CHINA);
        if (msgList.size() == 0) {
            return dateFormat.format(new Date(System.currentTimeMillis()));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date date = new Date(msgList.get(msgList.size()-1).getTimestamp());

        if (date.before(calendar.getTime())) {
            dateFormat.applyPattern("M月d日");
        }
        return dateFormat.format(date);
    }

    public String getLastMsg() {
        if (msgList.size() == 0 || msgList.get(msgList.size()-1).getMsg() == null) {
            return "";
        }

        return msgList.get(msgList.size()-1).getMsg();
    }
}

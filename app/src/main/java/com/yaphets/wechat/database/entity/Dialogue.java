package com.yaphets.wechat.database.entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Dialogue {

    private Friend friend;

    private long createTime;

    private int nonRead;

    public Dialogue(Friend friend) {
        this.friend = friend;
        this.nonRead = 0;
        this.createTime = System.currentTimeMillis();
    }


    public Friend getFriend() {
        return friend;
    }

    public List<Message> getMsgList() {
        return friend.getMessages();
    }

    public int getNonRead() { return this.nonRead; }

    public void setNonRead(int nonRead) { this.nonRead = nonRead;}

    public String getLastTime() {
        List<Message> msgList = friend.getMessages();
        SimpleDateFormat dateFormat = new SimpleDateFormat("a h:mm", Locale.CHINA);
        if (msgList.size() == 0) {
            return dateFormat.format(new Date(createTime));
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

    /**
     * 获取最新消息的时间戳
     * @return
     * long类型的时间戳
     */
    public long getLastTimestamp() {
        int lsize = friend.getMessages().size();
        return lsize==0?createTime:friend.getMessages().get(lsize-1).getTimestamp();
    }

    public String getLastMsg() {
        List<Message> msgList = friend.getMessages();
        if (msgList.size() == 0 || msgList.get(msgList.size()-1).getMsg() == null) {
            return "";
        }

        return msgList.get(msgList.size()-1).getMsg();
    }
}

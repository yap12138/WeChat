package com.yaphets.wechat.database.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.R;
import com.yaphets.wechat.util.PhotoUtils;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class Friend extends DataSupport {
    @Column(unique = true)
    private String username;
    private String nickname;
    private String description;
    private byte[] thumb;
    private long update_time; //用于比对本地版本是否最新

    private List<Message> messages = new ArrayList<>();

    @Column(ignore = true)
    private Bitmap thumbBitmap;
    @Column(ignore = true)
    private int friendshipPolicy;

    public Friend() {

    }

    private Friend(String nickname, byte[] thumb) {
        this.nickname = nickname;
        this.thumb = thumb;
    }

    public Friend(String username, String nickname, String description, byte[] thumb) {
        this(nickname, thumb);
        this.description = description;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getThumb() {
        return thumb;
    }

    public void setThumb(byte[] thumb) {
        this.thumb = thumb;
    }

    public long getUpdateTime() {
        return update_time;
    }

    public void setUpdateTime(long update_time) {
        this.update_time = update_time;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    /**
     * 延时加载Bitmap
     * @return
     * byte[]生成的Bitmap对象
     */
    public Bitmap getThumbBitmap() {
        if (thumbBitmap == null) {
            if (thumb == null || thumb.length == 0) {
                this.thumbBitmap = BitmapFactory.decodeResource(ClientApp.getContext().getResources(), R.drawable.default_thumb);
            } else {
                this.thumbBitmap = PhotoUtils.getBitmap(thumb);
            }
        }
        return thumbBitmap;
    }

    public int getFriendshipPolicy() {
        return friendshipPolicy;
    }

    public void setFriendshipPolicy(int friendshipPolicy) {
        this.friendshipPolicy = friendshipPolicy;
    }

    @Override
    public boolean equals(Object obj) {
        boolean flag = false;
        if (obj instanceof Friend) {
            Friend oth = (Friend) obj;
            flag = this.username.equals(oth.getUsername());
        }else if (obj instanceof String) {  //以用户名判断
            String username = (String) obj;
            flag = this.username.equals(username);
        }

        return flag;
    }
}

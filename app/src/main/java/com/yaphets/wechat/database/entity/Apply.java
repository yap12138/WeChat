package com.yaphets.wechat.database.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.R;
import com.yaphets.wechat.util.PhotoUtils;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

public class Apply extends DataSupport{
    private int fromId;
    private String username;
    private String nickname;
    private byte[] thumb;
    /**
     * 0表示  还未处理的好友申请
     * 1表示  接受好友申请
     */
    private int status;
    private String msg;

    @Column(ignore = true)
    Bitmap thumbBitmap;

    public Apply(int fromId, String username, String nickname, byte[] thumb, int status, String msg) {
        this.fromId = fromId;
        this.username = username;
        this.nickname = nickname;
        this.thumb = thumb;
        this.status = status;
        this.msg = msg;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
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

    public byte[] getThumb() {
        return thumb;
    }

    public void setThumb(byte[] thumb) {
        this.thumb = thumb;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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
}

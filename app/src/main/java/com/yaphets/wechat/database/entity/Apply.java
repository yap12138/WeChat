package com.yaphets.wechat.database.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.R;
import com.yaphets.wechat.util.PhotoUtils;

import org.litepal.crud.DataSupport;

public class Apply extends DataSupport implements Parcelable {
    private int fromId;
    private String username;
    private String nickname;
    private byte[] thumb;
    /**
     * 0表示  还未处理的好友申请
     * 1表示  接受好友申请
     */
    private byte status;
    private String msg;

    Bitmap thumbBitmap;

    public Apply(int fromId, String username, String nickname, byte[] thumb, byte status, String msg) {
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

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(fromId);
        dest.writeString(username);
        dest.writeString(nickname);
        dest.writeByteArray(thumb);
        dest.writeByte(status);
        dest.writeString(msg);
    }

    protected Apply(Parcel in) {
        fromId = in.readInt();
        username = in.readString();
        nickname = in.readString();
        thumb = in.createByteArray();
        status = in.readByte();
        msg = in.readString();
    }

    public static final Creator<Apply> CREATOR = new Creator<Apply>() {
        @Override
        public Apply createFromParcel(Parcel in) {
            return new Apply(in);
        }

        @Override
        public Apply[] newArray(int size) {
            return new Apply[size];
        }
    };
}

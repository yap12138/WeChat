package com.yaphets.wechat.database.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.R;
import com.yaphets.wechat.util.PhotoUtils;

import org.litepal.crud.DataSupport;

public class Friend extends DataSupport implements Parcelable {
    private String username;
    private String nickname;
    private String description;
    private byte[] thumb;
    private long update_time; //用于比对本地版本是否最新

    Bitmap thumbBitmap;
    int friendshipPolicy;

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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(nickname);
        dest.writeString(description);
        dest.writeByteArray(thumb);
        dest.writeLong(update_time);

        dest.writeInt(friendshipPolicy);
    }

    protected Friend(Parcel in) {
        username = in.readString();
        nickname = in.readString();
        description = in.readString();
        thumb = in.createByteArray();
        update_time = in.readLong();

        friendshipPolicy = in.readInt();
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {
        @Override
        public Friend createFromParcel(Parcel in) {
            return new Friend(in);
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };

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

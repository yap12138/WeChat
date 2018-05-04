package com.yaphets.wechat.database.entity;

import android.graphics.Bitmap;

//TODO 存储本地数据，blog数据存储图片
public class UserInfo {
    private int _uid;
    private String _username;
    private String _password;

    private String _nickname;
    private String _desc;

    private Bitmap _thumb;

    public UserInfo() {

    }

    public UserInfo(String _username, String _password, String _nickname, String _desc) {
        this._username = _username;
        this._password = _password;
        this._nickname = _nickname;
        this._desc = _desc;
    }

    public int get_uid() {
        return _uid;
    }

    public void set_uid(int _uid) {
        this._uid = _uid;
    }

    public String get_username() {
        return _username;
    }

    public void set_username(String _username) {
        this._username = _username;
    }

    public String get_password() {
        return _password;
    }

    public void set_password(String _password) {
        this._password = _password;
    }

    public String get_nickname() {
        return _nickname;
    }

    public void set_nickname(String _nickname) {
        this._nickname = _nickname;
    }

    public String get_desc() {
        return _desc;
    }

    public void set_desc(String _desc) {
        this._desc = _desc;
    }

    public Bitmap get_thumb() {
        return _thumb;
    }

    public void set_thumb(Bitmap _thumb) {
        this._thumb = _thumb;
    }
}

package com.yaphets.wechat.util;

public class RequestParam {
    public static String USERNAME = "username";
    public static String PASSWORD = "password";
    public static String STATUS = "loginStatus";
    public static int ONLINE = 1;
    public static int OFFLINE = 0;

    private String _username;
    private String _password;
    private String _nickname;

    public void setNickname(String nickname) {
        this._nickname = nickname;
    }

    public void setUsername(String username) {
        this._username = username;
    }

    public void setPassword(String password) {
        this._password = password;
    }

    public String getNickname() {
        return _nickname;
    }

    public String getUsername() {
        return _username;
    }

    public String getPassword() {
        return _password;
    }
}

package com.yaphets.wechat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.yaphets.wechat.database.entity.Friend;
import com.yaphets.wechat.database.entity.UserInfo;
import com.yaphets.wechat.util.RequestParam;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;
import org.litepal.LitePalDB;

import java.util.HashMap;
import java.util.List;

public class ClientApp extends LitePalApplication {
    private static final String TAG = "ClientApp";

    private static Context _context;

    private SharedPreferences _loginShared;
    private static UserInfo _loginUserinfo;

    public static String _username;
    public static String _password;

    public static final String _serverIP = "120.78.139.85";
    //public static final String _serverIP = "172.16.31.9";

    public static final int _serverPort = 8495;

    public static final String _serverRootDir = "app_wechat";

    public static Handler _handler = new Handler();

    public static List<Friend> _friendsList;

    private static HashMap<String, Object> _attribute = new HashMap<>();

    private static TextBadgeItem _msgNotifyBadge;
    private static TextBadgeItem _applyNotifyBadge;

    @Override
    public void onCreate() {
        super.onCreate();

        _context = getApplicationContext();

        SharedPreferences shared = getLoginShared();
        _username = shared.getString(RequestParam.USERNAME, null);
        _password = shared.getString(RequestParam.PASSWORD, null);

        //Connector.getDatabase();

    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "onTerminate executed");
        super.onTerminate();
    }

    public static Context getContext(){
        return _context;
    }

    public static void initLitePalDB() {
        //每个账户一个以用户名命名的数据库
        LitePalDB litePalDB = LitePalDB.fromDefault(_username);
        LitePal.use(litePalDB);
    }

    public SharedPreferences getLoginShared() {
        //根据lastest_login.xml中当前登陆成功的用户名，获取对应的<用户名>.xml
        SharedPreferences shared = getSharedPreferences("lastest_login", Context.MODE_PRIVATE);
        String userName = shared.getString(RequestParam.USERNAME,"");

        this._loginShared = getSharedPreferences(userName, Context.MODE_PRIVATE);
        return  this._loginShared;
    }

    public void setLoginShared(String user_name, String ip) {
        SharedPreferences shared = this.getSharedPreferences("lastest_login", Context.MODE_PRIVATE);
        //保存登陆成功的用户名到lastest_login.xml中
        shared.edit().putString(RequestParam.USERNAME, user_name).apply();
    }

    public static UserInfo get_loginUserinfo() {
        return _loginUserinfo;
    }

    public static void set_loginUserinfo(UserInfo loginUserinfo) {
        _loginUserinfo = loginUserinfo;
    }

    public static Object getAttribute(String key) {
        return _attribute.get(key);
    }

    public static void setAttribute(String key, Object value) {
        _attribute.put(key, value);
    }

    public static TextBadgeItem getMsgNotifyBadge() {
        return _msgNotifyBadge;
    }

    public static void setMsgNotifyBadge(TextBadgeItem _msgNotifyBadge) {
        ClientApp._msgNotifyBadge = _msgNotifyBadge;
    }

    public static TextBadgeItem getApplyNotifyBadge() {
        return _applyNotifyBadge;
    }

    public static void setApplyNotifyBadge(TextBadgeItem _applyNotifyBadge) {
        ClientApp._applyNotifyBadge = _applyNotifyBadge;
    }
}

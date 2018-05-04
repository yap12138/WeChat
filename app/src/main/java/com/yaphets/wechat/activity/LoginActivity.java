package com.yaphets.wechat.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.R;
import com.yaphets.wechat.asynctask.GetThumbTask;
import com.yaphets.wechat.asynctask.LoginTask;
import com.yaphets.wechat.asynctask.PullFriendsTask;
import com.yaphets.wechat.database.dao.MySqlHelper;
import com.yaphets.wechat.database.entity.UserInfo;
import com.yaphets.wechat.util.CallbackListener;
import com.yaphets.wechat.util.DialogUtils;
import com.yaphets.wechat.util.HttpCallbackListener;
import com.yaphets.wechat.util.RequestParam;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int PREPARE_OK = 77;

    private EditText _edit_username;
    private EditText _edit_password;
    private Dialog _loadingDialog;
    private Handler _handler;
    private int barrier = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        _handler = new LoginHandler();
        findAllView();
        checkStatus();
        Log.d(TAG, "onCreate: executed");
    }

    private void findAllView(){
        Button _btn_Login = findViewById(R.id.btn_login);
        Button _btn_Register = findViewById(R.id.btn_register);

        _edit_username = findViewById(R.id.la_edit_username);
        _edit_password = findViewById(R.id.la_edit_password);

        _btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        _btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = _edit_username.getText().toString();
                String password = _edit_password.getText().toString();

                RequestParam rp = new RequestParam();
                rp.setUsername(username);
                rp.setPassword(password);

                new LoginTask(new HttpCallbackListener<String>() {
                    @Override
                    public void onFinish(String jsonStr) {
                        loginSucceed(jsonStr);
                    }

                    @Override
                    public void onError(String flag) {
                        Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
                    }
                }).execute(rp);

            }
        });
    }

    public void loginSucceed(String str){
        Log.d(TAG, "loginSucceed: checked ok");
        saveLoginInfo();
        //切换登陆用户数据库
        ClientApp.initLitePalDB();
        UserInfo userInfo = ClientApp.get_loginUserinfo();

        try {
            JSONObject json = new JSONObject(str);

            userInfo.set_uid(json.getInt("id"));
            userInfo.set_nickname(json.getString("nickname"));
            if (json.has("desc"))
                userInfo.set_desc(json.getString("desc"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        _loadingDialog = DialogUtils.createLoadingDialog(LoginActivity.this, "");
        //TODO 拉取好友信息，非拉取所有数据，先和本地数据库比对（通过最新更新时间戳比对），有更新则拉回来
        new PullFriendsTask(new CallbackListener<Integer>() {
            @Override
            public void run(Integer arg) {
                barrier++;
                Message msg = _handler.obtainMessage(PREPARE_OK);
                _handler.sendMessage(msg);
            }
        }).execute();
        //获取头像
        new GetThumbTask(new HttpCallbackListener<byte[]>() {
            @Override
            public void onFinish(byte[] bytes) {
                afterGetThumb(bytes);
                barrier++;
                Message msg = _handler.obtainMessage(PREPARE_OK);
                _handler.sendMessage(msg);
            }

            @Override
            public void onError(byte[] bytes) {

            }
        }).execute(userInfo.get_nickname());

        //设置在线
        MySqlHelper.updateStatus();
    }

    /**
     * 检查是否有保存账户密码
     */
    private void checkStatus(){
        if (ClientApp._username != null)
            _edit_username.getText().append(ClientApp._username);
        if (ClientApp._password != null)
            _edit_password.getText().append(ClientApp._password);
    }

    /**
     * 保存登陆信息
     */
    private void saveLoginInfo() {
        ClientApp app = (ClientApp) getApplication();

        ClientApp._username = _edit_username.getText().toString();
        ClientApp._password = _edit_password.getText().toString();

        UserInfo user = new UserInfo();
        user.set_username(ClientApp._username);
        user.set_password(ClientApp._password);
        ClientApp.set_loginUserinfo(user);

        app.setLoginShared(ClientApp._username, ClientApp._serverIP);

        SharedPreferences saveShared = app.getLoginShared();
        SharedPreferences.Editor editor = saveShared.edit();
        //保存账号密码
        editor.putString(RequestParam.USERNAME, ClientApp._username);
        editor.putString(RequestParam.PASSWORD, ClientApp._password);
        editor.putInt(RequestParam.STATUS, RequestParam.ONLINE);
        editor.apply();
    }

    /**
     * 回调
     * @param data
     *      头像数据
     */
    private void afterGetThumb(byte[] data) {
        Bitmap thumb;
        if (data != null && data.length != 0) {
            thumb = BitmapFactory.decodeByteArray(data, 0, data.length);
        } else {
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.default_thumb);
        }

        UserInfo userInfo = ClientApp.get_loginUserinfo();
        userInfo.set_thumb(thumb);
    }

    class LoginHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PREPARE_OK:
                    if (barrier >= 2) {
                        _loadingDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        //进入主界面
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
            }
        }
    }
}

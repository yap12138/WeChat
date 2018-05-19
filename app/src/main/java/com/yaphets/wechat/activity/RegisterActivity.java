package com.yaphets.wechat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yaphets.wechat.R;
import com.yaphets.wechat.asynctask.RegisterTask;
import com.yaphets.wechat.util.RequestParam;
import com.yaphets.wechat.util.listener.HttpCallbackListener;

public class RegisterActivity extends AppCompatActivity {

    private EditText _username;
    private EditText _password;
    private EditText _name;

    private Button _registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findAllViews();
    }

    private void findAllViews() {
        _username = findViewById(R.id.ra_edit_username);
        _password = findViewById(R.id.ra_edit_password);
        _name = findViewById(R.id.ra_edit_name);

        _registerBtn = findViewById(R.id.ra_btn_register);

        _registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = _username.getText().toString();
                String pwd = _password.getText().toString();
                String name = _name.getText().toString();
                if ("".equals(username)) {
                    Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else if ("".equals(pwd)) {
                    Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else if ("".equals(name)) {
                    Toast.makeText(RegisterActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestParam requestParam = new RequestParam();
                requestParam.setUsername(username);
                requestParam.setPassword(pwd);
                requestParam.setNickname(name);

                new RegisterTask(new HttpCallbackListener<Integer>() {
                    @Override
                    public void onFinish(Integer flag) {
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(Integer flag) {
                        if (flag == -1) {
                            Toast.makeText(RegisterActivity.this, "已有相同账户", Toast.LENGTH_SHORT).show();
                        } else if (flag == -2) {
                            Toast.makeText(RegisterActivity.this, "已有相同昵称", Toast.LENGTH_SHORT).show();
                        } else if (flag == -3) {
                            Toast.makeText(RegisterActivity.this, "服务器错误,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).execute(requestParam);
            }
        });
    }
}

package com.yaphets.wechat.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.R;
import com.yaphets.wechat.asynctask.AddFriendTask;
import com.yaphets.wechat.database.entity.Friend;
import com.yaphets.wechat.util.listener.OpenDialogueListener;

public class FriendDetailActivity extends BaseActionBarActivity {
    private Friend _friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);

        _friend = getIntent().getParcelableExtra("friend");
        if (ClientApp._friendsMap.containsKey(_friend.getNickname())) {
            _friend = ClientApp._friendsMap.get(_friend.getNickname());
        }

        initView();
        initData();
    }

    private void initView() {
        ImageView img = findViewById(R.id.fda_iv_thumb);

        TextView nickname = findViewById(R.id.fda_tv_nickname);

        TextView username = findViewById(R.id.fda_tv_username);

        TextView desc = findViewById(R.id.fda_tv_desc);

        if (_friend != null) {
            img.setImageBitmap(_friend.getThumbBitmap());
            nickname.setText(_friend.getNickname());
            username.setText(_friend.getUsername());
            desc.setText(_friend.getDescription());
        }
    }

    private void initData() {
        Button add_btn = findViewById(R.id.fda_btn_add);
        Button send_btn = findViewById(R.id.fda_btn_send);
        if (ClientApp._friendsMap.containsValue(_friend)) {
            add_btn.setVisibility(View.GONE);
            //打开聊天 逻辑处理
            send_btn.setOnClickListener(new OpenDialogueListener(_friend));
        } else {
            send_btn.setVisibility(View.GONE);
            if (_friend.getFriendshipPolicy() == 3) {
                add_btn.setVisibility(View.GONE);
            } else {
                add_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //添加好友 逻辑处理 打开对话框填写验证信息
                        showInputDialog();
                    }
                });
            }
        }
    }

    private void showInputDialog() {
        /*@setView 装入一个EditView*/
        final EditText editText = new EditText(FriendDetailActivity.this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder inputDialog = new AlertDialog.Builder(FriendDetailActivity.this);

        inputDialog.setTitle("输入验证信息");
        inputDialog.setMessage("").setView(editText);

        inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //输入申请消息
                String apply_msg = editText.getText().toString();
                new AddFriendTask().execute(_friend.getUsername(), apply_msg);
            }
        });
        inputDialog.setNegativeButton("取消", null);
        inputDialog.show();
    }
}

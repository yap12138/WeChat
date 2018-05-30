package com.yaphets.wechat.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yaphets.wechat.R;
import com.yaphets.wechat.adapter.GroupMsgAdapter;
import com.yaphets.wechat.asynctask.SendGroupMsgTask;
import com.yaphets.wechat.database.entity.Friend;
import com.yaphets.wechat.database.entity.Message;

public class GroupChatsActivity extends BaseActionBarActivity {
    private static final String _title = "官方群聊";
    private int _userCount;

    private RecyclerView _msgList;

    private Button _sendBtn;
    private EditText _msgText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chats);

        initView();
        initData();
    }

    private void initView() {
        _msgList = findViewById(R.id.rv_msg);
        _sendBtn = findViewById(R.id.btn_send);
        _msgText = findViewById(R.id.et_msg);
    }

    private void initData() {
        setResult(RESULT_OK);
        //title
        _userCount = getIntent().getIntExtra("groupSize", 0);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(_title + "(" + _userCount + ")");
        //init msgList
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        _msgList.setLayoutManager(linearLayoutManager);
        GroupMsgAdapter groupMsgAdapter = GroupMsgAdapter.createInstance();
        _msgList.setAdapter(groupMsgAdapter);

        _sendBtn.setOnClickListener(o->{
            String msgStr = _msgText.getText().toString();
            if ("".equals(msgStr)) {
                return;
            }

            _msgText.getText().clear();
            Message msg = new Message(null, msgStr, System.currentTimeMillis(), Message.TYPE_SEND);
            GroupMsgAdapter.getInstance().addMsg(msg);
            GroupMsgAdapter.getInstance().notifyDataSetChanged();
            _msgList.scrollToPosition(_msgList.getAdapter().getItemCount() - 1);
            //send task to remote server
            new SendGroupMsgTask().execute(msgStr);
        });
    }

    public void userEnter(String username) {
        Friend friend = GroupMsgAdapter.getInstance().getFriend(username);
        getSupportActionBar().setTitle(_title + "(" + (++_userCount) + ")");
        Toast.makeText(GroupChatsActivity.this, "新用户:" + friend.getNickname() + "加入群聊", Toast.LENGTH_SHORT).show();
    }

    public void userExit(String username) {
        Friend friend = GroupMsgAdapter.getInstance().removeFriend(username);
        getSupportActionBar().setTitle(_title + "(" + (--_userCount) + ")");
        if (friend != null) {
            Toast.makeText(GroupChatsActivity.this, "用户:" + friend.getNickname() + "退出群聊", Toast.LENGTH_SHORT).show();
        }
    }

    public void addMsg(Message msg) {
        GroupMsgAdapter.getInstance().addMsg(msg);
        GroupMsgAdapter.getInstance().notifyDataSetChanged();
        _msgList.scrollToPosition(_msgList.getAdapter().getItemCount() - 1);
    }
}

package com.yaphets.wechat.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.R;
import com.yaphets.wechat.adapter.DialogueAdapter;
import com.yaphets.wechat.adapter.MsgAdapter;
import com.yaphets.wechat.asynctask.SendMsgTask;
import com.yaphets.wechat.database.entity.Dialogue;
import com.yaphets.wechat.database.entity.Friend;
import com.yaphets.wechat.database.entity.Message;

/**
 * launch mode = singleTask
 */
public class CommunicateActivity extends BaseActionBarActivity {

    private RecyclerView _msgList;

    private Button _sendBtn;
    private EditText _msgText;
    private Dialogue _dialogue;

    private String _fnname;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicate);

        initView();
        initData();
    }

    /**
     * 重点！！！ 因为CommunicateActivity的启动模式是single task， 如果打开了好友B的聊天界面时，A发消息过来，此时点击通知打开同一个activity时不会改变的（因为single task）。
     * 但是单击会传递intent进来，在这里比较是否是与B相同的name，不相同则先finish当前activity，再以新的intent启动CommunicateActivity
     * @param intent
     * 新传进来的intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        if (!_fnname.equals(intent.getStringExtra("fri_key"))) {
            finish();
            ClientApp.getContext().startActivity(intent);
        }
        super.onNewIntent(intent);
        //setIntent(intent);
    }

    private void initView() {
        _msgList = findViewById(R.id.ca_rv_msg);
        _sendBtn = findViewById(R.id.ca_btn_send);
        _msgText = findViewById(R.id.ca_et_msg);
    }

    private void initData() {
        Intent intent = getIntent();
        _fnname = intent.getStringExtra("fri_key");
        //标题栏换昵称
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(_fnname);
        }
        //移除通知
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null)
            manager.cancel(ClientApp._friendsMap.get(_fnname).hashCode());

        if (!ClientApp._dialogueMap.containsKey(_fnname)) {
            Friend tmp = ClientApp._friendsMap.get(_fnname);
            Dialogue dialogue = new Dialogue(tmp);
            ClientApp._dialogueMap.put(_fnname, dialogue);
            DialogueAdapter.getInstance().updateDataSetWithNotify(ClientApp._dialogueMap);
        }
        _dialogue = ClientApp._dialogueMap.get(_fnname);

        // load msg
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        _msgList.setLayoutManager(linearLayoutManager);
        MsgAdapter adapter = new MsgAdapter(_dialogue.getFriend());
        _msgList.setAdapter(adapter);
        //滑动到底部
        _msgList.scrollToPosition(_msgList.getAdapter().getItemCount() - 1);
        //init send msg listener
        _sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgStr = _msgText.getText().toString();
                if ("".equals(msgStr)) {
                    return;
                }
                _msgText.getText().clear();
                Friend friend =  _dialogue.getFriend();
                Message msg = new Message(friend, msgStr, System.currentTimeMillis(), Message.TYPE_SEND);
                friend.getMessages().add(msg);

                //notify chat fragment's Dialogue Adapter
                DialogueAdapter.getInstance().sortDataSetWithNotify();
                //notify msg list
                _msgList.getAdapter().notifyDataSetChanged();
                _msgList.scrollToPosition(_msgList.getAdapter().getItemCount() - 1);
                //president task to remote server
                new SendMsgTask(msg).execute();
            }
        });

        //消去小红点
        ClientApp.getMsgNotifyBadge().setNumber(ClientApp.getMsgNotifyBadge().getNumber() - _dialogue.getNonRead());
        _dialogue.setNonRead(0);
    }

    /**
     * 获取当前聊天页面好友的昵称
     * @return
     * 好友的昵称
     */
    public String getFnname() {
        return _fnname;
    }

    public void notifyListChange() {
        _msgList.getAdapter().notifyDataSetChanged();
        _msgList.scrollToPosition(_msgList.getAdapter().getItemCount() - 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ca_toolbar, menu);
        return true;
    }
}

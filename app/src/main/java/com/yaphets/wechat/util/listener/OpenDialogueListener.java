package com.yaphets.wechat.util.listener;

import android.content.Intent;
import android.view.View;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.activity.CommunicateActivity;
import com.yaphets.wechat.database.entity.Friend;

public class OpenDialogueListener implements View.OnClickListener {

    private Friend _friend;

    public OpenDialogueListener(Friend friend) {
        this._friend = friend;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(ClientApp.getContext(), CommunicateActivity.class);
        intent.putExtra("fri_key", _friend.getNickname());
        ClientApp.getContext().startActivity(intent);
    }
}

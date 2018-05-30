package com.yaphets.wechat.ui.fragment;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yaphets.wechat.R;
import com.yaphets.wechat.activity.GroupChatsActivity;
import com.yaphets.wechat.asynctask.AttachGroupChatsTask;
import com.yaphets.wechat.asynctask.DetachGroupChatsTask;
import com.yaphets.wechat.util.listener.HttpCallbackListener;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoveryFragment extends BaseFragment {

    private static final int CODE_ATTACH_GROUP_CHATS = 0x77;

    private LinearLayout _groupChatLayout;

    public DiscoveryFragment() {
        // Required empty public constructor
    }

    @Override
    protected void loadData() {
        _groupChatLayout.setOnClickListener(v -> {
            //enter group chats task
            new AttachGroupChatsTask(new HttpCallbackListener<Integer>() {
                @Override
                public void onFinish(Integer groupSize) {
                    Intent intent = new Intent(mContext, GroupChatsActivity.class);
                    intent.putExtra("groupSize", groupSize);
                    startActivityForResult(intent, CODE_ATTACH_GROUP_CHATS);
                }

                @Override
                public void onError(Integer errorCode) {
                    Toast.makeText(mContext, "connected failed.", Toast.LENGTH_SHORT).show();
                }
            }).execute();

        });
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_discovery, container, false);
        _groupChatLayout = root.findViewById(R.id.group_chats);
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_ATTACH_GROUP_CHATS:
                    new DetachGroupChatsTask().execute();
                    break;
                default:
            }
        }
    }
}

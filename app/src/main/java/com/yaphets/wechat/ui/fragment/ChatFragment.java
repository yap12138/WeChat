package com.yaphets.wechat.ui.fragment;


import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.R;
import com.yaphets.wechat.adapter.DialogueAdapter;
import com.yaphets.wechat.database.entity.Dialogue;
import com.yaphets.wechat.database.entity.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends BaseFragment {

    private RecyclerView _dialogueList;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    protected void loadData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        _dialogueList.setLayoutManager(linearLayoutManager);
        //TODO load dialogue
        List<Dialogue> list = initDialogueList();

        DialogueAdapter adapter = new DialogueAdapter(list);
        _dialogueList.setAdapter(adapter);
        _dialogueList.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);
        _dialogueList = root.findViewById(R.id.cf_rv_dialogue);
        return root;
    }

    private List<Dialogue> initDialogueList() {
        List<Dialogue> list = new ArrayList<>();
        if (ClientApp._friendsList != null && ClientApp._friendsList.size() > 1) {
            Message msg1 = new Message("123", System.currentTimeMillis() - 515130);
            Message msg2 = new Message("7777777", System.currentTimeMillis() - 36550500);
            Dialogue dia1 = new Dialogue(ClientApp._friendsList.get(0));
            List<Message> msgL1 = new ArrayList<Message>();
            msgL1.add(msg1);
            dia1.setMsgList(msgL1);
            list.add(dia1);
            Dialogue dia2 = new Dialogue(ClientApp._friendsList.get(1));
            List<Message> msgL2 = new ArrayList<Message>();
            msgL2.add(msg2);
            dia2.setMsgList(msgL2);
            list.add(dia2);
        }
        return list;
    }
}

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
import com.yaphets.wechat.database.entity.Friend;
import com.yaphets.wechat.util.comparator.DialogueComparator;

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

        DialogueAdapter adapter = DialogueAdapter.createInstance(list);
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
        for (Friend friend : ClientApp._friendsMap.values()) {
            if (friend.getMessages().size() != 0) {
                Dialogue dialogue = new Dialogue(friend);
                ClientApp._dialogueMap.put(friend.getNickname(), dialogue);
            }
        }
        List<Dialogue> list = new ArrayList<>(ClientApp._dialogueMap.values());
        list.sort(new DialogueComparator());
        return list;
    }
}

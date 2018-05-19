package com.yaphets.wechat.ui.fragment;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.R;
import com.yaphets.wechat.activity.NewFriendActivity;
import com.yaphets.wechat.adapter.ContactAdapter;
import com.yaphets.wechat.asynctask.PullFriendRequestTask;
import com.yaphets.wechat.database.entity.Apply;
import com.yaphets.wechat.database.entity.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends BaseFragment {

    private RecyclerView _contactList;
    private ContactAdapter _adapter;
    private LinearLayout _newFriend;
    private ImageView _redPoint;

    private List<Apply> _applies = new ArrayList<>();

    public ContactFragment() {

        // Required empty public constructor
    }

    @Override
    protected void loadData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        _contactList.setLayoutManager(linearLayoutManager);

        //load contact
        _adapter = ContactAdapter.createInstance(ClientApp._friendsMap);
        _contactList.setAdapter(_adapter);
        _contactList.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));

        //new friend callback
        _newFriend.setOnClickListener(v -> {
            if (_redPoint.getVisibility() == View.VISIBLE)
                _redPoint.setVisibility(View.INVISIBLE);
            if (!ClientApp.getApplyNotifyBadge().isHidden())
                ClientApp.getApplyNotifyBadge().hide();
            Intent intent = new Intent(mContext, NewFriendActivity.class);
            //intent.putParcelableArrayListExtra("applyList", _applies);
            startActivity(intent);
        });

        new PullFriendRequestTask(_applies, this::notifyApply).execute();
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_contact, container, false);
        _contactList = root.findViewById(R.id.cf_rv_dialogue);
        _newFriend = root.findViewById(R.id.cf_new_friend);
        _redPoint = root.findViewById(R.id.iv_red_point);
        return root;
    }

    public List<Apply> getApplies() {
        return _applies;
    }

    /**
     * 提醒有size个新好友申请
     * @param size
     * 新好友申请的个数
     */
    public void notifyApply(int size) {
        _redPoint.setVisibility(View.VISIBLE);
        ClientApp.getApplyNotifyBadge().setNumber(ClientApp.getApplyNotifyBadge().getNumber() + size).show();
    }

    /**
     * 添加好友并保存好友信息至本地数据库，更新通讯录界面
     * @param friend
     * 新好友
     */
    public void AddFriend(Friend friend) {
        friend.saveOrUpdate();
        ClientApp._friendsMap.put(friend.getNickname(), friend);

        _adapter.updateDataSet(ClientApp._friendsMap);
    }
}

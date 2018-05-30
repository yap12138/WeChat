package com.yaphets.wechat.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.R;
import com.yaphets.wechat.activity.FriendDetailActivity;
import com.yaphets.wechat.database.dao.MySqlHelper;
import com.yaphets.wechat.database.entity.Friend;
import com.yaphets.wechat.database.entity.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class GroupMsgAdapter extends RecyclerView.Adapter<GroupMsgAdapter.ViewHolder> {

    private static GroupMsgAdapter mGroupMsgAdapter;

    private List<Message> _messages;

    /**
     * Key = username
     */
    private Map<String, Friend> _friendCache;

    public static GroupMsgAdapter createInstance() {
        if (mGroupMsgAdapter != null) {
            mGroupMsgAdapter._friendCache.clear();
            mGroupMsgAdapter._messages.clear();
        }

        mGroupMsgAdapter = new GroupMsgAdapter();
        return mGroupMsgAdapter;
    }

    public static GroupMsgAdapter getInstance() {
        if (mGroupMsgAdapter == null) {
            throw new RuntimeException("mContactAdapter never create");
        }
        return mGroupMsgAdapter;
    }

    private GroupMsgAdapter() {
        _messages = new Vector<>();
        _friendCache = new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == Message.TYPE_RECEIVE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_left, parent, false);
        } else if (viewType == Message.TYPE_SEND){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_right, parent, false);
        }
        return new ViewHolder(view, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        return _messages.get(position).getType();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message msg = _messages.get(position);
        if (msg.getType() == Message.TYPE_RECEIVE) {
            holder.thumb.setImageBitmap(msg.getFriend().getThumbBitmap());
            holder.nickname.setText(msg.getFriend().getNickname());
            holder.thumb.setOnClickListener(v -> {
                Intent intent = new Intent(ClientApp.getContext(), FriendDetailActivity.class);
                intent.putExtra("friend", msg.getFriend());
                ClientApp.getContext().startActivity(intent);
            });
        } else {
            holder.thumb.setImageBitmap(ClientApp.get_loginUserinfo().get_thumb());
        }
        holder.msg.setText(msg.getMsg());

    }

    @Override
    public int getItemCount() {
        return _messages.size();
    }

    public void addMsg(Message msg) {
        this._messages.add(msg);
    }

    public boolean containsFriend(String username) {
        return _friendCache.containsKey(username);
    }

    /**
     * 如果username的用户不在list，则加入。 注：非UI线程操作
     * @param username 用户名
     */
    public void testFriend(String username) {
        if (!_friendCache.containsKey(username)) {
            Friend friend = MySqlHelper.searchFriend(username);
            _friendCache.put(username, friend);
        }
    }

    public Friend getFriend(String username) {
        return _friendCache.get(username);
    }

    public Friend removeFriend(String username) {
        return _friendCache.remove(username);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView msg;
        TextView nickname;
        ViewHolder(View itemView, int type) {
            super(itemView);
            thumb = itemView.findViewById(R.id.iv_thumb);
            msg = itemView.findViewById(R.id.tv_msg);
            if (type == Message.TYPE_RECEIVE) {
                nickname = itemView.findViewById(R.id.tv_nickname);
                nickname.setVisibility(View.VISIBLE);
            }
        }
    }
}

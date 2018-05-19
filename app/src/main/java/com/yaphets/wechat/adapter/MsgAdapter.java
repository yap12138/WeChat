package com.yaphets.wechat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.R;
import com.yaphets.wechat.database.entity.Friend;
import com.yaphets.wechat.database.entity.Message;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private Friend _friend;

    public MsgAdapter(Friend friend) {
        this._friend = friend;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == Message.TYPE_RECEIVE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_left, parent, false);
        } else if (viewType == Message.TYPE_SEND){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_right, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message msg = _friend.getMessages().get(position);
        if (msg.getType() == Message.TYPE_RECEIVE) {
            holder.thumb.setImageBitmap(_friend.getThumbBitmap());
        } else {
            holder.thumb.setImageBitmap(ClientApp.get_loginUserinfo().get_thumb());
        }
        holder.msg.setText(msg.getMsg());
    }

    @Override
    public int getItemViewType(int position) {
        return _friend.getMessages().get(position).getType();
    }

    @Override
    public int getItemCount() {
        return _friend.getMessages() == null?0:_friend.getMessages().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView msg;
        ViewHolder(View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.iv_thumb);
            msg = itemView.findViewById(R.id.tv_msg);
        }
    }
}

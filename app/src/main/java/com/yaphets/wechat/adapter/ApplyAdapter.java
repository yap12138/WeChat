package com.yaphets.wechat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaphets.wechat.R;
import com.yaphets.wechat.asynctask.AcceptFriendTask;
import com.yaphets.wechat.database.entity.Apply;
import com.yaphets.wechat.database.entity.Friend;
import com.yaphets.wechat.ui.fragment.ContactFragment;
import com.yaphets.wechat.ui.fragment.FragmentFactory;
import com.yaphets.wechat.util.CallbackListener;

import java.util.List;

public class ApplyAdapter extends RecyclerView.Adapter<ApplyAdapter.ViewHolder> {

    private List<Apply> _applyList;

    public ApplyAdapter(List<Apply> apply) {
        this._applyList = apply;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_friend, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        if (viewType == 1) {
            viewHolder.accept.setVisibility(View.GONE);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Apply apply = _applyList.get(position);
        holder.thumb.setImageBitmap(apply.getThumbBitmap());
        holder.nickname.setText(apply.getNickname());
        holder.msg.setText(apply.getMsg());
        if (apply.getStatus() == 0) {
            //接受好友添加
            holder.accept.setOnClickListener(new AcceptListener(apply, holder));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return _applyList.get(position).getStatus();
    }

    @Override
    public int getItemCount() {
        return _applyList==null?0:_applyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView nickname;
        TextView msg;
        TextView accepted;
        Button accept;

        ViewHolder(View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.iv_thumb);
            nickname = itemView.findViewById(R.id.tv_nickname);
            msg = itemView.findViewById(R.id.tv_validate);
            accepted = itemView.findViewById(R.id.tv_accepted);
            accept = itemView.findViewById(R.id.btn_accept);
        }
    }

    static class AcceptListener implements View.OnClickListener {
        Apply _apply;
        ViewHolder holder;

        AcceptListener(Apply apply, ViewHolder holder) {
            this._apply = apply;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            new AcceptFriendTask(new CallbackListener<Friend>() {
                @Override
                public void run(Friend arg) {
                    ContactFragment cf = FragmentFactory.getContactFragmentInstance();
                    cf.AddFriend(arg);

                    //取消按钮
                    holder.accept.setVisibility(View.GONE);
                    //显示已添加
                    holder.accepted.setVisibility(View.VISIBLE);
                    //修改本地Apply
                    _apply.setStatus(1);
                    _apply.saveOrUpdate("username = ?", _apply.getUsername());
                }
            }).execute(_apply.getFromId(), _apply.getUsername());
        }
    }
}

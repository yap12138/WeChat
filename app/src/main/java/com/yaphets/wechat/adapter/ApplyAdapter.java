package com.yaphets.wechat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaphets.wechat.R;
import com.yaphets.wechat.database.entity.Apply;

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
        if (viewType == 0) {
            viewHolder.accepted.setVisibility(View.INVISIBLE);
        } else if (viewType == 1) {
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
        if (apply.getStatus() == 1) {
            //TODO 接受好友添加
            holder.accept.setOnClickListener(new AcceptListener(apply));
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

        AcceptListener(Apply apply) {
            this._apply = apply;
        }

        @Override
        public void onClick(View v) {

        }
    }
}

package com.yaphets.wechat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaphets.wechat.R;
import com.yaphets.wechat.database.entity.Dialogue;
import com.yaphets.wechat.util.comparator.DialogueComparator;
import com.yaphets.wechat.util.listener.OpenDialogueListener;

import java.util.List;
import java.util.Map;

public class DialogueAdapter extends RecyclerView.Adapter<DialogueAdapter.ViewHolder> {

    private List<Dialogue> _dialogueList;

    private static DialogueAdapter mDialogueAdapter;

    public static DialogueAdapter createInstance(List<Dialogue> dialogues) {
        if (mDialogueAdapter == null) {
            mDialogueAdapter = new DialogueAdapter(dialogues);
        }
        return mDialogueAdapter;
    }

    public static DialogueAdapter getInstance() {
        if (mDialogueAdapter == null) {
            throw new RuntimeException("mContactAdapter never create");
        }
        return mDialogueAdapter;
    }

    private DialogueAdapter(List<Dialogue> dialogueList) {
        this._dialogueList = dialogueList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialogue, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Dialogue dialogue = _dialogueList.get(position);
        holder.thumb.setImageBitmap(dialogue.getFriend().getThumbBitmap());
        holder.nickname.setText(dialogue.getFriend().getNickname());
        holder.msgPreView.setText(dialogue.getLastMsg());
        holder.receiveTime.setText(dialogue.getLastTime());
        holder.itemView.setOnClickListener(new OpenDialogueListener(dialogue.getFriend()));
    }

    @Override
    public int getItemCount() {
        return _dialogueList == null?0:_dialogueList.size();
    }

    public void updateDataSetWithNotify(Map<String, Dialogue> dialogues) {
        _dialogueList.clear();
        _dialogueList.addAll(dialogues.values());
        _dialogueList.sort(new DialogueComparator());
        notifyDataSetChanged();
    }

    public void updateDataSet(Map<String, Dialogue> dialogues) {
        _dialogueList.clear();
        _dialogueList.addAll(dialogues.values());
        _dialogueList.sort(new DialogueComparator());
    }

    public void sortDataSetWithNotify() {
        _dialogueList.sort(new DialogueComparator());
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView thumb;
        TextView nickname;
        TextView msgPreView;
        TextView receiveTime;

        ViewHolder(View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.di_img_thumb);
            nickname = itemView.findViewById(R.id.di_tv_name);
            msgPreView = itemView.findViewById(R.id.di_tv_msg);
            receiveTime = itemView.findViewById(R.id.di_tv_time);
        }
    }

}

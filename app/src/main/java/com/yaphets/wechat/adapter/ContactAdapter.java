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
import com.yaphets.wechat.database.entity.Friend;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<Friend> _friendList;

    public ContactAdapter(List<Friend> friendList) {
        /*Map<String, Friend> fmap = friendList.stream().collect(Collectors.toMap(Friend::getNickname, a->a));    //Java 8的新特性
        TreeMap<String, Friend> treeMap = new TreeMap<>(new CollatorComparator());                            //让名字排序
        treeMap.putAll(fmap);*/
        friendList.sort(new CollatorComparator());
        this._friendList = friendList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Friend friend = _friendList.get(position);
        holder.thumb.setImageBitmap(friend.getThumbBitmap());
        holder.nickname.setText(friend.getNickname());
        holder.itemView.setOnClickListener(new HolderClickListener(friend));
    }

    @Override
    public int getItemCount() {
        return _friendList == null?0:_friendList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView nickname;

        ViewHolder(View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.ci_iv_thumb);
            nickname = itemView.findViewById(R.id.ci_tv_name);
        }
    }

    static class HolderClickListener implements View.OnClickListener {
        private Friend friend;
        HolderClickListener(Friend friend) {
            this.friend = friend;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ClientApp.getContext(), FriendDetailActivity.class);
            intent.putExtra("friend", friend);
            ClientApp.getContext().startActivity(intent);
        }
    }

    static class CollatorComparator implements Comparator<Friend> {
        Collator collator = Collator.getInstance();

        public int compare(Friend element1, Friend element2) {
            //TODO 为什么在这里英文在后面
            CollationKey key1 = collator.getCollationKey(element1.getNickname());
            CollationKey key2 = collator.getCollationKey(element2.getNickname());
            return key1.compareTo(key2);
        }
    }
}

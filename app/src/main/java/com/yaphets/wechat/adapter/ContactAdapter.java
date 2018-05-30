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

import java.util.Map;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private Friend[] _friendList;

    private static ContactAdapter mContactAdapter;

    public static ContactAdapter createInstance(Map<String, Friend> friends) {
        mContactAdapter = new ContactAdapter(friends);
        return mContactAdapter;
    }

    public static ContactAdapter getInstance() {
        if (mContactAdapter == null) {
            throw new RuntimeException("mContactAdapter never create");
        }
        return mContactAdapter;
    }

    private ContactAdapter(Map<String, Friend> friends) {
        /*Map<String, Friend> fmap = friendList.stream().collect(Collectors.toMap(Friend::getNickname, a->a));    //Java 8的新特性
        TreeMap<String, Friend> treeMap = new TreeMap<>(new CollatorComparator());                            //让名字排序
        treeMap.putAll(fmap);*/
        //friendList.sort(new FriendComparator());
        _friendList = friends.values().toArray(new Friend[0]);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Friend friend = _friendList[position];
        holder.thumb.setImageBitmap(friend.getThumbBitmap());
        holder.nickname.setText(friend.getNickname());
        holder.itemView.setOnClickListener(new HolderClickListener(friend));
    }

    @Override
    public int getItemCount() {
        return _friendList == null?0:_friendList.length;
    }

    public void updateDataSet(Map<String, Friend> friends) {
        _friendList = friends.values().toArray(_friendList);
        notifyDataSetChanged();
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
}

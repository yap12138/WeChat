package com.yaphets.wechat.util.comparator;

import com.yaphets.wechat.database.entity.Friend;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;

public class FriendComparator implements Comparator<Friend> {
    Collator collator = Collator.getInstance();
    @Override
    public int compare(Friend element1, Friend element2) {
        //TODO 为什么在这里英文在后面
        CollationKey key1 = collator.getCollationKey(element1.getNickname());
        CollationKey key2 = collator.getCollationKey(element2.getNickname());
        return key1.compareTo(key2);
    }
}

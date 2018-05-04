package com.yaphets.wechat.database.factory;

import com.yaphets.wechat.database.entity.Friend;

public class FriendFactory {
    public static Friend createFriend(String username, String nickname, String description, byte[] thumb) {
        Friend friend = new Friend();
        friend.setUsername(username);
        friend.setNickname(nickname);
        friend.setDescription(description);
        friend.setThumb(thumb);
        return friend;
    }
}

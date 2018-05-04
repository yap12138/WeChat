package com.yaphets.wechat.database.dao;

import android.util.Log;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.database.entity.Apply;
import com.yaphets.wechat.database.entity.Friend;
import com.yaphets.wechat.util.HttpUtils;

import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public class MySqlHelper {
    private static final String TAG = "MySqlHelper";
    /**
     * 设置在线状态
     */
    public static void updateStatus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection con = null;
                PreparedStatement ps = null;

                try {
                    con = MySqlDAO.getConnection();
                    String sql = "UPDATE login_status SET ip=?,status=? WHERE user_id=?";
                    ps = con.prepareStatement(sql);
                    ps.setString(1, HttpUtils.getDevieceIP());
                    ps.setBoolean(2, true);
                    ps.setInt(3, ClientApp.get_loginUserinfo().get_uid());

                    ps.executeUpdate();

                } catch (Exception e) {
                    Log.e(TAG, "updateStatus: " + e.getMessage(), e);
                } finally {
                    MySqlDAO.release(con, ps);
                }
            }
        }).start();
    }

    public static Friend searchFriend(String usernameArg) {
        Connection con = null;
        PreparedStatement ps = null;
        Friend friend = null;

        try {
            con = MySqlDAO.getConnection();
            String sql = "SELECT username, nickname, thumb, description, friendshipPolicy, update_time FROM user WHERE username=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, usernameArg);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String username = rs.getString(1);
                String nickname = rs.getString(2);
                String description = rs.getString(4);
                int friendshipPolicy = rs.getInt(5);
                long update_time = rs.getTimestamp(6).getTime();
                Blob thumb = rs.getBlob(3);
                if (thumb == null || thumb.length() == 0) {
                    friend = new Friend(username, nickname, description, null);
                } else {
                    friend = new Friend(username, nickname, description, thumb.getBytes(1, (int) thumb.length()));
                }
                friend.setFriendshipPolicy(friendshipPolicy);
                friend.setUpdateTime(update_time);
            }
        } catch (Exception e) {
            Log.e(TAG, "searchFriend: " + e.getMessage(), e);
        } finally {
            MySqlDAO.release(con, ps);
        }
        return friend;
    }

    public static List<Friend> getFriends(List<Friend> localList, Map<String, Friend> friendMap) {
        Connection con = null;
        CallableStatement cs = null;

        try {
            con = MySqlDAO.getConnection();
            String sql = "{CALL query_friends(?)}";
            cs = con.prepareCall(sql);
            cs.setInt(1, ClientApp.get_loginUserinfo().get_uid());
            ResultSet resultSet = cs.executeQuery();
            String username, nickname, description;
            long updateTime;
            Blob thumb;
            Friend friend;
            while (resultSet.next()) {
                username = resultSet.getString("username");
                updateTime = resultSet.getTimestamp("update_time").getTime();
                if (!friendMap.containsKey(username)) {
                    nickname = resultSet.getString("nickname");
                    description = resultSet.getString("description");
                    thumb = resultSet.getBlob("thumb");

                    if (thumb == null || thumb.length() == 0) {
                        friend = new Friend(username, nickname, description, null);
                    } else {
                        friend = new Friend(username, nickname, description, thumb.getBytes(1, (int) thumb.length()));
                    }
                    friend.setUpdateTime(updateTime);
                    friend.save();
                    localList.add(friend);
                } else {
                    Friend oldFriend = friendMap.get(username);
                    if (oldFriend.getUpdateTime() >= updateTime) {
                        continue;
                    }
                    nickname = resultSet.getString("nickname");
                    description = resultSet.getString("description");
                    thumb = resultSet.getBlob("thumb");

                    oldFriend.setNickname(nickname);
                    oldFriend.setDescription(description);
                    oldFriend.setUpdateTime(updateTime);
                    if (thumb == null || thumb.length() == 0) {
                        oldFriend.setThumb(null);
                    } else if (thumb.length() != oldFriend.getThumb().length) { //长度相等默认一样，避免拉取重复的照片
                            oldFriend.setThumb(thumb.getBytes(1, (int) thumb.length()));
                    }
                    oldFriend.saveOrUpdate("username = ?", oldFriend.getUsername());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getFriends: " + e.getMessage(), e);
        } finally {
            MySqlDAO.release(con, cs);
        }
        return localList;
    }

    /**
     * 查询未处理的好友申请
     * @return
     * 返回申请者列表
     */
    public static void getApply(List<Apply> applies) {
        Connection con = null;
        CallableStatement cs = null;

        try {
            con = MySqlDAO.getConnection();
            String sql = "{CALL query_apply(?)}";
            cs = con.prepareCall(sql);
            cs.setInt(1, ClientApp.get_loginUserinfo().get_uid());
            ResultSet resultSet = cs.executeQuery();
            String username, nickname, msg;
            int fromId;
            byte status;
            Blob thumb;
            Apply apply;
            while (resultSet.next()) {
                fromId = resultSet.getInt("fromId");
                username = resultSet.getString("username");
                nickname = resultSet.getString("nickname");
                thumb = resultSet.getBlob("thumb");
                status = resultSet.getByte("status");
                msg = resultSet.getString("msg");

                if (thumb == null || thumb.length() == 0) {
                    apply = new Apply(fromId, username, nickname,null, status, msg);
                } else {
                    apply = new Apply(fromId, username, nickname,thumb.getBytes(1, (int) thumb.length()), status, msg);
                }
                apply.save();
                applies.add(0, apply);
            }
        } catch (Exception e) {
            Log.e(TAG, "getApply: " + e.getMessage(), e);
        } finally {
            MySqlDAO.release(con, cs);
        }
    }

    public static Apply getApply(String f_uname) {
        Connection con = null;
        CallableStatement cs = null;
        Apply apply = null;

        try {
            con = MySqlDAO.getConnection();
            String sql = "{CALL query_apply_one(?, ?)}";
            cs = con.prepareCall(sql);
            cs.setInt(1, ClientApp.get_loginUserinfo().get_uid());
            cs.setString(2, f_uname);
            ResultSet resultSet = cs.executeQuery();
            String username, nickname, msg;
            int fromId;
            byte status;
            Blob thumb;

            while (resultSet.next()) {
                fromId = resultSet.getInt("fromId");
                username = resultSet.getString("username");
                nickname = resultSet.getString("nickname");
                thumb = resultSet.getBlob("thumb");
                status = resultSet.getByte("status");
                msg = resultSet.getString("msg");

                if (thumb == null || thumb.length() == 0) {
                    apply = new Apply(fromId, username, nickname,null, status, msg);
                } else {
                    apply = new Apply(fromId, username, nickname,thumb.getBytes(1, (int) thumb.length()), status, msg);
                }
                apply.save();
            }
        } catch (Exception e) {
            Log.e(TAG, "getApply: " + e.getMessage(), e);
        } finally {
            MySqlDAO.release(con, cs);
        }
        return apply;
    }
}

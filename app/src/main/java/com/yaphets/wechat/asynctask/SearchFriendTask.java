package com.yaphets.wechat.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.yaphets.wechat.database.dao.MySqlHelper;
import com.yaphets.wechat.database.entity.Friend;
import com.yaphets.wechat.util.HttpCallbackListener;

import org.json.JSONObject;

public class SearchFriendTask extends AsyncTask<String, Integer, Friend> {
    private static final String TAG = "SearchFriendTask";
    private HttpCallbackListener<Friend> _callback;

    public SearchFriendTask(HttpCallbackListener<Friend> callback) {
        this._callback = callback;
    }

    @Override
    protected Friend doInBackground(String... strings) {
        String unameArg = strings[0];
        JSONObject json = MySqlHelper.searchFriend(unameArg);
        Friend friend = null;
        try {
            if (json != null) {
                byte[] thumb = null;
                String desc = null;
                if (json.has("thumb")) {
                    thumb = (byte[]) json.get("thumb");
                }
                if (json.has("description")) {
                    desc = json.getString("description");
                }
                friend = new Friend(json.getString("username"), json.getString("nickname"), desc, thumb);
                friend.setFriendshipPolicy(json.getInt("friendshipPolicy"));
            }
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: " + e.getMessage(), e);
        }

        return friend;
    }

    @Override
    protected void onPostExecute(Friend friend) {
        if (friend != null) {
            _callback.onFinish(friend);
        } else {
            _callback.onError(null);
        }
    }
}

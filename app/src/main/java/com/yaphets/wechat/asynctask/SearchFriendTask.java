package com.yaphets.wechat.asynctask;

import android.os.AsyncTask;

import com.yaphets.wechat.database.dao.MySqlHelper;
import com.yaphets.wechat.database.entity.Friend;
import com.yaphets.wechat.util.listener.HttpCallbackListener;

public class SearchFriendTask extends AsyncTask<String, Integer, Friend> {
    private static final String TAG = "SearchFriendTask";
    private HttpCallbackListener<Friend> _callback;

    public SearchFriendTask(HttpCallbackListener<Friend> callback) {
        this._callback = callback;
    }

    @Override
    protected Friend doInBackground(String... strings) {
        String unameArg = strings[0];

        Friend friend = MySqlHelper.searchFriend(unameArg);

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

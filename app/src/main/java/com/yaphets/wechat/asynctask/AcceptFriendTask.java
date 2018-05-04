package com.yaphets.wechat.asynctask;

import android.os.AsyncTask;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.database.dao.MySqlHelper;
import com.yaphets.wechat.database.entity.Friend;
import com.yaphets.wechat.util.CallbackListener;
import com.yaphets.wechat.util.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class AcceptFriendTask extends AsyncTask<Object, Integer, Friend> {

    private CallbackListener<Friend> _callback;

    public AcceptFriendTask(CallbackListener<Friend> callback) {
        this._callback = callback;
    }

    @Override
    protected Friend doInBackground(Object... params) {
        JSONObject data = new JSONObject();
        try {
            data.put("fromId",(int)params[0]);
            data.put("f_uname", params[1]);
            data.put("toId", ClientApp.get_loginUserinfo().get_uid());
            data.put("u_uname", ClientApp.get_loginUserinfo().get_username());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        boolean succeed = HttpUtils.sendHttpRequest("http://" + ClientApp._serverIP + ":8080/" + ClientApp._serverRootDir +"/AcceptFriend", data.toString());

        if (succeed) {
            return MySqlHelper.searchFriend((String) params[1]);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Friend friend) {
        if (friend != null) {
            _callback.run(friend);
        }
    }
}

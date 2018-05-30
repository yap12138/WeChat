package com.yaphets.wechat.asynctask;

import android.os.AsyncTask;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.util.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class DetachGroupChatsTask extends AsyncTask<Integer, Integer, Boolean> {
    @Override
    protected Boolean doInBackground(Integer... integers) {
        JSONObject data = new JSONObject();
        try {
            data.put("user_username", ClientApp.get_loginUserinfo().get_username());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return HttpUtils.sendHttpRequest("http://" + ClientApp._serverIP + ":8080/" + ClientApp._serverRootDir +"/DetachGroupChats",data.toString());
    }
}

package com.yaphets.wechat.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.util.HttpUtils;
import com.yaphets.wechat.util.listener.HttpCallbackListener;

import org.json.JSONException;
import org.json.JSONObject;

public class AttachGroupChatsTask extends AsyncTask<Integer, Integer, Integer> {
    private static final String TAG = "AttachGroupChatsTask";

    private HttpCallbackListener<Integer> _callback;

    public AttachGroupChatsTask(HttpCallbackListener<Integer> callback) {
        this._callback = callback;
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        int size = -1;

        JSONObject data = new JSONObject();
        try {
            data.put("user_username", ClientApp.get_loginUserinfo().get_username());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String resp = HttpUtils.sendHttpRequestForString("http://" + ClientApp._serverIP + ":8080/" + ClientApp._serverRootDir +"/AttachGroupChats",data.toString());
        try {
            JSONObject resJson = new JSONObject(resp);
            size = resJson.getInt("groupSize");
        } catch (JSONException e) {
            Log.e(TAG, "doInBackground: " + e.getMessage(), e);
        }
        return size;
    }

    @Override
    protected void onPostExecute(Integer size) {
        if (size > 0) {
            _callback.onFinish(size);
        } else {
            _callback.onError(size);
        }
    }
}

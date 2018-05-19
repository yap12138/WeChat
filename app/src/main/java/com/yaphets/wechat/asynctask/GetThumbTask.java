package com.yaphets.wechat.asynctask;

import android.os.AsyncTask;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.util.HttpUtils;
import com.yaphets.wechat.util.listener.HttpCallbackListener;

import org.json.JSONException;
import org.json.JSONObject;

public class GetThumbTask extends AsyncTask<String, Integer, byte[]>  {

    private HttpCallbackListener<byte[]> _callback;

    public GetThumbTask(HttpCallbackListener<byte[]> callback) {
        this._callback = callback;
    }

    @Override
    protected byte[] doInBackground(String... params) {
        byte[] response;
        JSONObject json = new JSONObject();
        try {
            json.put("nickname", params[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response = HttpUtils.sendHttpRequestForBytes("http://" + ClientApp._serverIP + ":8080/" + ClientApp._serverRootDir +"/ThumbQuery", json.toString());

        return response;
    }

    @Override
    protected void onPostExecute(byte[] data) {
        _callback.onFinish(data);
    }
}

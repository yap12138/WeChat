package com.yaphets.wechat.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.util.HttpCallbackListener;
import com.yaphets.wechat.util.HttpUtils;
import com.yaphets.wechat.util.RequestParam;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginTask extends AsyncTask<RequestParam, Integer, String> {

    private HttpCallbackListener<String> _callback;

    public LoginTask(HttpCallbackListener<String> callback) {
        this._callback = callback;
    }

    @Override
    protected String doInBackground(RequestParam... params) {
        String respone = null;

        JSONObject data = new JSONObject();
        try {
            data.put("password",params[0].getPassword());
            data.put("username", params[0].getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        respone = HttpUtils.sendHttpRequestForString("http://" + ClientApp._serverIP + ":8080/" + ClientApp._serverRootDir +"/LoginCheck",data.toString());

        return respone;
    }

    @Override
    protected void onPostExecute(String res) {
        if (res == null) {
            _callback.onError(null);
            return;
        }

        int flag;
        try {
            JSONObject result = new JSONObject(res);
            flag = result.getBoolean("haveFind") ?1:0;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Login Exception: ", e.getMessage());
            flag = -1;
        }


        if (flag == 1) {
            _callback.onFinish(res);
        } else {
            _callback.onError(null);
        }
    }
}

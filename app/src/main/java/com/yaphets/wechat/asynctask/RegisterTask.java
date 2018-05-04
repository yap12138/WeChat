package com.yaphets.wechat.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.util.HttpCallbackListener;
import com.yaphets.wechat.util.HttpUtils;
import com.yaphets.wechat.util.RequestParam;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterTask extends AsyncTask<RequestParam, Integer, Integer> {
    private static final String TAG = "RegisterTask";

    private HttpCallbackListener<Integer> _callback;

    public RegisterTask(HttpCallbackListener<Integer> callback) {
        this._callback = callback;
    }

    @Override
    protected Integer doInBackground(RequestParam... params) {
        Integer flag = 0;

        JSONObject data = new JSONObject();
        try {
            data.put("password",params[0].getPassword());
            data.put("username", params[0].getUsername());
            data.put("nickname", params[0].getNickname());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String respone = HttpUtils.sendHttpRequestForString("http://" + ClientApp._serverIP + ":8080/" + ClientApp._serverRootDir +"/RegisterCheck", data.toString());
        try {
            JSONObject result = new JSONObject(respone);
            flag = result.getInt("resultCode");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            flag = -3;
        }
        return flag;
    }

    /**
     *
     * @param flag -1表示已有相同账户，-2表示有相同昵称，1表示注册成功
     */
    @Override
    protected void onPostExecute(Integer flag) {
        if (flag == 1) {
            _callback.onFinish(flag);
        } else {
            _callback.onError(flag);
        }
    }
}

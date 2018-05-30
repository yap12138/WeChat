package com.yaphets.wechat.asynctask;

import android.os.AsyncTask;
import android.widget.Toast;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.util.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class SendGroupMsgTask extends AsyncTask<String, Integer, Boolean> {
    @Override
    protected Boolean doInBackground(String... params) {

        JSONObject json = new JSONObject();
        try {
            json.put("from_username", ClientApp.get_loginUserinfo().get_username());
            json.put("msg", params[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return HttpUtils.sendHttpRequest("http://" + ClientApp._serverIP + ":8080/" + ClientApp._serverRootDir +"/SendGroupMessage", json.toString());
    }

    @Override
    protected void onPostExecute(Boolean succeed) {
        if (!succeed) {
            Toast.makeText(ClientApp.getContext(), "发送失败", Toast.LENGTH_SHORT).show();
        }
    }
}

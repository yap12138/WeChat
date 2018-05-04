package com.yaphets.wechat.asynctask;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.util.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class AddFriendTask extends AsyncTask<String, Integer, Integer> {
    private static final String TAG = "AddFriendTask";

    @Override
    protected Integer doInBackground(String... params) {
        int code = 0;
        JSONObject data = new JSONObject();
        try {
            data.put("fri_username",params[0]);
            data.put("apply_msg", params[1]);
            data.put("user_id", ClientApp.get_loginUserinfo().get_uid());
            data.put("user_username", ClientApp.get_loginUserinfo().get_username());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String resp = HttpUtils.sendHttpRequestForString("http://" + ClientApp._serverIP + ":8080/" + ClientApp._serverRootDir +"/AddFriend",data.toString());
        try {
            JSONObject resJson = new JSONObject(resp);
            code = resJson.getInt("resCode");
        } catch (JSONException e) {
            Log.e(TAG, "doInBackground: " + e.getMessage(), e);
        }
        return code;
    }

    @Override
    protected void onPostExecute(Integer succeed) {
        if (succeed == 1) {
            Toast.makeText(ClientApp.getContext(), "已发送好友申请", Toast.LENGTH_SHORT).show();
        } else if (succeed == -1) {
            Toast.makeText(ClientApp.getContext(), "对方已经是您的好友", Toast.LENGTH_SHORT).show();
        } else if (succeed == -2) {
            Toast.makeText(ClientApp.getContext(), "您已发送好友申请", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ClientApp.getContext(), "请求失败", Toast.LENGTH_SHORT).show();
        }
    }
}

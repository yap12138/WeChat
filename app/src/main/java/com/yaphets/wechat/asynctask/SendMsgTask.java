package com.yaphets.wechat.asynctask;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.database.entity.Message;
import com.yaphets.wechat.util.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class SendMsgTask extends AsyncTask<String, Integer, String> {
    private static final String TAG = "SendMsgTask";

    private Message _msg;

    public SendMsgTask(Message msg) {
        this._msg = msg;
    }

    @Override
    protected String doInBackground(String... params) {
        String retMsg = null;

        JSONObject json = new JSONObject();
        try {
            json.put("from_nickname", ClientApp.get_loginUserinfo().get_nickname());
            json.put("to_nickname", _msg.getFriend().getNickname());
            json.put("msg", _msg.getMsg());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        retMsg = HttpUtils.sendHttpRequestForString("http://" + ClientApp._serverIP + ":8080/" + ClientApp._serverRootDir +"/SendMessage", json.toString());

        return retMsg;
    }

    @Override
    protected void onPostExecute(String str) {
        if (str == null) {
            return;
        }

        int send;
        int receive = 0;
        try {
            JSONObject result = new JSONObject(str);
            send = result.getBoolean("send succeed") ?1:0;
            receive = result.getBoolean("receive succeed") ?1:0;
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            send = -1;
        }

        if (send == -1) {
            Toast.makeText(ClientApp.getContext(), "发送失败", Toast.LENGTH_SHORT).show();
            //tip.setText("失败");
            return;
        }
        //发送成功再save
        _msg.save();
        /*if (receive == 1) {
            tip.setText("已达");
        } else {
            tip.setText("已发");
        }*/
    }
}

package com.yaphets.wechat.asynctask;

import android.os.AsyncTask;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.database.dao.MySqlHelper;
import com.yaphets.wechat.database.entity.Friend;
import com.yaphets.wechat.util.CallbackListener;

import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PullFriendsTask extends AsyncTask<Integer, Integer, Integer> {
    private static final String TAG = "PullFriendsTask";

    private CallbackListener<Integer> _callback;

    public PullFriendsTask(CallbackListener<Integer> callback) {
        this._callback = callback;
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        //局部拉取，先从本地数据库加载，有更新的再从服务器数据库拉取
        List<Friend> list = DataSupport.findAll(Friend.class);
        Map<String, Friend> fmap = list.stream().collect(Collectors.toMap(Friend::getUsername, a->a));  //Java 8的新特性

        ClientApp._friendsList = MySqlHelper.getFriends(list, fmap);
        return 1;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        _callback.run(integer);
    }
}

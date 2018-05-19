package com.yaphets.wechat.asynctask;

import android.os.AsyncTask;

import com.yaphets.wechat.database.dao.MySqlHelper;
import com.yaphets.wechat.database.entity.Apply;
import com.yaphets.wechat.util.listener.CallbackListener;

import org.litepal.crud.DataSupport;

import java.util.List;

public class PullFriendRequestTask extends AsyncTask<Integer, Integer, Integer> {

    private List<Apply> _applies;
    private CallbackListener<Integer> _callback;

    public PullFriendRequestTask(List<Apply> requesApplies, CallbackListener<Integer> callback) {
        this._applies = requesApplies;
        this._callback = callback;
    }

    /**
     * 先本地取出申请，再从服务器数据库取
     * @param integers
     * 未使用
     * @return
     * 返回新收到的申请数目
     */
    @Override
    protected Integer doInBackground(Integer... integers) {
        //_applies.addAll(DataSupport.findAll(Apply.class));
        _applies.addAll(DataSupport.order("id desc").find(Apply.class));
        int local = _applies.size();
        MySqlHelper.getApply(_applies);
        return _applies.size() - local;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        if (integer > 0)
            _callback.run(integer);
    }
}

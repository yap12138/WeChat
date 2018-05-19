package com.yaphets.wechat.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.R;
import com.yaphets.wechat.activity.CommunicateActivity;
import com.yaphets.wechat.adapter.DialogueAdapter;
import com.yaphets.wechat.database.dao.MySqlHelper;
import com.yaphets.wechat.database.entity.Apply;
import com.yaphets.wechat.database.entity.Friend;
import com.yaphets.wechat.database.entity.Message;
import com.yaphets.wechat.ui.fragment.ContactFragment;
import com.yaphets.wechat.ui.fragment.FragmentFactory;
import com.yaphets.wechat.util.MyActivityManager;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ReceiveThread implements Runnable {
    private static final String TAG = "ReceiveThread";
    private static final int REQUEST_FRIEND = 2;
    private static final int SEND_MSG = 3;
    private static final int REPLY_FRIEND = 4;
    private static final int FOCUS_LOGOUT = 5;

    private Socket _socket;

    ReceiveThread(Socket socket) {
        this._socket = socket;
    }

    @Override
    public void run() {
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        int responseCode;
        //String uname = ClientApp.get_loginUserinfo().get_nickname();
        try {
            bis = new BufferedInputStream(_socket.getInputStream());
            dis = new DataInputStream(bis);

            while (!_socket.isClosed() && HeartBeatService.isAlive()) {

                responseCode = dis.readInt();
                switch (responseCode) {
                    case REQUEST_FRIEND: //请求添加好友的提示
                    {
                        String funame = dis.readUTF();
                        Apply receive = MySqlHelper.getApply(funame);
                        ClientApp._handler.post(new FriendRequest(receive));
                        break;
                    }
                    case SEND_MSG:  //接收消息
                    {
                        String msg = dis.readUTF();
                        String fnname = dis.readUTF();
                        Friend friend = ClientApp._friendsMap.get(fnname);
                        Message message = new Message(friend, msg, System.currentTimeMillis(), Message.TYPE_RECEIVE);
                        friend.getMessages().add(message);
                        message.save();

                        ClientApp._handler.post(new ReceiveMsg(message));
                        break;
                    }
                    case REPLY_FRIEND: //好友答复 添加好友请求
                    {
                        String funame = dis.readUTF();
                        Friend friend = MySqlHelper.searchFriend(funame);
                        ClientApp._handler.post(new FriendReply(friend));
                        break;
                    }
                    case FOCUS_LOGOUT: //TODO 强制下线
                        break;
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "run: " + e.getMessage(), e);
        }
        Log.d(TAG, "run(): exit");
    }

    static class FriendRequest implements Runnable {
        private Apply apply;

        FriendRequest(Apply apply) {
            this.apply = apply;
        }

        @Override
        public void run() {
            //Toast.makeText(ClientApp.getContext(), "收到来自 " + this.uname + " 的好友申请", Toast.LENGTH_SHORT).show();
            ContactFragment cf = FragmentFactory.getContactFragmentInstance();
            cf.getApplies().add(0, apply);
            cf.notifyApply(1);
        }
    }

    static class FriendReply implements Runnable {
        private Friend friend;

        FriendReply(Friend friend) {
            this.friend = friend;
        }

        @Override
        public void run() {
            ContactFragment cf = FragmentFactory.getContactFragmentInstance();
            cf.AddFriend(friend);
        }
    }

    static class ReceiveMsg implements Runnable {
        private Message _msg;

        ReceiveMsg(Message msg) {
            this._msg = msg;
        }

        @Override
        public void run() {
            DialogueAdapter.getInstance().sortDataSetWithNotify();
            Context ctx = ClientApp.getContext();
            Activity activity = MyActivityManager.getInstance().getCurrentActivity();
            if (activity instanceof CommunicateActivity) {
                CommunicateActivity communicateActivity = (CommunicateActivity) activity;
                String fname = communicateActivity.getFnname();
                if (fname.equals(_msg.getFriend().getNickname())) {
                    communicateActivity.notifyListChange();
                    return;
                }
            }
            makeNotification(ctx);
        }

        private void makeNotification(Context ctx) {

            Intent intent = new Intent(ctx, CommunicateActivity.class);
            intent.putExtra("fri_key", _msg.getFriend().getNickname());
            PendingIntent pi = PendingIntent.getActivity(ctx, 0, intent, 0);


            NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(ctx);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new Notification.Builder(ctx, null);
            }

            Notification notification = builder
                    .setContentTitle("来自" + _msg.getFriend().getNickname() + "的一条消息")
                    .setContentText(_msg.getMsg())
                    .setWhen(_msg.getTimestamp())
                    .setSmallIcon(R.drawable.tab_message)
                    .setTicker(_msg.getMsg())                   //状态栏文字
                    .setLargeIcon(_msg.getFriend().getThumbBitmap())
                    .setContentIntent(pi)
                    .setDefaults(Notification.DEFAULT_ALL)      //振动、铃声、LED
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)     //横幅显示
                    .build();
            assert manager != null;
            manager.notify(_msg.getFriend().hashCode(), notification);
        }
    }
}

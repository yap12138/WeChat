package com.yaphets.wechat.service;

import android.util.Log;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.database.dao.MySqlHelper;
import com.yaphets.wechat.database.entity.Apply;
import com.yaphets.wechat.database.entity.Friend;
import com.yaphets.wechat.ui.fragment.ContactFragment;
import com.yaphets.wechat.ui.fragment.FragmentFactory;

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

    public ReceiveThread(Socket socket) {
        this._socket = socket;
    }

    @Override
    public void run() {
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        int responseCode;
        String funame;
        String msg = null;
        //String uname = ClientApp.get_loginUserinfo().get_nickname();
        try {
            bis = new BufferedInputStream(_socket.getInputStream());
            dis = new DataInputStream(bis);

            while (!_socket.isClosed() && HeartBeatService.isAlive()) {

                responseCode = dis.readInt();
                switch (responseCode) {
                    case REQUEST_FRIEND: //请求添加好友的提示
                        funame = dis.readUTF();
                        Apply receive = MySqlHelper.getApply(funame);
                        ClientApp._handler.post(new FriendRequest(receive));
                        break;
                    case SEND_MSG:  //TODO 接收消息
                        msg = dis.readUTF();
                        //ClientApp._msgHandler.post(new UpdataUI(new MessageEntity(cname, uname, msg, System.currentTimeMillis())));
                        break;
                    case REPLY_FRIEND: //好友答复 添加好友请求
                        funame = dis.readUTF();
                        Friend friend = MySqlHelper.searchFriend(funame);
                        ClientApp._handler.post(new FriendReply(friend));
                        break;
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
            ContactFragment cf = (ContactFragment)FragmentFactory.createFragment(1);
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

    /*class UpdataUI implements Runnable {
        private MessageEntity _msg;

        public UpdataUI(MessageEntity msg) {
            this._msg = msg;
        }

        @Override
        public void run() {
            ClientApp._msgAdapter.add(_msg);
            _msg.save();

            Context ctx = ClientApp.getContext();

            Intent intent = new Intent(ctx, MainActivity.class);
            intent.putExtra("receiveMsg", true);
            PendingIntent pi = PendingIntent.getActivity(ctx, 0, intent, 0);

            NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification.Builder(ctx)
                    .setContentTitle("来自" + _msg.getFname() + "的一条消息")
                    .setContentText(_msg.getMsg())
                    .setWhen(_msg.getTimestamp())
                    .setSmallIcon(R.drawable.tab_message)
                    .setTicker(_msg.getMsg())                   //状态栏文字
                    .setLargeIcon(ClientApp.get_coupleUserinfo().get_thumb())
                    .setContentIntent(pi)
                    .setDefaults(Notification.DEFAULT_ALL)      //振动、铃声、LED
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)     //横幅显示
                    .build();
            manager.notify(1, notification);
        }
    }*/
}

package com.yaphets.wechat.service;

import android.util.Log;
import android.widget.Toast;

import com.yaphets.wechat.ClientApp;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ReceiveThread implements Runnable {
    private static final String TAG = "ReceiveThread";
    private static final int REQUEST_FRIEND = 2;
    private static final int SEND_MSG = 3;
    private static final int FOCUS_LOGOUT = 4;

    private Socket _socket;

    public ReceiveThread(Socket socket) {
        this._socket = socket;
    }

    @Override
    public void run() {
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        int responseCode;
        String msg = null;
        String uname = null;
        //String uname = ClientApp.get_loginUserinfo().get_nickname();
        try {
            bis = new BufferedInputStream(_socket.getInputStream());
            dis = new DataInputStream(bis);

            while (!_socket.isClosed() && HeartBeatService.isAlive()) {

                responseCode = dis.readInt();
                switch (responseCode) {
                    case REQUEST_FRIEND: //TODO 请求添加好友的答复
                        uname = dis.readUTF();
                        ClientApp._handler.post(new FriendReply(uname));
                        break;
                    case SEND_MSG:
                        msg = dis.readUTF();
                        //ClientApp._msgHandler.post(new UpdataUI(new MessageEntity(cname, uname, msg, System.currentTimeMillis())));
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

    class FriendReply implements Runnable {
        private String uname;

        FriendReply (String name) {
            this.uname = name;
        }

        @Override
        public void run() {
            Toast.makeText(ClientApp.getContext(), "收到来自 " + this.uname + " 的好友申请", Toast.LENGTH_SHORT).show();
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

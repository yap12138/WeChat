package com.yaphets.wechat.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.R;
import com.yaphets.wechat.activity.CommunicateActivity;
import com.yaphets.wechat.activity.GroupChatsActivity;
import com.yaphets.wechat.adapter.DialogueAdapter;
import com.yaphets.wechat.adapter.GroupMsgAdapter;
import com.yaphets.wechat.database.dao.MySqlHelper;
import com.yaphets.wechat.database.entity.Apply;
import com.yaphets.wechat.database.entity.Dialogue;
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
    private static final int RECEIVE_MSG = 3;
    private static final int REPLY_FRIEND = 4;
    private static final int ATTACH_GC = 5;
    private static final int DETACH_GC = -5;
    private static final int RECEIVE_GROUP_MSG = 6;
    private static final int FOCUS_LOGOUT = 10;

    private static final String id = "channel_1";
    private static final String name = "channel_name_1";

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
                    case RECEIVE_MSG:  //接收消息
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
                    case ATTACH_GC:
                    {
                        String username = dis.readUTF();
                        GroupMsgAdapter.getInstance().testFriend(username);
                        Activity activity = MyActivityManager.getInstance().getCurrentActivity();
                        if (activity instanceof GroupChatsActivity) {
                            ClientApp._handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    GroupChatsActivity gcAcitvity = (GroupChatsActivity) activity;
                                    gcAcitvity.userEnter(username);
                                }
                            });
                        }
                        break;
                    }
                    case DETACH_GC:
                    {
                        String username = dis.readUTF();

                        Activity activity = MyActivityManager.getInstance().getCurrentActivity();
                        if (activity instanceof GroupChatsActivity) {
                            ClientApp._handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    GroupChatsActivity gcAcitvity = (GroupChatsActivity) activity;
                                    gcAcitvity.userExit(username);
                                }
                            });
                        }
                        break;
                    }
                    case RECEIVE_GROUP_MSG:
                    {
                        String funame = dis.readUTF();
                        String msg = dis.readUTF();
                        GroupMsgAdapter.getInstance().testFriend(funame);
                        Friend friend = GroupMsgAdapter.getInstance().getFriend(funame);
                        Message message = new Message(friend, msg, System.currentTimeMillis(), Message.TYPE_RECEIVE);
                        Activity activity = MyActivityManager.getInstance().getCurrentActivity();
                        if (activity instanceof GroupChatsActivity) {
                            ClientApp._handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    GroupChatsActivity gcAcitvity = (GroupChatsActivity) activity;
                                    gcAcitvity.addMsg(message);
                                }
                            });
                        }
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
            //设置个人小红点
            Dialogue cur = ClientApp._dialogueMap.get(_msg.getFriend().getNickname());
            if (cur == null) {
                Friend tmp = ClientApp._friendsMap.get(_msg.getFriend().getNickname());
                Dialogue dialogue = new Dialogue(tmp);
                ClientApp._dialogueMap.put(_msg.getFriend().getNickname(), dialogue);
                DialogueAdapter.getInstance().updateDataSetWithNotify(ClientApp._dialogueMap);
                cur = dialogue;
            }
            cur.setNonRead(cur.getNonRead() + 1);
            //设置主界面小红点
            int num = ClientApp.getMsgNotifyBadge().getNumber() + 1;
            ClientApp.getMsgNotifyBadge().setNumber(num);
        }

        private void makeNotification(Context ctx) {

            Intent intent = new Intent(ctx, CommunicateActivity.class);
            intent.putExtra("fri_key", _msg.getFriend().getNickname());
            PendingIntent pi = PendingIntent.getActivity(ctx, _msg.getFriend().hashCode(), intent, 0);


            NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(ctx);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
                builder = new Notification.Builder(ctx, id);
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

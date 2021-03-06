package com.yaphets.wechat.asynctask;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import com.yaphets.wechat.ClientApp;
import com.yaphets.wechat.R;
import com.yaphets.wechat.activity.CommunicateActivity;
import com.yaphets.wechat.adapter.DialogueAdapter;
import com.yaphets.wechat.database.dao.MySqlHelper;
import com.yaphets.wechat.database.entity.Dialogue;
import com.yaphets.wechat.database.entity.Message;

import java.util.List;

public class PullMsgTask extends AsyncTask<String, Integer, List<Message>> {

    private static final String id = "channel_1";
    private static final String name = "channel_name_1";

    @Override
    protected List<Message> doInBackground(String... strings) {
        //拉取未读消息，notify用户 记录次数
        List<Message> list = MySqlHelper.getUnreceiveMsg();
        boolean bool = false;
        for(Message msg : list) {
            String fnname = msg.getFriend().getNickname();
            if (!ClientApp._dialogueMap.containsKey(fnname)) {
                bool = true;
                Dialogue dialogue = new Dialogue(msg.getFriend());
                ClientApp._dialogueMap.put(fnname, dialogue);
            }
        }
        if (bool) {
            DialogueAdapter.getInstance().updateDataSet(ClientApp._dialogueMap);
        }

        return list;
    }

    @Override
    protected void onPostExecute(List<Message> messages) {
        if (messages.isEmpty()) {
            return;
        }

        DialogueAdapter.getInstance().notifyDataSetChanged();
        ClientApp.getMsgNotifyBadge().setNumber(messages.size());

        Context ctx = ClientApp.getContext();

        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(ctx);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
            builder = new Notification.Builder(ctx, id);
        }

        for (Message msg : messages) {
            //每个dialogue设置红点
            Dialogue cur = ClientApp._dialogueMap.get(msg.getFriend().getNickname());
            cur.setNonRead(cur.getNonRead() + 1);

            Intent intent = new Intent(ctx, CommunicateActivity.class);
            intent.putExtra("fri_key", msg.getFriend().getNickname());
            PendingIntent pi = PendingIntent.getActivity(ctx, msg.getFriend().hashCode(), intent, 0);

            Notification notification = builder
                    .setContentTitle("来自" + msg.getFriend().getNickname() + "的一条消息")
                    .setContentText(msg.getMsg())
                    .setWhen(msg.getTimestamp())
                    .setSmallIcon(R.drawable.tab_message)
                    .setTicker(msg.getMsg())                   //状态栏文字
                    .setLargeIcon(msg.getFriend().getThumbBitmap())
                    .setContentIntent(pi)
                    .setDefaults(Notification.DEFAULT_ALL)      //振动、铃声、LED
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)     //横幅显示
                    .build();
            assert manager != null;
            manager.notify(msg.getFriend().hashCode(), notification);
        }

    }
}

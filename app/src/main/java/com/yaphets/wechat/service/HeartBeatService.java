package com.yaphets.wechat.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.yaphets.wechat.ClientApp;

import java.io.IOException;
import java.net.Socket;

public class HeartBeatService extends Service {
    private static final String TAG = "HeartBeatService";

    private Socket socket = null;
    private static boolean _alive;

    public HeartBeatService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _alive = true;
        Log.d(TAG, "onCreate executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ClientApp._serverIP, ClientApp._serverPort);
                    //心跳线程
                    new Thread(new HeartThread(socket)).start();
                    //接收服务器的数据包
                    new Thread(new ReceiveThread(socket)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        Log.d("HeartBeatService", "onStartCommand executed");
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        _alive = false;
        Log.d(TAG, "onDestroy executed");
    }

    public static boolean isAlive() {
        return _alive;
    }
}

package com.yaphets.wechat.service;

import android.util.Log;
import android.widget.Toast;

import com.yaphets.wechat.ClientApp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HeartThread implements Runnable {
    private static final String TAG = "HeartThread";

    private static final int _HOLD_HEARTBEAT = 100;

    private final static int _HEARTBEAT = 1;
    private Socket _socket;

    HeartThread(Socket socket) {
        this._socket = socket;
    }

    @Override
    public void run() {
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(_socket.getOutputStream());
            //发送类型码
            dos.writeInt(_HOLD_HEARTBEAT);
            //发送账户
            dos.writeUTF(ClientApp._username);
            dos.flush();

            int ERROR_count = 0;
            while (!_socket.isClosed() && HeartBeatService.isAlive()) {
                try {
                    //10s发送心跳包
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "run: sleep(10000) :" + e.getMessage(), e);
                }

                try {
                    dos.writeInt(_HEARTBEAT);
                } catch (IOException e) {
                    Log.e(TAG, "writeInt(_HEARTBEAT): " + e.getMessage(), e);
                    Toast.makeText(ClientApp.getContext(), "无法连接服务器", Toast.LENGTH_SHORT).show();
                    if (++ERROR_count == 5) {
                        break;
                    }
                }
            }
            Log.d(TAG, "exit");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (_socket != null) {
                try {
                    _socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

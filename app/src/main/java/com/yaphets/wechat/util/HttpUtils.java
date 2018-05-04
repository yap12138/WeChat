package com.yaphets.wechat.util;

import android.util.Log;
import android.widget.Toast;

import com.yaphets.wechat.ClientApp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class HttpUtils {
    private static final String TAG = "HttpUtils";

    public static boolean sendHttpRequest(final String address, final String requestParam) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");

            if (requestParam != null) {
                BufferedOutputStream dos = new BufferedOutputStream(connection.getOutputStream());
                String postParam = "requestParam=" + requestParam;

                dos.write(postParam.getBytes("utf-8"));
                dos.flush();
                dos.close();
            }

            int resultCode = connection.getResponseCode();
            if (resultCode == 200) {
                return true;
            }
        } catch (IOException e) {
            Log.e(TAG, "sendHttpRequest: " + e.getMessage(), e);
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return false;
    }

    /**
     *
     * @param address
     *      IP地址
     * @param requestParam
     *      请求参数
     * @return
     *      字节数组数据
     */
    public static String sendHttpRequestForString(final String address, final String requestParam) {
        String res = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");

            if (requestParam != null) {
                BufferedOutputStream dos = new BufferedOutputStream(connection.getOutputStream());
                String postParam = "requestParam=" + requestParam;

                dos.write(postParam.getBytes("utf-8"));
                dos.flush();
                dos.close();
            }

            int resultCode = connection.getResponseCode();
            if (resultCode == 200) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                BufferedInputStream inStream = new BufferedInputStream(connection.getInputStream());
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                inStream.close();
                outStream.close();

                res = outStream.toString("utf-8");
            }
        } catch (IOException e) {
            if (e instanceof SocketTimeoutException) {
                Toast.makeText(ClientApp.getContext(), "连接服务器超时", Toast.LENGTH_SHORT).show();
            }
            Log.e(TAG, "sendHttpRequestForString: " + e.getMessage(), e);
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return res;
    }

    /**
     *
     * @param address
     *      IP地址
     * @param requestParam
     *      请求参数
     * @return
     *      字节数组数据
     */
    public static byte[] sendHttpRequestForBytes(final String address, final String requestParam) {
        byte[] res = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");

            if (requestParam != null) {
                BufferedOutputStream dos = new BufferedOutputStream(connection.getOutputStream());
                String postParam = "requestParam=" + requestParam;
                //System.out.println(postParam);
                dos.write(postParam.getBytes("utf-8"));
                dos.flush();
                dos.close();
            }

            int resultCode = connection.getResponseCode();
            if (resultCode == 200) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                BufferedInputStream inStream = new BufferedInputStream(connection.getInputStream());
                byte[] buffer = new byte[1024];
                int len;

                while ((len = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                inStream.close();
                outStream.close();

                res = outStream.toByteArray();
            }
        } catch (IOException e) {
            if (e instanceof SocketTimeoutException) {
                Toast.makeText(ClientApp.getContext(), "连接服务器超时", Toast.LENGTH_SHORT).show();
            }
            Log.e(TAG, "sendHttpRequestForBytes: " + e.getMessage(), e);
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return res;
    }

    /**
     *
     * @return
     *      返回当前android设备IP
     */
    public static String getDevieceIP() {
        try {
            ArrayList<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni: nilist)
            {
                ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());
                for (InetAddress address: ialist){
                    if (!address.isLoopbackAddress());
                    {
                        if (address instanceof Inet6Address)
                            continue;
                        String ip = address.getHostAddress();
                        if ("127.0.0.1".equals(ip)) {
                            continue;
                        }
                        return ip;
                    }
                }

            }

        } catch (SocketException ex) {
            Log.e("HttpUtils", ex.toString());
        }
        return null;
    }
}

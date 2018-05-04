package com.yaphets.wechat.database.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlDAO {
    private static String driverName = "com.mysql.jdbc.Driver";
    private static String url = "jdbc:mysql://120.78.139.85:3306/app_wechat?useSSL=false";
    private static String userName = "hogason";
    private static String password = "ddd123456";

    public static Connection getConnection() {
        try {
            Class<?> clazz = Class.forName(driverName);
            Connection con = DriverManager.getConnection(url, userName, password);
            return con;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void release(Connection con , Statement state){//关闭数据库连接
        if(state != null){
            try {
                state.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(con != null){
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

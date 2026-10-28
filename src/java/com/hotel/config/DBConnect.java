package com.hotel.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {

    public static Connection dbCon() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hotel_db";
        String user = "root";
        String password = "";   // ← put your MySQL root password here if you set one

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver not found.", e);
        }

        return DriverManager.getConnection(url, user, password);
    }
}

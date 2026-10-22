package ajp_lec;

import java.sql.*;

public class DBConnect {

    public Connection dbCon() {

        Connection ct = null;

        final String url = "jdbc:mysql://localhost:3306/uai";
        final String user = "root";
        final String password = "yashloq";

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            ct = DriverManager.getConnection(url, user, password);

            System.out.println("Connected Successfully");

        } catch (Exception e) {

            System.out.println(e);

        }

        return ct;
    }
}
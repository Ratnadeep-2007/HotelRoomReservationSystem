package studentreg;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ratna
 */
import java.sql.*;
public class DBConnect {
    public Connection dbCon(){
    Connection ct=null;
    final String url="jdbc:mysql://localhost:3306/new_schema";
    final String user="root";
    final String password="asdf@1234";
    try{
        ct=DriverManager.getConnection(url,user,password);
    }
    catch(Exception e){
        System.out.println(e.toString());
        }
   return ct;
    }
    
}

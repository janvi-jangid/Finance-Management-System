package com.binarybrains;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MySQl {

    static void main(String[] args) throws Exception {

        try
        {
            String url ="mysql:mysql://localhost:3306/studentdb";
            String username = "root";
            String pass = "sa123";

            Connection con = DriverManager.getConnection(url,username,pass);
            System.out.println("Connected ");

            PreparedStatement stmt = con.prepareStatement("select * from student");
            ResultSet rs = stmt.executeQuery();

            if(rs.next() == true) //checking if data is fetch or not
            {
                System.out.println("sid is "  + rs.getInt(1));
                System.out.println("name is " + rs.getString(2));
                System.out.println("course is " + rs.getString(3));
                System.out.println("Fee is " + rs.getFloat(4));
            }
            else
            {
                System.out.println("data nai aya ");
            }
            con.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }
}
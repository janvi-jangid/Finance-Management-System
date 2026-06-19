package com.binarybrains.project;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLConnector
    {
        public static Connection getConnection()
        {
            Connection con = null;
            try
            {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/finance_manager", "root", "sa123");
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
            return con;
        }
    }


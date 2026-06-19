package com.binarybrains.project;

import java.sql.Connection;

public class TestConnection
{
    public static void main(String[] args)
    {
        SQLConnector DBConnection = null;
        Connection con = DBConnection.getConnection();
        if(con != null)
        {
            System.out.println("Database Connected Successfully");
        }
        else
        {
            System.out.println("Connection Failed");
        }
    }
}

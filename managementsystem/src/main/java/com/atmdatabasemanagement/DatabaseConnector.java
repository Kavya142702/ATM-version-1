package com.atmdatabasemanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.atmexceptions.ATMExceptions;

public class DatabaseConnector {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USER_NAME = "SYSTEM";
    private static final String PASSWORD = "@Kavya1427";

    public static Connection getDBConnection() throws ATMExceptions {
        Connection con = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new ATMExceptions("Database driver not found.");
        } catch (SQLException e) {
            throw new ATMExceptions("Failed to connect to the database: " + e.getMessage());
        }
        return con;
    }
}

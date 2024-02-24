package com.atmaccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.atmexceptions.ATMExceptions;

public class CheckingAccount {

	private static final double OVERDRAFT_LIMIT = 1000.0;
	
	public static double performCurrentAccountWithdrawal(Connection conn, String debitCard_No, double amount) throws ATMExceptions {
	    String balanceUpdateQuery = "UPDATE KAVYAP.Account SET Balance = Balance - ? WHERE Account_No = (SELECT Account_No FROM KAVYAP.ATM_Card WHERE debitCard_No = ?) AND Balance + ? >= ?";
	    try (PreparedStatement updateStmt = conn.prepareStatement(balanceUpdateQuery)) {
	        updateStmt.setDouble(1, amount);
	        updateStmt.setString(2, debitCard_No);
	        updateStmt.setDouble(3, OVERDRAFT_LIMIT);
	        updateStmt.setDouble(4, amount);
	        int rowsAffected = updateStmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("Cash withdrawal from current account successful!");
	        } else {
	            throw new ATMExceptions("Withdrawal amount exceeds available balance plus overdraft limit.");
	        }
	    } catch (SQLException e) {
	        throw new ATMExceptions("Database error occurred: " + e.getMessage());
	    }
	    return amount;
	}

	public static double performCurrentAccountDeposit(Connection conn, String debitCard_No, double amount) throws SQLException {
	    String depositQuery = "UPDATE KAVYAP.Account SET Balance = Balance + ? WHERE Account_No = (SELECT Account_No FROM KAVYAP.ATM_Card WHERE debitCard_No = ?)";
	    try (PreparedStatement depositStmt = conn.prepareStatement(depositQuery)) {
	        depositStmt.setDouble(1, amount);
	        depositStmt.setString(2, debitCard_No);
	        int rowsAffected = depositStmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("Deposit to current account successful!");
	        } else {
	            System.out.println("Failed to deposit to current account.");
	        }
	    }
		return amount;
	}
}

package com.atmaccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.atmexceptions.ATMExceptions;

public class SavingsAccount {

    public static double performSavingsAccountWithdrawal(Connection conn, String debitCard_No, double amount) throws ATMExceptions {
        String balanceUpdateQuery = "UPDATE KAVYAP.Account SET Balance = Balance - ? WHERE Account_No = (SELECT Account_No FROM KAVYAP.ATM_Card WHERE debitCard_No = ?) AND Balance >= ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(balanceUpdateQuery)) {
            updateStmt.setDouble(1, amount);
            updateStmt.setString(2, debitCard_No);
            updateStmt.setDouble(3, amount);
            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cash withdrawal from savings account successful!");
            } else {
                throw new ATMExceptions("Insufficient balance in the savings account. Withdrawal failed.");
            }
        } catch (SQLException e) {
            throw new ATMExceptions("Database error occurred: " + e.getMessage());
        }
        return amount;
    }

    public static double performSavingsAccountDeposit(Connection conn, String debitCard_No, double amount) throws ATMExceptions {
        String depositQuery = "UPDATE KAVYAP.Account SET Balance = Balance + ? WHERE Account_No = (SELECT Account_No FROM KAVYAP.ATM_Card WHERE debitCard_No = ?)";
        try (PreparedStatement depositStmt = conn.prepareStatement(depositQuery)) {
            depositStmt.setDouble(1, amount);
            depositStmt.setString(2, debitCard_No);
            int rowsAffected = depositStmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Deposit to savings account successful!");
            } else {
                throw new ATMExceptions("Failed to deposit to savings account.");
            }
        } catch (SQLException e) {
            throw new ATMExceptions("Database error occurred: " + e.getMessage());
        }
        return amount;
    }
}

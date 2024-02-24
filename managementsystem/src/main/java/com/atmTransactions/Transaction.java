package com.atmTransactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;

import com.atmexceptions.ATMExceptions;
import com.atmdatabasemanagement.DatabaseConnector;

public class Transaction {
    private int transaction_ID;
    private String debitCard_No;
    private String transaction_Type;
    private double amount;
    private Date transaction_Date;

    public Transaction() {
        // Default constructor
    }

    public Transaction(int transaction_ID, String debitCard_No, String transaction_Type, double amount,
            Date transactionDate) {
        this.transaction_ID = transaction_ID;
        this.debitCard_No = debitCard_No;
        this.transaction_Type = transaction_Type;
        this.amount = amount;
        this.transaction_Date = transactionDate;
    }

    public int getTransaction_ID() {
        return transaction_ID;
    }

    public void setTransaction_ID(int transaction_ID) {
        this.transaction_ID = transaction_ID;
    }

    public String getDebitCard_No() {
        return debitCard_No;
    }

    public String getTransaction_Type() {
        return transaction_Type;
    }

    public double getAmount() {
        return amount;
    }

    public Date getTransaction_Date() {
        return transaction_Date;
    }

    static DatabaseConnector db = new DatabaseConnector();
    private static LinkedList<Transaction> transactionList = new LinkedList<>();

    @SuppressWarnings("static-access")
    public static void viewAllTransactions() throws ATMExceptions, ClassNotFoundException {
        try (Connection conn = db.getDBConnection()) {
            String selectQuery = "SELECT * FROM KAVYAP.Transaction";
            PreparedStatement stmt = conn.prepareStatement(selectQuery);
            ResultSet rs = stmt.executeQuery();
            LinkedList<Transaction> transactionList = new LinkedList<>();
            while (rs.next()) {
                int transactionID = rs.getInt("Transaction_ID");
                String debitCardNo = rs.getString("DebitCard_No");
                String transactionType = rs.getString("Transaction_Type");
                double amount = rs.getDouble("Amount");
                Date transactionDate = rs.getTimestamp("Transaction_Date");

                Transaction transaction = new Transaction(transactionID, debitCardNo, transactionType, amount, transactionDate);
                transactionList.add(transaction);
            }

            System.out.println("+-----------------+-------------------+-------------------+----------+-----------------------+");
            System.out.println("| Transaction ID  | Debit Card Number | Transaction Type  | Amount   | Transaction Date      |");
            System.out.println("+-----------------+-------------------+-------------------+----------+-----------------------+");
            for (Transaction transactions : transactionList) {
                System.out.printf("| %-15d | %-17s | %-17s | %-8.2f | %-21s |%n", transactions.getTransaction_ID(),
                        transactions.getDebitCard_No(), transactions.getTransaction_Type(), transactions.getAmount(),
                        transactions.getTransaction_Date());
            }
            System.out.println("+-----------------+-------------------+-------------------+----------+-----------------------+");
        } catch (SQLException e) {
            throw new ATMExceptions("Database error occurred: " + e.getMessage());
        }
    }
}

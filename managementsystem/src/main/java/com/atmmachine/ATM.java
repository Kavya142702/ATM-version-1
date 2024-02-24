package com.atmmachine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

import com.atmTransactions.Transaction;
import com.atmaccount.CheckingAccount;
import com.atmaccount.SavingsAccount;
import com.atmdatabasemanagement.DatabaseConnector;
import com.atmexceptions.ATMExceptions;
import com.users.OTPManager;

public class ATM {
	
	private static DatabaseConnector db = new DatabaseConnector();
    
	static ATM_Machine atmMoney = new ATM_Machine();
	
    public static void performCashWithdrawal(Scanner sc, String debitCard_No, int atm_id) throws ClassNotFoundException, ATMExceptions {
        boolean isValidAmount = false;
        double amount = 0;

        while (!isValidAmount) {
            System.out.println("Enter the amount to withdraw: ");

            // Validate input
            if (!sc.hasNextDouble()) {
                System.out.println("Invalid input. Please enter a valid numeric amount.");
                sc.nextLine(); // Consume invalid input
                continue;
            }
            amount = sc.nextDouble();
            sc.nextLine();

            // Check for negative amount
            if (amount < 0) {
                System.out.println("Negative amount entered. Please enter a positive amount.");
                continue;
            }

            // Check for zero amount
            if (amount == 0) {
                System.out.println("Zero amount entered. Please enter a non-zero amount.");
                continue;
            }

            // Check if the amount is a multiple of 100, 200, or 500
            if (amount % 100 != 0 && amount % 200 != 0 && amount % 500 != 0) {
                System.out.println("Amount must be a multiple of 100, 200, or 500.");
                continue;
            }

            // Amount is valid
            isValidAmount = true;
        }

        // Proceed with the withdrawal process
        try (Connection conn = db.getDBConnection()) {
            // Retrieve account type
            String withdrawQuery = "SELECT Account_Type FROM KAVYAP.Account WHERE Account_No = (SELECT Account_No FROM KAVYAP.ATM_Card WHERE debitCard_No = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(withdrawQuery)) {
                stmt.setString(1, debitCard_No);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String accountType = rs.getString("Account_Type");
                        if (accountType.equalsIgnoreCase("Current")) {
                            double dispensedAmount = CheckingAccount.performCurrentAccountWithdrawal(conn, debitCard_No, amount);
                            recordTransaction(debitCard_No, "Credit", dispensedAmount);
                            atmMoney.updateATMBalance(conn, dispensedAmount, false, atm_id);
                            printDispensedAmount(dispensedAmount);
                            performBalanceInquiry(debitCard_No);
                        } else if (accountType.equalsIgnoreCase("Savings")) {
                            double dispensedAmount = SavingsAccount.performSavingsAccountWithdrawal(conn, debitCard_No, amount);
                            recordTransaction(debitCard_No, "Credit", dispensedAmount);
                            atmMoney.updateATMBalance(conn, dispensedAmount, false, atm_id);
                            printDispensedAmount(dispensedAmount);
                            performBalanceInquiry(debitCard_No);
                        } else {
                            throw new ATMExceptions("Unsupported account type.");
                        }
                    } else {
                        throw new ATMExceptions("Account not found for the given debit card.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new ATMExceptions("Database error occurred: " + e.getMessage());
        }
    }
	
	public static void performCashDeposit(Scanner sc, String debitCard_No, int atm_id) throws ClassNotFoundException, ATMExceptions {
	    System.out.println("Enter the amount to deposit: ");
	    double amount = sc.nextDouble();
	    sc.nextLine();
	    try (Connection conn = db.getDBConnection()) {
	        String depositQuery = "SELECT Account_Type FROM KAVYAP.Account WHERE Account_No = (SELECT Account_No FROM KAVYAP.ATM_Card WHERE debitCard_No = ?)";
	        try (PreparedStatement stmt = conn.prepareStatement(depositQuery)) {
	            stmt.setString(1, debitCard_No);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    String accountType = rs.getString("Account_Type");
	                    if (accountType.equalsIgnoreCase("Current")) {
	                        double dispensedAmount = CheckingAccount.performCurrentAccountDeposit(conn, debitCard_No, amount);
	                        recordTransaction(debitCard_No, "Debit", dispensedAmount);
	                        atmMoney.updateATMBalance(conn, dispensedAmount, true, atm_id); 
	                        printDispensedAmount(dispensedAmount);
	                    } else if (accountType.equalsIgnoreCase("Savings")) {
	                        double dispensedAmount = SavingsAccount.performSavingsAccountDeposit(conn, debitCard_No, amount);
	                        recordTransaction(debitCard_No, "Debit", dispensedAmount);
	                        atmMoney.updateATMBalance(conn, dispensedAmount, true, atm_id); 
	                        printDispensedAmount(dispensedAmount);
	                    } else {
	                        throw new ATMExceptions("Unsupported account type.");
	                    }
	                } else {
	                    throw new ATMExceptions("Account not found for the given debit card.");
	                }
	            }
	        }
	    } catch (SQLException e) {
	        throw new ATMExceptions("Database error occurred: " + e.getMessage());
	    }
	}
	
	private static void printDispensedAmount(double amount) {
	    int[] denominations = {500, 200, 100}; 
	    int[] dispensedNotes = new int[3]; 
	    int totalAmount = 0;
	    
	    for (int i = 0; i < denominations.length; i++) {
	        dispensedNotes[i] = (int) (amount / denominations[i]);
	        totalAmount += dispensedNotes[i] * denominations[i]; // Calculate total amount
	        amount %= denominations[i];
	    }
	    
	    System.out.println("Notes Count:");
	    for (int i = 0; i < denominations.length; i++) {
	        System.out.println(denominations[i] + " * " + dispensedNotes[i] + " = " + (denominations[i] * dispensedNotes[i]));
	    }
	    System.out.println("Total amount = " + totalAmount);
	}

	
	public static void performBalanceInquiry(String debitCard_No) throws ClassNotFoundException, ATMExceptions {
	    try (Connection conn = db.getDBConnection()) {
	        String balanceQuery = "SELECT a.Balance, ac.Account_No FROM KAVYAP.Account a INNER JOIN KAVYAP.ATM_Card ac ON a.Account_No = ac.Account_No WHERE ac.debitCard_No = ?";
	        PreparedStatement balanceStmt = conn.prepareStatement(balanceQuery);
	        balanceStmt.setString(1, debitCard_No);
	        ResultSet balanceRs = balanceStmt.executeQuery();
	        if (balanceRs.next()) {
	            double balance = balanceRs.getDouble("Balance");
	            String accountNo = balanceRs.getString("Account_No");
	            System.out.println("Your Account No is : " + accountNo);
	            System.out.println("Your current balance is : " + balance + " INR");
	        } else {
	            throw new ATMExceptions("Failed to fetch account balance.");
	        }
	    } catch (SQLException e) {
	        throw new ATMExceptions("Database error occurred: " + e.getMessage());
	    }
	}

	
	public static void performFundTransfer(Scanner sc, String debitCard_No) throws ClassNotFoundException, ATMExceptions {
	    try (Connection conn = db.getDBConnection()) {
	        System.out.println("Enter your account number: ");
	        String senderAccountNo = sc.nextLine();
	        System.out.println("Enter the destination account number: ");
	        String receiverAccountNo = sc.nextLine();
	        System.out.println("Enter the amount to transfer: ");
	        double amount = sc.nextDouble();
	        sc.nextLine(); 
	        String senderBalanceQuery = "SELECT Balance FROM KAVYAP.Account WHERE Account_No = ?";
	        try (PreparedStatement senderBalanceStmt = conn.prepareStatement(senderBalanceQuery)) {
	        	senderBalanceStmt.setString(1, senderAccountNo);
	            try (ResultSet senderBalanceRs = senderBalanceStmt.executeQuery()) {
	               if (senderBalanceRs.next()) {
	                  double senderBalance = senderBalanceRs.getDouble("Balance");
	                    if (senderBalance >= amount) {
	                        String updateSenderQuery = "UPDATE KAVYAP.Account SET Balance = Balance - ? WHERE Account_No = ?";
	                       try (PreparedStatement updateSenderStmt = conn.prepareStatement(updateSenderQuery)) {
	                          updateSenderStmt.setDouble(1, amount);
	                           updateSenderStmt.setString(2, senderAccountNo);
	                            int senderRowsupdate = updateSenderStmt.executeUpdate();
	                            if (senderRowsupdate > 0) {
	                               String updateReceiverQuery = "UPDATE KAVYAP.Account SET Balance = Balance + ? WHERE Account_No = ?";
	                                try (PreparedStatement updateReceiverStmt = conn.prepareStatement(updateReceiverQuery)) {
	                                   updateReceiverStmt.setDouble(1, amount);
	                                   updateReceiverStmt.setString(2, receiverAccountNo);
	                                    int receiverRowsupdate = updateReceiverStmt.executeUpdate();
	                                    if (receiverRowsupdate > 0) {
	                                      System.out.println("Fund transfer successful!");
	                                      recordTransaction(debitCard_No, "Transfer", amount);
	                                        // sendTransactionNotification(debitCard_No, "Transfer", amount);
	                                        //reduceATMMoney(conn, amount);
	                                 } else throw new ATMExceptions("Failed to transfer funds to the destination account.");
	                               }
	                           } else  throw new ATMExceptions("Failed to deduct funds from the sender account."); 
	                        }
	                    } else  throw new ATMExceptions("Insufficient balance in the sender account...Fund transfer failed.");
	                } else  throw new ATMExceptions("Sender account not found.");
	            }
	        }
	    } catch (SQLException e) {
	        throw new ATMExceptions("Database error occurred: " + e.getMessage());
	    }
	}

	
	@SuppressWarnings("static-access")
	private static void recordTransaction(String debitCard_No, String transactionType, double amount) throws ClassNotFoundException, ATMExceptions {
	        int transactionID = generateTransactionID();
	        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		    try (Connection conn = db.getDBConnection()) {
	            String insertQuery = "INSERT INTO KAVYAP.Transaction (Transaction_ID, DebitCard_No, Transaction_Type, Amount, Transaction_Date) VALUES (?, ?, ?, ?, ?)";
	            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
	                insertStmt.setInt(1, transactionID);
	                insertStmt.setString(2, debitCard_No);
	                insertStmt.setString(3, transactionType);
	                insertStmt.setDouble(4, amount);
	                insertStmt.setTimestamp(5, timestamp);
	                int rowsAffected = insertStmt.executeUpdate();
	                if (rowsAffected > 0) {
	                	System.out.println("Your transaction is Successfully recorded...Check your balance.");
	                } else {
	                    throw new ATMExceptions("Failed to record the transaction.");
	                }
	            }
	        } catch (SQLException e) {
	            throw new ATMExceptions("Database error occurred: " + e.getMessage());
	        }
	    }

	    private static int generateTransactionID() throws ClassNotFoundException, ATMExceptions {
	        int lastTransactionID = getLastTransactionID();
	        return lastTransactionID + 1;
	    }

	    private static int getLastTransactionID() throws ClassNotFoundException, ATMExceptions {
	        int lastTransactionID = 0;
		    try (
			Connection conn = db.getDBConnection()) {
	            String query = "SELECT MAX(Transaction_ID) AS LastTransactionID FROM KAVYAP.Transaction ";
	            try (PreparedStatement pstmt = conn.prepareStatement(query);
	                 ResultSet resultSet = pstmt.executeQuery()) {
	                if (resultSet.next()) {
	                    lastTransactionID = resultSet.getInt("LastTransactionID");
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return lastTransactionID;
	    }
	    
	    public static void getMiniStatement(String debitCardNo) throws ATMExceptions, ClassNotFoundException {
	        try (Connection conn = db.getDBConnection()) {
	            // Retrieve additional details
	            String accountNoQuery = "SELECT Account_No FROM KAVYAP.ATM_Card WHERE DebitCard_No = ?";
	            PreparedStatement accountNoStmt = conn.prepareStatement(accountNoQuery);
	            accountNoStmt.setString(1, debitCardNo);
	            ResultSet accountNoRs = accountNoStmt.executeQuery();
	            String accountNo = "";
	            if (accountNoRs.next()) {
	                accountNo = accountNoRs.getString("Account_No");
	            }

	            String balanceQuery = "SELECT Balance FROM KAVYAP.Account WHERE Account_No = ?";
	            PreparedStatement balanceStmt = conn.prepareStatement(balanceQuery);
	            balanceStmt.setString(1, accountNo);
	            ResultSet balanceRs = balanceStmt.executeQuery();
	            double balance = 0.0;
	            if (balanceRs.next()) {
	                balance = balanceRs.getDouble("Balance");
	            }

	            // Print additional details
	            System.out.println("Debit Card Number: " + debitCardNo);
	            System.out.println("Account Number: " + accountNo);
	            System.out.println("Available Balance: " + balance + " INR");

	            // Retrieve transaction records
	            String selectQuery = "SELECT * FROM (SELECT * FROM KAVYAP.Transaction WHERE DebitCard_No = ? ORDER BY Transaction_Date DESC) WHERE ROWNUM <= 10";
	            PreparedStatement stmt = conn.prepareStatement(selectQuery);
	            stmt.setString(1, debitCardNo);
	            ResultSet rs = stmt.executeQuery();
	            LinkedList<Transaction> transactionList = new LinkedList<>();
	            while (rs.next()) {
	                int transactionID = rs.getInt("Transaction_ID");
	                String transactionType = rs.getString("Transaction_Type");
	                double amount = rs.getDouble("Amount");
	                Date transactionDate = rs.getTimestamp("Transaction_Date");

	                Transaction transaction = new Transaction(transactionID, debitCardNo, transactionType, amount, transactionDate);
	                transactionList.add(transaction);
	            }
	            // Print transaction records
	            System.out.println("+---------------+-------------------+------------------+----------+---------------------+");
	            System.out.println("| Transaction ID| Debit Card Number | Transaction Type | Amount   | Transaction Date    |");
	            System.out.println("+---------------+-------------------+------------------+----------+---------------------+");
	            for (Transaction transactions : transactionList) {
	                System.out.printf("| %-13d | %-17s | %-16s | %-8.2f | %-19s |%n", transactions.getTransaction_ID(),
	                        transactions.getDebitCard_No(), transactions.getTransaction_Type(), transactions.getAmount(),
	                        transactions.getTransaction_Date());
	            }
	            System.out.println("+---------------+-------------------+------------------+----------+---------------------+");
	        } catch (SQLException e) {
	            throw new ATMExceptions("Database error occurred: " + e.getMessage());
	        }
	    }


}

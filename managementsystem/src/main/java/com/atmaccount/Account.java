package com.atmaccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

import com.atmTransactions.Transaction;
import com.atmdatabasemanagement.DatabaseConnector;
import com.atmexceptions.ATMExceptions;
import com.atmexceptions.DatabaseConnectionException;
import com.users.BankStaff;
import com.users.Customer;
import com.atmcard.ATMCard;

public class Account {
    private String accountNo;
    private String accountType;
    private double balance;
    private Date openDate;
    private double interestRate;
    private String accountStatus;
    private int customerID;
    private int branchID;

    public Account(String accountNo, int customerID, String accountType, double balance, Date openDate, int branchID, double interestRate, String accountStatus) {
    	this.customerID = customerID;
        this.accountNo = accountNo;
        this.accountType = accountType;
        this.balance = balance;
        this.openDate = openDate;
        this.branchID = branchID;
        this.interestRate = interestRate;
        this.accountStatus = accountStatus;
    }

    public Account() {
		// TODO Auto-generated constructor stub
	}

	// Getters and Setters
    public String getAccountNo() {
        return accountNo;
    }

    public String getAccountType() {
        return accountType;
    }

    public double getBalance() {
        return balance;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

	@Override
	public String toString() {
		return "Account [accountNo=" + accountNo + ", accountType=" + accountType + ", balance=" + balance
				+ ", openDate=" + openDate + ", interestRate=" + interestRate + ", accountStatus=" + accountStatus
				+ "]";
	}
	
	static DatabaseConnector db = new DatabaseConnector();
    static BankStaff staff = new BankStaff();
    static Transaction transaction = new Transaction();
    static Customer customer = new Customer();

    public static void insertAccountDetails(Scanner sc) throws ClassNotFoundException, ATMExceptions, DatabaseConnectionException {
        String accountNo = generateAccountNumber();
        if (accountNo.isEmpty()) {
            return;
        }
        try (Connection conn = db.getDBConnection()) {
            String status = getStatusFromGuest(conn);
            if ("Processed".equals(status)) {
                System.out.println("Status is already processed. Returning to previous menu.");
                return;
            }

            PreparedStatement ps = conn.prepareStatement("INSERT INTO KAVYAP.Account (Account_No, Customer_ID, Account_Type, Balance, Open_Date,"
                    + "Branch_ID, Interest_Rate, Account_Status) VALUES (?, ?, ?, ?, ?, ?, ?, 'Active')");
            int id = customer.getLastCustomerID(conn);
            sc.nextLine();
            System.out.println("Enter the account Type (Savings/Current): ");
            String accountType = sc.nextLine();
            System.out.println("Enter the Initial Balance: ");
            double balance = sc.nextDouble();
            ps.setString(1, accountNo);
            ps.setInt(2, id);
            ps.setString(3, accountType);
            ps.setDouble(4, balance);
            ps.setObject(5, LocalDate.now());
            ps.setInt(6, 101);
            ps.setDouble(7, 3.5);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Data inserted into the Account table successfully!");
                System.out.println("Account Number: " + accountNo);
                System.out.println("Account created successfully!");
                sc.nextLine();
                System.out.println("Would you like to create ATM card? (yes/no):");
                String choice = sc.nextLine().toLowerCase();
                if (choice.equals("yes")) {
                    ATMCard atm = new ATMCard();
                    atm.insertATMCardDetails(id, accountNo);
                } else {
                    System.out.println("Account creation canceled.");
                }
            } else {
                System.out.println("Failed to insert data into the Account table.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static String getStatusFromGuest(Connection conn) throws SQLException {
        String status = "";
        try (PreparedStatement selectStmt = conn.prepareStatement("SELECT Status FROM KAVYAP.guest WHERE Status = 'Processed'", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    status = rs.getString("Status");
                }
            }
        }
        return status;
    }

    private static String generateAccountNumber() {
        StringBuilder accountNumber = new StringBuilder("1225");
        Random random = new Random();
        for (int i = 4; i < 13; i++) {
            accountNumber.append(random.nextInt(10));
        }
        return accountNumber.toString();
    }
}
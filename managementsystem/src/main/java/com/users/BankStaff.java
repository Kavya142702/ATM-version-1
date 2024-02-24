package com.users;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import com.atmaccount.Account;
import com.atmdatabasemanagement.DatabaseConnector;
import com.atmexceptions.ATMExceptions;

public class BankStaff {

    DatabaseConnector db = new DatabaseConnector();

    @SuppressWarnings("static-access")
    public LinkedList<Account> viewAccount() throws ClassNotFoundException, ATMExceptions {
        LinkedList<Account> accountList = new LinkedList<>();
        try (Connection conn = db.getDBConnection();
             PreparedStatement viewstmt = conn.prepareStatement("SELECT * FROM KAVYAP.Account");
             ResultSet balanceRs = viewstmt.executeQuery()) {

            System.out.println("+--------------+-------------+--------------+-------------+-------------+------------+-------------+---------------+");
            System.out.println("| Account No   | Customer ID | Account Type | Balance     | Open Date   | Branch ID  | Interest Rate| Account Status|");
            System.out.println("+--------------+-------------+--------------+-------------+-------------+------------+-------------+---------------+");

            while (balanceRs.next()) {
                String accountNo = balanceRs.getString("Account_No");
                int customerID = balanceRs.getInt("Customer_ID");
                String accountType = balanceRs.getString("Account_Type");
                double balance = balanceRs.getDouble("Balance");
                Date openDate = balanceRs.getDate("Open_Date");
                int branchID = balanceRs.getInt("Branch_ID");
                double interestRate = balanceRs.getDouble("Interest_Rate");
                String accountStatus = balanceRs.getString("Account_Status");

                System.out.printf("| %-12s | %-11d | %-12s | %-11.2f | %-11s | %-10d | %-12.2f | %-13s |%n",
                        accountNo, customerID, accountType, balance, openDate.toString(), branchID, interestRate, accountStatus);

                Account account = new Account(accountNo, customerID, accountType, balance, openDate, branchID, interestRate, accountStatus);
                accountList.add(account);
            }

            System.out.println("+--------------+-------------+--------------+-------------+-------------+------------+-------------+---------------+");
        } catch (SQLException e) {
            throw new ATMExceptions("Database error occurred: " + e.getMessage());
        }
        return accountList;
    }

}

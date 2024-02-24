package com.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import com.atmexceptions.*;
import com.atmmachine.ATM_Machine;
import com.atmTransactions.Transaction;
import com.atmTransactions.TransactionOptions;
import com.atmaccount.Account;
import com.atmdatabasemanagement.*;

public class BankEmployee {

    static DatabaseConnector db = new DatabaseConnector();
    static BankStaff staff = new BankStaff();
    static Transaction transaction = new Transaction();
   
    @SuppressWarnings("static-access")
    public boolean employeeLogin(Scanner sc) throws InvalidCredentialsException, DatabaseConnectionException, ClassNotFoundException, ATMExceptions {
        String username;
        String password;
        
        try (Connection conn = db.getDBConnection()) {
            System.out.println("Please enter your Username: ");
            username = sc.nextLine();
            System.out.println("Please enter your password: ");
            password = sc.nextLine();
            String query = "SELECT * FROM KAVYAP.Bank_Employees WHERE userName = ? AND Password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                ResultSet resultSet = pstmt.executeQuery();
                if (!resultSet.next()) {
                    throw new InvalidCredentialsException("Invalid credentials. Please try again.");
                } else {
                    String position = resultSet.getString("Position");
                    do {
                        switch (position) {
                            case "Bank Staff":
                                handleBankStaffActions(sc);
                                break;
                            case "Maintainer":
                                handleMaintainerActions(sc);
                                break;
                        }
                    } while (true);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Database error occurred: " + e.getMessage());
        }
    }

    @SuppressWarnings("static-access")
    private static void handleBankStaffActions(Scanner sc) throws ATMExceptions, ClassNotFoundException, DatabaseConnectionException, InvalidCredentialsException, SQLException {
        int staffChoice;
        do {
            System.out.println("********************************************");
            System.out.println("*          Bank Staff Actions              *");
            System.out.println("********************************************");
            System.out.println("* 1. Create Account                        *");
            System.out.println("* 2. View Accounts                         *");
            System.out.println("* 3. View Transactions                     *");
            System.out.println("* 4. Back                                  *");
            System.out.println("* 5. Exit                                  *");
            System.out.println("********************************************");
            System.out.println("Enter Your Choice: ");
            staffChoice = sc.nextInt();
            switch (staffChoice) {
                case 1:
                    Customer.insertGuestsIntoCustomers();
                    Account.insertAccountDetails(sc);
                    break;
                case 2:
                    staff.viewAccount();
                    break;
                case 3:
                    transaction.viewAllTransactions();
                    break;
                case 4:
                	TransactionOptions transactions = new TransactionOptions();
                	transactions.adminOptions(sc);
                case 5:
                    System.exit(0);
                    break;
                default:
                    throw new ATMExceptions("Invalid choice. Please enter a valid option.");
            }
        } while (true);
    }


    
    @SuppressWarnings("resource")
	public static void processGuestRequests() throws DatabaseConnectionException, ATMExceptions, ClassNotFoundException {
    	Scanner sc = new Scanner(System.in);
        try (Connection conn = db.getDBConnection()) {
        	Customer.insertGuestsIntoCustomers();
            Account.insertAccountDetails(sc);
            } catch (SQLException e) {
            throw new DatabaseConnectionException("Database error occurred: " + e.getMessage());
        }
    }

    private static void handleMaintainerActions(Scanner sc) throws ATMExceptions, ClassNotFoundException, DatabaseConnectionException, InvalidCredentialsException {
        System.out.println("Enter your ATM ID: ");
        int atmId = sc.nextInt();
        int maintainerChoice;
        do {
            System.out.println("********************************************");
            System.out.println("*          Maintainer Actions              *");
            System.out.println("********************************************");
            System.out.println("* 1. View available cash in the ATM        *");
            System.out.println("* 2. Refill ATM with cash                  *");
            System.out.println("* 3. Back                                  *");
            System.out.println("* 4. Exit                                  *");
            System.out.println("********************************************");
            System.out.println("Enter Your Choice: ");
            maintainerChoice = sc.nextInt();
            switch (maintainerChoice) {
                case 1:
                    ATM_Machine.viewATMBalance(sc, atmId);
                    break;
                case 2:
                    ATM_Machine.addMoneyToATM(atmId);
                    break;
                case 3:
                    TransactionOptions.adminOptions(sc);
                case 4:
                    System.exit(0);
                    break;
                default:
                    throw new ATMExceptions("Invalid choice. Please enter a valid option.");
            }
        } while (true);
    }


     static int branchId = 0; 
     static int bankId = -1;

    public static int getBranchId() {
        return branchId;
    }

    public static void setBranchId(int branchId) {
        BankEmployee.branchId = branchId;
    }

    public static int getBankId() {
        return bankId;
    }

    public static void setBankId(int bankId) {
        BankEmployee.bankId = bankId;
    }

    @SuppressWarnings({ "static-access", "unused" })
    
	private static void setBranchAndBankIds(int employeeId, String password) throws ATMExceptions, DatabaseConnectionException {
        try (Connection conn = db.getDBConnection()) {
            PreparedStatement branchIdStatement = conn.prepareStatement("SELECT Branch_ID FROM KAVYAP.Bank_Employees WHERE Employee_ID = ? AND password = ?");
            branchIdStatement.setInt(1, employeeId);
            branchIdStatement.setString(2, password);
            ResultSet branchIdResult = branchIdStatement.executeQuery();
            if (branchIdResult.next()) {
                branchId = branchIdResult.getInt("Branch_ID");
            }
            PreparedStatement bankIdStatement = conn.prepareStatement("SELECT Bank_ID FROM KAVYAP.Branch WHERE Branch_ID = ?");
            bankIdStatement.setInt(1, branchId);
            ResultSet bankIdResult = bankIdStatement.executeQuery();
            if (bankIdResult.next()) {
                bankId = bankIdResult.getInt("Bank_ID");
            } else {
                throw new DatabaseConnectionException("Bank_ID not found for the given Branch_ID.");
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Database error occurred: " + e.getMessage());
        }
    }
}

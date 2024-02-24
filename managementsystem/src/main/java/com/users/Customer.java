package com.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Scanner;
import com.atmexceptions.ATMExceptions;
import com.atmexceptions.DatabaseConnectionException;
import com.atmregex.ATMRegex;
import com.atmdatabasemanagement.*;
import com.atmaccount.*;

public class Customer extends Users {
	private String customer_ID;
	
	public Customer() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Customer(String name, Long mobileNumber, String email, String address, Date dob, String gender,
			String customer_ID) {
		super(name, mobileNumber, email, address, dob, gender);
		this.customer_ID = customer_ID;
	}
	
	static ATMRegex regex = new ATMRegex();
	static DatabaseConnector db = new DatabaseConnector();
	static BankStaff bs = new BankStaff();
	
    public void changeAccountHolderName(Scanner sc, String debitCardNo) throws ClassNotFoundException, DatabaseConnectionException, ATMExceptions {
    try (Connection conn = db.getDBConnection()) {
        System.out.println("Enter your current Name: ");
        String currentName = sc.nextLine();
        System.out.println("Enter your updated Name: ");
        String updatedName = sc.nextLine();
        regex.validateInput(updatedName, name -> name.matches("^[a-zA-Z\\s]+$"), "Invalid name format. Please enter alphabetic characters only.");
        String updateQuery = "UPDATE KAVYAP.Bank_Customers SET Customer_Name = ? WHERE Customer_Name = ? AND Customer_ID = (SELECT Customer_ID FROM KAVYAP.ATM_Card WHERE debitCard_No = ?)";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
            updateStmt.setString(1, updatedName);
            updateStmt.setString(2, currentName);
            updateStmt.setString(3, debitCardNo);
            int updatedRows = updateStmt.executeUpdate();
            if (updatedRows > 0) {
                System.out.println("Account holder name updated successfully!");
            } else {
                throw new DatabaseConnectionException("Failed to update account holder name.");
            }
        }
    } catch (SQLException e) {
        throw new DatabaseConnectionException("An error occurred while updating account holder name: " + e.getMessage());
    }
}

    @SuppressWarnings("static-access")
	public void changeAddress(Scanner sc, String debitCardNo) throws DatabaseConnectionException, ATMExceptions {
        try (Connection conn = db.getDBConnection()) {
            System.out.println("Enter your current Address: ");
            String currentAddress = sc.nextLine();
            System.out.println("Enter your new Address: ");
            String newAddress = sc.nextLine();
            regex.validateInput(newAddress, address -> true, "Address validation failed.");
            String updateQuery = "UPDATE KAVYAP.Bank_Customers SET Address = ? WHERE Address = ? AND Customer_ID = (SELECT Customer_ID FROM KAVYAP.ATM_Card WHERE debitCard_No = ?)";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, newAddress);
                updateStmt.setString(2, currentAddress);
                updateStmt.setString(3, debitCardNo);
                int updatedRows = updateStmt.executeUpdate();
                if (updatedRows > 0) {
                    System.out.println("Address updated successfully!");
                } else {
                    System.out.println("Failed to update address.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("An error occurred while updating address.");
        }
    }

    @SuppressWarnings("static-access")
	public void changeMobileNumber(Scanner sc, String debitCardNo) throws DatabaseConnectionException, ATMExceptions {
        try (Connection conn = db.getDBConnection()) {
            System.out.println("Enter your current Mobile Number: ");
            String currentMobileNumber = sc.nextLine();
            System.out.println("Enter your new Mobile Number: ");
            String newMobileNumber = sc.nextLine();
            regex.validateInput(newMobileNumber, mobile -> mobile.matches("^[6789]\\d{9}$"), "Invalid mobile number format. Please enter a 10-digit mobile number.");
            String updateQuery = "UPDATE KAVYAP.Bank_Customers SET MobileNumber = ? WHERE MobileNumber = ? AND Customer_ID = (SELECT Customer_ID FROM KAVYAP.ATM_Card WHERE debitCard_No = ?)";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, newMobileNumber);
                updateStmt.setString(2, currentMobileNumber);
                updateStmt.setString(3, debitCardNo);
                int updatedRows = updateStmt.executeUpdate();
                if (updatedRows > 0) {
                    System.out.println("Mobile number updated successfully!");
                } else {
                    System.out.println("Failed to update mobile number.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("An error occurred while updating mobile number.");
        }
    }

    @SuppressWarnings("static-access")
	public void changeEmail(Scanner sc, String debitCardNo) throws DatabaseConnectionException, ATMExceptions {
        try (Connection conn = db.getDBConnection()) {
            System.out.println("Enter your current Email: ");
            String currentEmail = sc.nextLine();
            System.out.println("Enter your new Email: ");
            String newEmail = sc.nextLine();
            regex.validateInput(newEmail, email -> email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$"), "Invalid email format. Please enter a valid email address.");
            String updateQuery = "UPDATE KAVYAP.Bank_Customers SET Email = ? WHERE Email = ? AND Customer_ID = (SELECT Customer_ID FROM KAVYAP.ATM_Card WHERE debitCard_No = ?)";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, newEmail);
                updateStmt.setString(2, currentEmail);
                updateStmt.setString(3, debitCardNo);
                int updatedRows = updateStmt.executeUpdate();
                if (updatedRows > 0) {
                    System.out.println("Email updated successfully!");
                } else {
                    System.out.println("Failed to update email.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("An error occurred while updating email.");
        }
    }


    public static void insertGuestsIntoCustomers() throws SQLException, DatabaseConnectionException, ClassNotFoundException, ATMExceptions {
        String selectSQL = "SELECT Guest_Name, Address, MobileNumber, Email, Gender, DOB, Aadhar_Number, PAN_Number FROM KAVYAP.guest WHERE Status = ?";
        String updateGuestStatusSQL = "UPDATE KAVYAP.guest SET Status = ? WHERE MobileNumber = ?";
        Connection conn = null;
        try {
            conn = db.getDBConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                 PreparedStatement updateStmt = conn.prepareStatement(updateGuestStatusSQL)) {
                selectStmt.setString(1, "Requested");
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("No guests with status 'Requested' found. Exiting.");
                        return; // No guests with requested status found, exit method
                    }
                    rs.beforeFirst(); // Reset cursor position
                    while (rs.next()) {
                        String name = rs.getString("Guest_Name");
                        String address = rs.getString("Address");
                        String mobileNumber = rs.getString("MobileNumber");
                        String email = rs.getString("Email");
                        String gender = rs.getString("Gender");
                        Date dob = rs.getDate("DOB");
                        String aadharNumber = rs.getString("Aadhar_Number");
                        String panNumber = rs.getString("PAN_Number");
                        if (!customerExists(conn, mobileNumber)) {
                            insertIntoBankCustomers(conn, name, address, mobileNumber, email, gender, dob, aadharNumber, panNumber);
                        } else {
                            System.out.println("Customer already exists: " + name);
                        }
                        updateStmt.setString(1, "Processed");
                        updateStmt.setString(2, mobileNumber);
                        updateStmt.addBatch();
                    }
                }
                updateStmt.executeBatch();
                conn.commit();
            }
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }



    private static boolean customerExists(Connection conn, String mobileNumber) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM KAVYAP.Bank_Customers WHERE MobileNumber = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, mobileNumber);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    return count > 0;
                }
            }
        }
        return false;
    }

    @SuppressWarnings({ "static-access", "resource" })
    private static void insertIntoBankCustomers(Connection conn, String name, String address, String mobileNumber, String email, String gender, Date dob, String aadharNumber, String panNumber) throws SQLException, DatabaseConnectionException, ClassNotFoundException, ATMExceptions {
        Scanner sc = new Scanner(System.in);
        String sql = "INSERT INTO KAVYAP.Bank_Customers (Customer_Id, Customer_Name, Address, MobileNumber, Email, Gender, DOB, Aadhar_Number, PAN_Number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, generateCustomerID(conn));
            pstmt.setString(2, name);
            pstmt.setString(3, address);
            pstmt.setString(4, mobileNumber);
            pstmt.setString(5, email);
            pstmt.setString(6, gender);
            pstmt.setDate(7, dob);
            pstmt.setString(8, aadharNumber);
            pstmt.setString(9, panNumber);
            
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Customer details added successfully");
            } else {
                System.out.println("No rows inserted into Bank_Customers table.");
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Database error occurred: " + e.getMessage());
        }
    }


    public static int generateCustomerID(Connection conn) throws ClassNotFoundException, DatabaseConnectionException, ATMExceptions {
        int lastCustomerID = getLastCustomerID(conn);
        return lastCustomerID + 1;
    }

    public static int getLastCustomerID(Connection conn) throws ClassNotFoundException, DatabaseConnectionException, ATMExceptions {
        int lastCustomerID = 0;
        try {
            String query = "SELECT MAX(Customer_ID) AS LastCustomerID FROM KAVYAP.Bank_Customers";
            try (PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    lastCustomerID = resultSet.getInt("LastCustomerID");
                }
            }
            return lastCustomerID;
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error in retrieving last customer ID: " + e.getMessage());
    }
    }
    public static String validateInput(String input, String fieldName, String regexPattern) {
        Scanner sc = new Scanner(System.in);
        while (!input.matches(regexPattern)) {
            System.out.println("Invalid input for " + fieldName + ". Please enter a valid input: ");
            input = sc.nextLine();
        }
        return input;
    }
}
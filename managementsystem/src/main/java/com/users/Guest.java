package com.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Scanner;

import com.atmdatabasemanagement.DatabaseConnector;
import com.atmexceptions.ATMExceptions;
import com.atmexceptions.DatabaseConnectionException;
import com.atmregex.*;

public class Guest {
    private int guestID;
    private String Guest_Name;
    private String address;
    private String mobileNumber;
    private String email;
    private String gender;
    private Date dob;
    private String aadharNumber;
    private String panNumber;

    public Guest() {
    }

    public Guest(String Guest_Name, String address, String mobileNumber, String email, String gender, Date dob, String aadharNumber, String panNumber) {
        this.Guest_Name = Guest_Name;
        this.address = address;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.aadharNumber = aadharNumber;
        this.panNumber = panNumber;
    }

    // Getters and Setters
    public int getGuestID() {
        return guestID;
    }

    public void setGuestID(int guestID) {
        this.guestID = guestID;
    }

    public String getGuest_Name() {
        return Guest_Name;
    }

    public void setGuest_Name(String Guest_Name) {
        this.Guest_Name = Guest_Name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    // toString method
    @Override
    public String toString() {
        return "Guest [guestID=" + guestID + ", Guest_Name=" + Guest_Name + ", address=" + address + ", mobileNumber=" + mobileNumber
                + ", email=" + email + ", gender=" + gender + ", dob=" + dob + ", aadharNumber=" + aadharNumber
                + ", panNumber=" + panNumber + "]";
    }
    
    static DatabaseConnector db = new DatabaseConnector();
    static ATMRegex regex = new ATMRegex();
    
    @SuppressWarnings("static-access")
	public static void guestDetails(Scanner sc) throws ATMExceptions, DatabaseConnectionException {
        try (Connection conn = db.getDBConnection()) {
            String sql = "INSERT INTO KAVYAP.Guest (Guest_ID, Guest_Name, Address, MobileNumber, Email, Gender, DOB, Aadhar_Number, PAN_Number) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            System.out.println("Enter the Customer Guest_Name: ");
            String Guest_Name = regex.validateInput(sc.nextLine(), "Customer Guest_Name", "[a-zA-Z ]+");
            System.out.println("Enter the Address: ");
            String custAddress = regex.validateInput(sc.nextLine(), "Address", ".+");
            System.out.println("Enter the Mobile Number: ");
            String mobNum = regex.validateInput(sc.nextLine(), "Mobile Number", ATMRegex.getMobilePattern());
            System.out.println("Enter the Email: ");
            String custEmail = regex.validateInput(sc.nextLine(), "Email", ATMRegex.getEmailPattern());
            System.out.println("Enter the Gender: ");
            String custGender = regex.validateInput(sc.nextLine(), "Gender", "(?i)^(male|female)$");
            String dobInput = "";
            Date custDob = null;
            boolean validInput = false;

            while (!validInput) {
                System.out.println("Enter the Date of Birth (YYYY-MM-DD): ");
                dobInput = sc.nextLine();
                
                if (dobInput.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    validInput = true;
                    custDob = Date.valueOf(dobInput);
                } else {
                    System.out.println("Invalid input format for Date of Birth. Please enter the date in the format YYYY-MM-DD.");
                }
            }
            System.out.println("Enter the Aadhar Number: ");
            String aadhar = regex.validateInput(sc.nextLine(), "Aadhar Number", ATMRegex.getAadharPattern());
            System.out.println("Enter the Pan Number: ");
            String pan = regex.validateInput(sc.nextLine(), "PAN Number", ATMRegex.getPanPattern());

            pstmt.setInt(1, generateGuestID(conn));
            pstmt.setString(2, Guest_Name);
            pstmt.setString(3, custAddress);
            pstmt.setString(4, mobNum);
            pstmt.setString(5, custEmail);
            pstmt.setString(6, custGender);
            pstmt.setDate(7, custDob);
            pstmt.setString(8, aadhar);
            pstmt.setString(9, pan);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Request sent successfully");
            } else {
                throw new DatabaseConnectionException("Failed to insert data into Guest table.");
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Database error occurred: " + e.getMessage());
        }
    }

    public static int generateGuestID(Connection conn) throws SQLException, DatabaseConnectionException {
        int lastGuestID = 0;
        try {
            String query = "SELECT MAX(Guest_ID) AS LastGuestID FROM KAVYAP.Guest";
            try (PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    lastGuestID = resultSet.getInt("LastGuestID");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error in retrieving last guest ID: " + e.getMessage());
        }
        return lastGuestID + 1;
    }

}

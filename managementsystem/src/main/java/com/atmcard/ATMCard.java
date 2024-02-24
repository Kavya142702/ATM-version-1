package com.atmcard;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.atmexceptions.ATMExceptions;
import com.atmregex.ATMRegex;
import com.users.OTPManager;
import com.atmdatabasemanagement.DatabaseConnector;

public class ATMCard {
    private String debitCard_No;
    private int pin;
    private Date expirey_Date;

    public ATMCard(String debitCard_No, int pin, Date expirey_Date) {
        super();
        this.debitCard_No = debitCard_No;
        this.pin = pin;
        this.expirey_Date = expirey_Date;
    }

    public ATMCard() {

    }

    public String getDebitCard_No() {
        return debitCard_No;
    }

    public int getPin() {
        return pin;
    }

    public Date getExpirey_Date() {
        return expirey_Date;
    }

    private static DatabaseConnector db = new DatabaseConnector();

    public void changeDebitCardPin(Scanner sc, String debitCardNo) throws ATMExceptions, FileNotFoundException {
        try (Connection conn = db.getDBConnection()) {
            System.out.println("Enter your current PIN: ");
            String currentPin = sc.nextLine();
            String otp = OTPManager.generateOTP();
            try {
                OTPManager.saveOTPToFile(otp); 
            } catch (IOException e) {
                throw new ATMExceptions("Error saving OTP to file: " + e.getMessage());
            }
            System.out.println("OTP send to your Mobile Number. Kindly check it..");
            System.out.println("Enter OTP received on your mobile: ");
            String enteredOTP = sc.nextLine();
            if (!OTPManager.verifyOTP(enteredOTP)) {
                System.out.println("OTP verification failed. Please try again.");
                return;
            }

            System.out.println("Enter your new PIN: ");
            String newPin = sc.nextLine();
            validateInput(newPin, ATMRegex.getPinPattern(), "Invalid PIN format. Please enter a 4-digit PIN.");
            
            System.out.println("Confirm your new PIN: ");
            String confirmedPin = sc.nextLine();
            if (!newPin.equals(confirmedPin)) {
                System.out.println("PIN verification failed. Please try again.");
                return;
            }
            String updateQuery = "UPDATE KAVYAP.ATM_Card SET PIN = ? WHERE debitCard_No = ? AND PIN = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, newPin);
                updateStmt.setString(2, debitCardNo);
                updateStmt.setString(3, currentPin);
                int updatedRows = updateStmt.executeUpdate();
                if (updatedRows > 0) {
                    System.out.println("PIN updated successfully!");
                } else {
                    throw new ATMExceptions("Failed to update PIN. Please verify your current PIN.");
                }
            }
        } catch (SQLException e) {
            throw new ATMExceptions("Database error occurred: " + e.getMessage());
        }
    }

    @SuppressWarnings("static-access")
	public static void insertATMCardDetails(int id, String accountNo) throws ClassNotFoundException, ATMExceptions {
        Scanner sc = new Scanner(System.in);
        String debitCardno = generateDebitCardNumber();
        LocalDate expiryDate = LocalDate.now().plusYears(5);
        int pin = 0;
        try (Connection conn = db.getDBConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO KAVYAP.ATM_Card (debitCard_No, Customer_ID, Account_NO, Expiry_Date, PIN, Bank_ID) " +
                "VALUES (?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, debitCardno);
            ps.setInt(2, id);
            ps.setString(3, accountNo);
            ps.setDate(4, java.sql.Date.valueOf(expiryDate));
            ps.setInt(5, pin);
            ps.setInt(6, 1);
            int updatedRows = ps.executeUpdate();
            if (updatedRows > 0) {
                System.out.println("Data inserted into ATM_Card table successfully!");
            } else {
                System.out.println("Failed to insert data into ATM_Card table.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String generateDebitCardNumber() {
        StringBuilder debitCardNumber = new StringBuilder();
        Random random = new Random();
        debitCardNumber.append(6);
        for (int i = 1; i < 16; i++) {
            debitCardNumber.append(random.nextInt(10));
        }
        return debitCardNumber.toString();
    }

    public static void validateInput(String input, Pattern pattern, String errorMessage) throws ATMExceptions {
        if (!pattern.matcher(input).matches()) {
            throw new ATMExceptions(errorMessage);
        }
    }
}

package com.atmTransactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InputMismatchException;

import com.atmexceptions.ATMExceptions;
import com.atmexceptions.DatabaseConnectionException;
import com.atmexceptions.InvalidCredentialsException;
import com.atmcard.*;
import com.atmdatabasemanagement.*;
import com.atmmachine.*;
import com.atmregex.ATMRegex;
import com.users.*;

public class TransactionOptions {

    static int atm_Id;
    static BankEmployee employee = new BankEmployee();
    static BankStaff bs = new BankStaff();
    Scanner sc = new Scanner(System.in);

    public static void displayMenu(Scanner sc) throws InterruptedException, SQLException, ClassNotFoundException, ATMExceptions, InvalidCredentialsException, DatabaseConnectionException, FileNotFoundException {
                System.out.println("********************************************");
                System.out.println("*       WELCOME TO THE ATM SERVICE         *");
                System.out.println("********************************************");
                Thread.sleep(3000);
                users(sc);
    }
    public static void users(Scanner sc) {
        int choiceOne = 0;
        boolean invalidInput = false; // Flag to track invalid input
        do {
            System.out.println("********************************************");
            System.out.println("*             CHOOSE THE USER              *");
            System.out.println("********************************************");
            System.out.println("*    1.Guest(Request to create Account)    *");
            System.out.println("*    2.Customer                            *");
            System.out.println("*    3.Bank Employees                      *");
            System.out.println("*    4.Exit                                *");
            System.out.println("********************************************");

            try {
                System.out.println("Enter your choice: ");
                choiceOne = sc.nextInt();
                sc.nextLine();
                invalidInput = false; // Reset invalidInput flag

                switch (choiceOne) {
                    case 1:
                        Guest guest = new Guest();
                        guest.guestDetails(sc);
                        break;
                    case 2:
                        customers(sc);
                        break;
                    case 3:
                        adminOptions(sc);
                        break;
                    case 4:
                        exitMessage();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                sc.nextLine(); // Clear the invalid input
                invalidInput = true; // Set invalidInput flag
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                invalidInput = true; // Set invalidInput flag
            }
        } while (invalidInput || choiceOne != 4); // Repeat loop if there was an invalid input or until the user chooses to exit
    }


        
    public static void customers(Scanner sc) throws ClassNotFoundException, FileNotFoundException, SQLException, ATMExceptions, DatabaseConnectionException, InvalidCredentialsException, InterruptedException {
        System.out.println("***************************************************");
        System.out.println("*     WELCOME TO THE STATE BANK OF INDIA  ATM     *");
        System.out.println("***************************************************");
        Thread.sleep(1000);
        System.out.println();
        System.out.println(" -----> Please insert your Card to begin the services <----- ");
        System.out.println();
        
        int response = 2;
        boolean invalidInput;
        do {
            invalidInput = false;
            printLobbyMessage(sc);

            try {
                System.out.println("Would you like to continue? ");
                response = sc.nextInt();
                sc.nextLine();
                Thread.sleep(500);
                if (response == 1) {
                    System.out.println("Thank you for confirming...You can proceed with your Transaction.");
                    customerOptions(sc);
                    Thread.sleep(500);
                } else if (response == 0) {
                    System.out.println("Please wait until the lobby is empty.");
                    printLobbyMessage(sc);
                } else {
                    System.out.println("Invalid input. Please enter '1' or '0'.");
                    invalidInput = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter '1' or '0'.");
                sc.nextLine(); // Consume the invalid input
                invalidInput = true;
            }
            System.out.println();
        } while (invalidInput || (response != 0 && response != 1)); 
    }



    public static void printLobbyMessage(Scanner sc) {
        System.out.println("*************************************************************************************");
        System.out.println("*  Please kindly ensure that no other person is present in the lobby with you.      *");
        System.out.println("*                  Are you currently alone in the lobby? (Yes - 1/ No - 0)          *");
        System.out.println("*************************************************************************************");
    }

    public static void customerOptions(Scanner sc) throws SQLException, ClassNotFoundException, ATMExceptions, DatabaseConnectionException, InvalidCredentialsException, FileNotFoundException {
        System.out.println("********************************************");
        System.out.println("*             Customer Options             *");
        System.out.println("********************************************");
        System.out.println("*      1. Green pin / forgot pin           *");
        System.out.println("*      2. Login                            *");
        System.out.println("*      3. Go back to main menu             *");
        System.out.println("*      4. Exit                             *");
        System.out.println("********************************************");

        System.out.println("Enter your Choice : ");
        int customerChoice = sc.nextInt();
        sc.nextLine();

        switch (customerChoice) {
            case 1:
                greenPinGenerator(sc);
                break;
            case 2:
                try {
                    authenticateUserPin(sc, atm_Id);
                } catch (InputMismatchException e) {
                    sc.nextLine(); 
                    System.out.println("Invalid input. Please enter numeric values for the debit card number and PIN.");
                } catch (Exception e) {
                    System.out.println("An error occurred: " + e.getMessage());
                }
                break;
            case 3:
                return;
            case 4:
                exitMessage();
                System.exit(0);
                break;
            default:
                defaultMessage();
        }
    }

    public static void adminOptions(Scanner sc) throws ClassNotFoundException, ATMExceptions, InvalidCredentialsException, DatabaseConnectionException {
        int adminChoice;
        do {
            System.out.println("********************************************");
            System.out.println("*          Bank Employee Options           *");
            System.out.println("********************************************");
            System.out.println("*       1. login                           *");
            System.out.println("*       2. Go back to main menu            *");
            System.out.println("*       3. Exit                            *");
            System.out.println("********************************************");

            System.out.println("Enter your Choice: ");
            adminChoice = sc.nextInt();
            sc.nextLine();

            switch (adminChoice) {
                case 1:
                    try {
                        employee.employeeLogin(sc);
                    } catch (InvalidCredentialsException e) {
                        System.out.println("Invalid username or password. Please try again with correct username and password.");
                    }
                    break;
                case 2:
                	users(sc);
                case 3:
                    exitMessage();
                    System.exit(0);
                    break;
                default:
                    defaultMessage();
                    continue;
            }
        } while (adminChoice != 3); // Keep looping until the user chooses to exit
    }


    @SuppressWarnings("static-access")
	private static void basicOperations(Scanner sc, String debitCard_No, int atm_Id) throws ClassNotFoundException, ATMExceptions, DatabaseConnectionException, FileNotFoundException {
        ATM atm = new ATM();
        Transaction transaction = new Transaction();
        int option;
        do {
            System.out.println("******************************************************************************");
            System.out.println("*  1. Cash Withdrawal              |   2.Deposit                             *");
            System.out.println("******************************************************************************");

            System.out.println("******************************************************************************");
            System.out.println("*  3. Balance Inquiry              |   4. Fund Transfer                      *");
            System.out.println("******************************************************************************");

            System.out.println("******************************************************************************");
            System.out.println("*  5. View Transaction Receipts    |   6. Account Managemnt                  *");
            System.out.println("******************************************************************************");

            System.out.println("******************************************************************************");
            System.out.println("*  7. Back                         |    8. Exit                              *");
            System.out.println("******************************************************************************");

            System.out.print("Enter your option: ");

            option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    atm.performCashWithdrawal(sc, debitCard_No, atm_Id);
                    break;
                case 2:
                    atm.performCashDeposit(sc, debitCard_No, atm_Id);
                    break;
                case 3:
                    atm.performBalanceInquiry(debitCard_No);
                    break;
                case 4:
                    atm.performFundTransfer(sc, debitCard_No);
                    break;
                case 5:
                    atm.getMiniStatement(debitCard_No);
                    break;
                case 6:
                    accountManagement(sc, debitCard_No);
                    break;
                case 7:
                    return;
                case 8:
                    exitMessage();
                    System.exit(0);
                    break;
                default:
                    defaultMessage();
            }
        } while (option != 8);
    }
    
    public static void accountManagement(Scanner sc, String debitCard_No) throws ClassNotFoundException, ATMExceptions, DatabaseConnectionException, FileNotFoundException {
        ATMCard card = new ATMCard();

        int serviceOption;
        do {
            System.out.println("***************************************************");
            System.out.println("*                Account Management               *");
            System.out.println("***************************************************");
            System.out.println("* 1. Pin Change          |  2. Profile Updation   *");
            System.out.println("* 3. Back                |  4. Exit               *");
            System.out.println("***************************************************");
            System.out.print("Enter your option: ");
            serviceOption = sc.nextInt();
            sc.nextLine();

            switch (serviceOption) {
                case 1:
                    card.changeDebitCardPin(sc, debitCard_No);
                    break;
                case 2:
                    profileManagement(sc, debitCard_No);
                    break;
                case 3:
                    return;
                case 5:
                    exitMessage();
                    System.exit(0);
                    break;
                default:
                    defaultMessage();
            }
        } while (true);
    }

    public static void profileManagement(Scanner sc, String debitCard_No) throws ClassNotFoundException, ATMExceptions, DatabaseConnectionException {
        Customer customer = new Customer();
        int profileOption;
        do {
            System.out.println("***************************************************");
            System.out.println("*          Profile Management Services            *");
            System.out.println("***************************************************");
            System.out.println("*           1. Change Address                     *");
            System.out.println("*           2. Change Mobile Number               *");
            System.out.println("*           3. Change Email                       *");
            System.out.println("*           4. Back                               *");
            System.out.println("*           5. Exit                               *");
            System.out.println("***************************************************");
            System.out.print("Enter your option: ");
            profileOption = sc.nextInt();
            sc.nextLine();

            switch (profileOption) {
                case 1:
                    customer.changeAddress(sc, debitCard_No);
                    break;
                case 2:
                    customer.changeMobileNumber(sc, debitCard_No);
                    break;
                case 3:
                    customer.changeEmail(sc, debitCard_No);
                    break;
                case 4:
                    return;
                case 5:
                    exitMessage();
                    System.exit(0);
                default:
                    defaultMessage();
            }
        } while (true);
    }

    static DatabaseConnector db = new DatabaseConnector();
    @SuppressWarnings("static-access")
    private static void greenPinGenerator(Scanner sc) throws SQLException, ClassNotFoundException, DatabaseConnectionException, ATMExceptions, FileNotFoundException {
        boolean pinVerified = false;
        String enteredPin = null;
        String debitCard_No = null; 
        ATMRegex atmRegex = new ATMRegex(); 
        String debitCardPattern = ATMRegex.getDebitCardPattern().pattern(); 
        String validatedMobileNumber = null;

        while (!pinVerified) {
            if (debitCard_No == null) {
                System.out.println("Enter debit card number: ");
                debitCard_No = sc.nextLine();
                String validatedDebitCardNumber = atmRegex.validateInput(debitCard_No, "Debit Card Number", debitCardPattern);
                System.out.println("Validated Debit Card Number: " + validatedDebitCardNumber);
            }
            if (validatedMobileNumber == null) {
                System.out.println("Enter mobile number: ");
                String mobileNumber = sc.nextLine();
                String mobilePattern = ATMRegex.getMobilePattern();
                validatedMobileNumber = atmRegex.validateInput(mobileNumber, "Mobile Number", mobilePattern);
                System.out.println("Validated Mobile Number: " + validatedMobileNumber);
            }
            if (enteredPin == null || enteredPin.isEmpty()) {
                String otp;
                try {
                    otp = OTPManager.generateOTP();
                    OTPManager.saveOTPToFile(otp);
                    System.out.println("OTP is sent to your mobile number. Kindly Check it...");
                } catch (IOException e) {
                    System.out.println("Error writing OTP to file: " + e.getMessage());
                    return;
                }
                System.out.println("Enter OTP received on your mobile: ");
                String enteredOTP = sc.nextLine();
                if (!OTPManager.verifyOTP(enteredOTP)) {
                    System.out.println("OTP verification failed. Please try again.");
                    continue;
                }
                System.out.println("Enter Your Green Pin (4 digits): ");
                enteredPin = sc.nextLine();
                System.out.println("Your Green pin is : " + enteredPin);
            }
            if (enteredPin != null && !enteredPin.isEmpty()) {
                System.out.println("1. Confirm");
                System.out.println("2. Back to change");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        if (!validatePin(enteredPin)) {
                            System.out.println("Invalid PIN format. Please enter a 4-digit PIN.");
                            continue;
                        }
                        pinVerified = true;
                        break;
                    case 2:
                        enteredPin = ""; 
                        continue;
                    default:
                        System.out.println("Invalid choice. Please enter either 1 or 2.");
                        break;
                }
            }
            if (pinVerified) {
                try (Connection conn = db.getDBConnection()) {
                    String customerIdQuery = "SELECT Customer_ID FROM KAVYAP.ATM_Card WHERE debitCard_No = ?";
                    PreparedStatement customerIdStmt = conn.prepareStatement(customerIdQuery);
                    customerIdStmt.setString(1, debitCard_No);
                    ResultSet customerIdResult = customerIdStmt.executeQuery();
                    if (customerIdResult.next()) {
                        int customerId = customerIdResult.getInt("Customer_ID");
                        String sql = "UPDATE KAVYAP.ATM_Card SET PIN = ? WHERE Customer_ID = ?";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setInt(1, Integer.parseInt(enteredPin));
                        pstmt.setInt(2, customerId);
                        pstmt.executeUpdate();
                        System.out.println("Green PIN updated successfully!");
                    } else {
                        System.out.println("No customer found with the provided mobile number.");
                    }
                } catch (SQLException e) {
                    throw new DatabaseConnectionException("Error connecting to the database.");
                }
            }
        }
    }

    private static boolean validatePin(String pin) {
        Matcher matcher = ATMRegex.getPinPattern().matcher(pin);
        return matcher.matches();
    }

    @SuppressWarnings("static-access")
    public static void authenticateUserPin(Scanner sc, int atm_Id) throws InvalidCredentialsException, ATMExceptions, ClassNotFoundException, DatabaseConnectionException, FileNotFoundException {
        String debitCard_No;
        int pin;
        int attemptsRemaining = 3; 
        while (true) {
            try {
                System.out.println("Enter Your 16 digit debitCard_No: ");
                debitCard_No = sc.nextLine();

                if (debitCard_No.length() != 16 || !debitCard_No.matches("\\d+")) {
                    System.out.println("Invalid debit card number format. Please enter a 16-digit numeric card number.");
                    continue; 
                }
                try (Connection connection = db.getDBConnection()) {
                    String sql = "SELECT * FROM KAVYAP.ATM_Card AC " +
                            "INNER JOIN KAVYAP.Account A ON AC.ACCOUNT_NO = A.ACCOUNT_NO " +
                            "WHERE AC.DEBITCARD_NO = ? AND A.ACCOUNT_STATUS = 'Active'";
                    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                        pstmt.setString(1, debitCard_No);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            if (rs.next()) {
                                System.out.println("Debit card number verified.");
                                break; 
                            } else {
                                attemptsRemaining--; // Decrement attempts 
                                if (attemptsRemaining == 0) {
                                    // Change account status to inactive
                                    updateAccountStatusToInactive(connection, debitCard_No);
                                    throw new InvalidCredentialsException("Invalid debit card number or the card is inactive. You have exceeded the maximum number of attempts. Your account has been deactivated.");
                                }
                                System.out.println("Invalid debit card number or the card is inactive. Please check your details and try again. Attempts remaining: " + attemptsRemaining);
                            }
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("An error occurred while processing your request: " + e.getMessage());
                    System.out.println("Please try again later.");
                    throw new DatabaseConnectionException("An error occurred while processing your request: " + e.getMessage());
                }
            } catch (InvalidCredentialsException e) {
                System.out.println(e.getMessage());
                System.out.println("Please try again.");
            }
        }
        attemptsRemaining = 3; // Reset attempts remaining for PIN entry
        while (attemptsRemaining > 0) {
            try {
                System.out.println("Enter Your 4 digit PIN: ");
                String pinStr = sc.nextLine();

                if (pinStr.length() != 4 || !pinStr.matches("\\d+")) {
                    System.out.println("Invalid PIN format. Please enter exactly 4 digits.");
                    continue;
                }
                pin = Integer.parseInt(pinStr);
                try (Connection conn = db.getDBConnection()) {
                    String sql = "SELECT * FROM KAVYAP.ATM_Card AC " +
                            "INNER JOIN KAVYAP.Account A ON AC.ACCOUNT_NO = A.ACCOUNT_NO " +
                            "WHERE AC.DEBITCARD_NO = ? AND AC.PIN = ? AND A.ACCOUNT_STATUS = 'Active'";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, debitCard_No);
                        pstmt.setInt(2, pin);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            if (rs.next()) {
                                System.out.println("Login Successful! You may now proceed with your transaction.");
                                basicOperations(sc, debitCard_No, atm_Id);
                                return;
                            } else {
                                attemptsRemaining--; // Decrement attempts
                                if (attemptsRemaining == 0) {
                                    updateAccountStatusToInactive(conn, debitCard_No);
                                    throw new InvalidCredentialsException("Invalid PIN. You have exceeded the maximum number of attempts.\nSo your account is temporarily blocked. Please contact your bank.");
                                }
                                System.out.println("Invalid PIN. Please check your details and try again. Attempts remaining: " + attemptsRemaining);
                            }
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("An error occurred while processing your request: " + e.getMessage());
                    System.out.println("Please try again later.");
                    throw new DatabaseConnectionException("An error occurred while processing your request: " + e.getMessage());
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid PIN format. Please enter numeric values only.");
            } catch (InvalidCredentialsException e) {
                System.out.println(e.getMessage());
                System.out.println("Please try again.");
            }
        }
        System.out.println("You have exceeded the maximum number of attempts. Please try again later.");
    }

    private static void updateAccountStatusToInactive(Connection connection, String debitCard_No) throws SQLException {
        String sql = "UPDATE KAVYAP.Account SET ACCOUNT_STATUS = 'In active' WHERE ACCOUNT_NO = (SELECT ACCOUNT_NO FROM KAVYAP.ATM_CARD WHERE DEBITCARD_NO =?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, debitCard_No);
            pstmt.executeUpdate();
        }
    }

    public static void exitMessage() {
        System.out.println("****************************************************");
        System.out.println("*                                                  *");
        System.out.println("*    Thank you for using our SBI ATM               *");
        System.out.println("*                                                  *");
        System.out.println("*                       Have a nice day!           *");
        System.out.println("*                                                  *");
        System.out.println("****************************************************");
    }

    public static void defaultMessage() {
        System.out.println("We apologize, but it seems you have selected an invalid option. Please choose a valid option.");
    }
}

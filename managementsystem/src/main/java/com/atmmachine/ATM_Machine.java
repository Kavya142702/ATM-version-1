package com.atmmachine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.atmdatabasemanagement.*;
import com.atmexceptions.ATMExceptions;

public class ATM_Machine {
    private String atm_ID;
    private String location;
    private String bank_ID;
    private double Money;

    public ATM_Machine() {
        // Default constructor
    }

    public ATM_Machine(String atm_ID, String location, String bank_ID, double money) {
        this.atm_ID = atm_ID;
        this.location = location;
        this.bank_ID = bank_ID;
        this.Money = money;
    }

    public String getAtm_ID() {
        return atm_ID;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBank_ID() {
        return bank_ID;
    }

    public double getMoney() {
        return Money;
    }

    @Override
    public String toString() {
        return "ATM_Machine [atm_ID=" + atm_ID + ", location=" + location + ", bank_ID=" + bank_ID + ", Money=" + Money
                + "]";
    }

    static DatabaseConnector db = new DatabaseConnector();
    private static final double THRESHOLD_LIMIT = 5000.0;

    public static void viewATMBalance(Scanner sc, int atm_Id) throws ATMExceptions, ClassNotFoundException {
        sc.nextLine();
        String query = "SELECT Money FROM KAVYAP.ATM WHERE ATM_ID = ?";
        try (Connection conn = db.getDBConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setInt(1, atm_Id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        double balance = resultSet.getDouble("Money");
                        System.out.println("Avilable Money in the ATM is : " + balance + "INR.");
                    } else      throw new ATMExceptions("Your searching ATM is not found.");
                }
            }
        } catch (SQLException e) {
            throw new ATMExceptions("Database error occurred: " + e.getMessage());
        }
    }

    public void updateATMBalance(Connection connection, double amount, boolean isDeposit, int atm_Id) throws ATMExceptions {
        String updateQuery = isDeposit ? "UPDATE KAVYAP.ATM SET Money = Money + ? WHERE ATM_ID = ?" :
                "UPDATE KAVYAP.ATM SET Money = Money - ? WHERE ATM_ID = ? AND Money >= ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setDouble(1, amount);
            preparedStatement.setInt(2, atm_Id);
            if (!isDeposit) {
                preparedStatement.setDouble(3, amount);
            }
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0 && !isDeposit) {
                double updatedBalance = getATMBalance(connection, atm_Id);
                if (updatedBalance < THRESHOLD_LIMIT) {
                    notifyMaintainer(atm_Id, updatedBalance);
                    addMoneyToATM(atm_Id);
                }
            }
        } catch (SQLException e) {
            throw new ATMExceptions("Database error occurred: " + e.getMessage());
        }
    }

    public static void addMoneyToATM(int atm_Id) throws ATMExceptions {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the money you want to add: ");
        double amount = sc.nextDouble();
        
        DatabaseConnector db = new DatabaseConnector();
        try (Connection connection = db.getDBConnection()) {
            String addMoneyQuery = "UPDATE KAVYAP.ATM SET Money = Money + ? WHERE ATM_ID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(addMoneyQuery)) {
                preparedStatement.setDouble(1, amount);
                preparedStatement.setInt(2, atm_Id);
                preparedStatement.executeUpdate();
                System.out.println("Added " + amount + " to ATM " + atm_Id);
            }
        } catch (SQLException e) {
            throw new ATMExceptions("Database error occurred: " + e.getMessage());
        }
    }

    private static double getATMBalance(Connection connection, int atm_Id) throws ATMExceptions {
        String balanceQuery = "SELECT Money FROM KAVYAP.ATM WHERE ATM_ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(balanceQuery)) {
            preparedStatement.setInt(1, atm_Id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("Money");
                } else        throw new ATMExceptions("ATM not found.");
            }
        } catch (SQLException e) {
            throw new ATMExceptions("Database error occurred: " + e.getMessage());
        }
    }

    private static void notifyMaintainer(int atm_Id, double balance) {
        System.out.println("ATM " + atm_Id + " balance is below threshold. Current balance: " + balance);
        System.out.println("Maintainer notified for cash replenishment.");
    }
}

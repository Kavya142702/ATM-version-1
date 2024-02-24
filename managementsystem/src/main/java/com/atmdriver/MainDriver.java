package com.atmdriver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.atmregex.ATMRegex;

import com.users.BankStaff;
import com.atmTransactions.TransactionOptions;
import com.atmdatabasemanagement.DatabaseConnector;
import com.atmexceptions.ATMExceptions;
import com.atmexceptions.DatabaseConnectionException;
import com.atmexceptions.InvalidCredentialsException;

public class MainDriver {
    @SuppressWarnings("static-access")
	public static void main(String[] args) throws InterruptedException, SQLException, ClassNotFoundException, InvalidCredentialsException, DatabaseConnectionException, FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        TransactionOptions transactionOptions = new TransactionOptions();
        BankStaff bs = new BankStaff();
        DatabaseConnector db = new DatabaseConnector();
        ATMRegex regex = new ATMRegex();
        try {
            transactionOptions.displayMenu(sc);
        } catch (ATMExceptions e) {
            System.out.println("ATM exception occurred: " + e.getMessage());
        }
    }
}
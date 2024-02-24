package com.users;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class OTPManager {
	 public static String readOTPFromFile() throws FileNotFoundException {
         File file = new File("otp.txt");
         Scanner scanner = new Scanner(file);
         String otp = scanner.nextLine();
         return otp;
     }
	 
    public static String generateOTP() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otp.append((int) (Math.random() * 10));
        }
        return otp.toString();
    }

    public static void saveOTPToFile(String otp) throws IOException {
        try (FileWriter writer = new FileWriter("otp.txt")) {
            writer.write(otp);
        }
    }
    public static boolean verifyOTP(String enteredOTP) throws FileNotFoundException {
        String savedOTP = readOTPFromFile();
        return savedOTP.equals(enteredOTP);
    }

}


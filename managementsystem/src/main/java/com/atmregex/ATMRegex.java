package com.atmregex;

import java.util.Scanner;
import java.util.regex.Pattern;

import com.atmexceptions.ATMExceptions;

public class ATMRegex {
	private static final String EMPLOYEE_ID_PATTERN = "\\d+";
    private static final String PASSWORD_PATTERN = "^(?=.[a-z])(?=.[A-Z])(?=.\\d)(?=.[@$!%?&])[A-Za-z\\d@$!%?&]{8,}$";
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
    private static final String MOBILE_PATTERN = "(0/91)?[6-9][0-9]{9}";
    private static final String AADHAR_PATTERN = "\\d{12}";
    private static final String PAN_PATTERN = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
	private static final Pattern PIN_PATTERN = Pattern.compile("\\d{4}");
	private static final Pattern OTP_PATTERN = Pattern.compile("\\d{6}");
	private static final Pattern DEBIT_CARD_PATTERN = Pattern.compile("\\d{16}");
	 
	
	public static String getMobilePattern() {
		return MOBILE_PATTERN;
	}
	public static String getEmailPattern() {
		return EMAIL_PATTERN;
	}
	public static String getAadharPattern() {
		return AADHAR_PATTERN;
	}
	public static String getPanPattern() {
		return PAN_PATTERN;
	}
	public static String getEmployeeIdPattern() {
		return EMPLOYEE_ID_PATTERN;
	}
	public static String getPasswordPattern() {
		return PASSWORD_PATTERN;
	}
	public static Pattern getDebitCardPattern() {
		return DEBIT_CARD_PATTERN;
	}
	public static Pattern getPinPattern() {
		return PIN_PATTERN;
	}
	public static boolean validateEmployeeID(String input) {
        return input.matches(EMPLOYEE_ID_PATTERN);
    }

    public static boolean validatePassword(String input) {
        return input.matches(PASSWORD_PATTERN);
    }

    public static boolean validateEmail(String input) {
        return input.matches(EMAIL_PATTERN);
    }

    public static boolean validateMobileNumber(String input) {
        return input.matches(MOBILE_PATTERN);
    }

    public static boolean validateAadharNumber(String input) {
        return input.matches(AADHAR_PATTERN);
    }

    public static boolean validatePANNumber(String input) {
        return input.matches(PAN_PATTERN);
    }
    
	public static String validateInput(String input, String fieldName, String pattern) {
        Scanner sc = new Scanner(System.in);
        while (!input.matches(pattern)) {
            System.out.println("Invalid input for " + fieldName + ".");
            System.out.println("Please enter a valid " +fieldName+ ": ");
            input = sc.nextLine();
        }
        return input;
    }
	public void validateInput(String input, ValidationRule rule, String errorMessage) throws ATMExceptions {
        if (!rule.validate(input)) {
            throw new ATMExceptions(errorMessage);
        }
    }
	
	@FunctionalInterface
    public interface ValidationRule {
        boolean validate(String input);
    }
	
	public void validateInput(String updatedName, Object fieldName, String pattern) {
		// TODO Auto-generated method stub
		
	}
}

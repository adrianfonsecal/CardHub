package com.example.cardhubapp.validator;
public class PasswordValidator {
    private static final int MIN_PASSWORD_LENGTH = 6;

    public static boolean isValidPassword(String password) {
        return password.length() >= MIN_PASSWORD_LENGTH && containsNumber(password);
    }

    private static boolean containsNumber(String password) {
        for (char passwordCharacter : password.toCharArray()) {
            if (Character.isDigit(passwordCharacter)) {
                return true;
            }
        }
        return false;
    }
}

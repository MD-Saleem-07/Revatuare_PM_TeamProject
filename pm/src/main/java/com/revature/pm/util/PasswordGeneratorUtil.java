package com.revature.pm.util;

import java.security.SecureRandom;

import com.revature.pm.exception.InvalidOperationException;

public class PasswordGeneratorUtil {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+<>?";

    private static final String SIMILAR = "0O1lI";

    public static String generatePassword(int length,boolean includeUpper,boolean includeLower,boolean includeNumbers,boolean includeSpecial,boolean excludeSimilar) {

        if (length < 8 || length > 64) {
        	throw new InvalidOperationException("Password length must be between 8 and 64");
        }

        StringBuilder characterPool = new StringBuilder();

        if (includeUpper) characterPool.append(UPPER);
        if (includeLower) characterPool.append(LOWER);
        if (includeNumbers) characterPool.append(NUMBERS);
        if (includeSpecial) characterPool.append(SPECIAL);

        if (characterPool.length() == 0) {
        	throw new InvalidOperationException("At least one character type must be selected");
        }

        String pool = characterPool.toString();

        if (excludeSimilar) {
            for (char c : SIMILAR.toCharArray()) {
                pool = pool.replace(String.valueOf(c), "");
            }
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(pool.length());
            password.append(pool.charAt(index));
        }

        return password.toString();
    }
    
    public static String checkStrength(String password) {

        int score = 0;

        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;

        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()\\-_=+<>?].*")) score++;

        if (score <= 2) return "Weak";
        if (score <= 4) return "Medium";
        if (score <= 5) return "Strong";
        return "Very Strong";
    }

}

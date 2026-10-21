package com.shopsphere.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[0-9]{10}$");
    private static final Pattern PINCODE_PATTERN = Pattern.compile("^[0-9]{6}$");
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidMobile(String mobile) {
        if (mobile == null) return true; // Optional
        String clean = mobile.replaceAll("[^0-9]", "");
        return clean.length() == 10;
    }

    public static boolean isValidPincode(String pincode) {
        if (pincode == null) return false;
        return PINCODE_PATTERN.matcher(pincode.trim()).matches();
    }

    public static boolean isStrongPassword(String password) {
        if (password == null) return false;
        return password.length() >= 6;
    }

    public static String generateToken() {
        byte[] randomBytes = new byte[24];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public static String sanitize(String input) {
        if (input == null) return "";
        return input.trim();
    }
}

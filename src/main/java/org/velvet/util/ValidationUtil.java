package org.velvet.util;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

public final class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+\\-]{7,15}$");

    private ValidationUtil() {
    }

    public static void requireNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }

    public static void requireNoPipe(String value, String fieldName) {
        if (value != null && value.contains("|")) {
            throw new IllegalArgumentException(fieldName + " must not contain '|'.");
        }
    }

    public static void validateEmail(String email) {
        requireNotBlank(email, "Email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email format is invalid.");
        }
    }

    public static void validatePhone(String phone) {
        requireNotBlank(phone, "Phone");
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("Phone format is invalid.");
        }
    }

    public static void validatePassword(String password) {
        requireNotBlank(password, "Password");
        if (password.trim().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
    }

    public static void validateDateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !end.isAfter(start)) {
            throw new IllegalArgumentException("Invalid date range.");
        }
    }
}

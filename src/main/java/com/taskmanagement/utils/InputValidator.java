package com.taskmanagement.utils;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Utility class for validating user input and data integrity
 */
public class InputValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$");

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[A-Za-z0-9_-]{3,20}$");

    private static final Pattern COLOR_PATTERN =
            Pattern.compile("^#[0-9A-Fa-f]{6}$");

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return password.length() >= 8;
    }

    public static boolean isValidColor(String color) {
        if (color == null) {
            return false;
        }
        return COLOR_PATTERN.matcher(color).matches();
    }

    public static boolean isFutureOrToday(LocalDate date) {
        if (date == null) {
            return true;
        }
        return !date.isBefore(LocalDate.now());
    }
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isLengthValid(String str, int minLength, int maxLength) {
        if (str == null) {
            return false;
        }
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }

    public static String validateTaskTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title is required");
        }
        if (!isLengthValid(title, 3, 255)) {
            throw new IllegalArgumentException("Task title must be between 3 and 255 characters");
        }
        return title.trim();
    }

    public static String validateProjectName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name is required");
        }
        if (!isLengthValid(name, 3, 100)) {
            throw new IllegalArgumentException("Project name must be between 3 and 100 characters");
        }
        return name.trim();
    }
    public static String validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            return null;
        }
        if (!isLengthValid(description, 0, 5000)) {
            throw new IllegalArgumentException("Description must not exceed 5000 characters");
        }
        return description.trim();
    }

    public static String validateTeamName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Team name is required");
        }
        if (!isLengthValid(name, 3, 100)) {
            throw new IllegalArgumentException("Team name must be between 3 and 100 characters");
        }
        return name.trim();
    }

    public static String validateComment(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }
        if (!isLengthValid(message, 1, 5000)) {
            throw new IllegalArgumentException("Comment must not exceed 5000 characters");
        }
        return message.trim();
    }

    public static void validatePriority(String priority) {
        if (priority == null) {
            return;
        }
        String[] validPriorities = {"Low", "Medium", "High", "Urgent"};
        boolean valid = false;
        for (String p : validPriorities) {
            if (p.equalsIgnoreCase(priority)) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            throw new IllegalArgumentException("Invalid priority: " + priority);
        }
    }

    public static void validateStatus(String status) {
        if (status == null) {
            return;
        }
        String[] validStatuses = {"To Do", "In Progress", "In Review", "Done"};
        boolean valid = false;
        for (String s : validStatuses) {
            if (s.equalsIgnoreCase(status)) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input.trim()
                .replaceAll("<script[^>]*>.*?</script>", "")
                .replaceAll("<[^>]*>", "");
    }
}

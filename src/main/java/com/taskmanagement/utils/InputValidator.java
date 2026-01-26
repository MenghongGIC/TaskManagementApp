package com.taskmanagement.utils;

import java.time.LocalDate;
import java.util.regex.Pattern;
public class InputValidator {

    // Validation Patterns
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$");
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[A-Za-z0-9_-]{3,20}$");
    private static final Pattern COLOR_PATTERN =
            Pattern.compile("^#[0-9A-Fa-f]{6}$");
    
    // Validation Constraints
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MIN_TITLE_LENGTH = 3;
    private static final int MAX_TITLE_LENGTH = 255;
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 5000;
    
    // Error Messages
    private static final String ERR_TASK_TITLE_REQUIRED = "Task title is required";
    private static final String ERR_TASK_TITLE_LENGTH = "Task title must be between 3 and 255 characters";
    private static final String ERR_PROJECT_NAME_REQUIRED = "Project name is required";
    private static final String ERR_PROJECT_NAME_LENGTH = "Project name must be between 3 and 100 characters";
    private static final String ERR_DESCRIPTION_TOO_LONG = "Description must not exceed 5000 characters";
    private static final String ERR_INVALID_PRIORITY = "Invalid priority value";
    private static final String ERR_INVALID_STATUS = "Invalid status value";

    private InputValidator() { }

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
        return password.length() >= MIN_PASSWORD_LENGTH;
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
            throw new IllegalArgumentException(ERR_TASK_TITLE_REQUIRED);
        }
        if (!isLengthValid(title, MIN_TITLE_LENGTH, MAX_TITLE_LENGTH)) {
            throw new IllegalArgumentException(ERR_TASK_TITLE_LENGTH);
        }
        return title.trim();
    }

    public static String validateProjectName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(ERR_PROJECT_NAME_REQUIRED);
        }
        if (!isLengthValid(name, MIN_NAME_LENGTH, MAX_NAME_LENGTH)) {
            throw new IllegalArgumentException(ERR_PROJECT_NAME_LENGTH);
        }
        return name.trim();
    }

    public static String validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            return null;
        }
        if (!isLengthValid(description, 0, MAX_DESCRIPTION_LENGTH)) {
            throw new IllegalArgumentException(ERR_DESCRIPTION_TOO_LONG);
        }
        return description.trim();
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
            throw new IllegalArgumentException(ERR_INVALID_PRIORITY + ": " + priority);
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
            throw new IllegalArgumentException(ERR_INVALID_STATUS + ": " + status);
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

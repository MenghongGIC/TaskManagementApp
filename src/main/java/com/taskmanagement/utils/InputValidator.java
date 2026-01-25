package com.taskmanagement.utils;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Utility class for validating user input and data integrity
 * Provides validation methods for common data types and constraints
 */
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

    private InputValidator() {
        // Utility class, no instantiation
    }

    /**
     * Validate email format
     * 
     * @param email the email to validate
     * @return true if valid email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate username format (3-20 characters, alphanumeric with dash/underscore)
     * 
     * @param username the username to validate
     * @return true if valid username format
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Validate password length (minimum 8 characters)
     * 
     * @param password the password to validate
     * @return true if password meets length requirement
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return password.length() >= MIN_PASSWORD_LENGTH;
    }

    /**
     * Validate hex color format
     * 
     * @param color the color to validate
     * @return true if valid hex color format
     */
    public static boolean isValidColor(String color) {
        if (color == null) {
            return false;
        }
        return COLOR_PATTERN.matcher(color).matches();
    }

    /**
     * Check if a date is today or in the future
     * 
     * @param date the date to check
     * @return true if date is today or later
     */
    public static boolean isFutureOrToday(LocalDate date) {
        if (date == null) {
            return true;
        }
        return !date.isBefore(LocalDate.now());
    }

    /**
     * Check if a string is not empty
     * 
     * @param str the string to check
     * @return true if string is not null and not empty after trimming
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Validate string length is within bounds
     * 
     * @param str the string to validate
     * @param minLength minimum length
     * @param maxLength maximum length
     * @return true if length is within bounds
     */
    public static boolean isLengthValid(String str, int minLength, int maxLength) {
        if (str == null) {
            return false;
        }
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Validate and normalize task title
     * 
     * @param title the task title to validate
     * @return the normalized title
     * @throws IllegalArgumentException if title is invalid
     */
    public static String validateTaskTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException(ERR_TASK_TITLE_REQUIRED);
        }
        if (!isLengthValid(title, MIN_TITLE_LENGTH, MAX_TITLE_LENGTH)) {
            throw new IllegalArgumentException(ERR_TASK_TITLE_LENGTH);
        }
        return title.trim();
    }

    /**
     * Validate and normalize project name
     * 
     * @param name the project name to validate
     * @return the normalized name
     * @throws IllegalArgumentException if name is invalid
     */
    public static String validateProjectName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(ERR_PROJECT_NAME_REQUIRED);
        }
        if (!isLengthValid(name, MIN_NAME_LENGTH, MAX_NAME_LENGTH)) {
            throw new IllegalArgumentException(ERR_PROJECT_NAME_LENGTH);
        }
        return name.trim();
    }

    /**
     * Validate and normalize description
     * 
     * @param description the description to validate
     * @return the normalized description, or null if null input
     * @throws IllegalArgumentException if description is too long
     */
    public static String validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            return null;
        }
        if (!isLengthValid(description, 0, MAX_DESCRIPTION_LENGTH)) {
            throw new IllegalArgumentException(ERR_DESCRIPTION_TOO_LONG);
        }
        return description.trim();
    }

    /**
     * Validate priority value
     * 
     * @param priority the priority to validate
     * @throws IllegalArgumentException if priority is not valid
     */
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

    /**
     * Validate status value
     * 
     * @param status the status to validate
     * @throws IllegalArgumentException if status is not valid
     */
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

    /**
     * Sanitize input by removing scripts and HTML tags
     * 
     * @param input the input to sanitize
     * @return the sanitized input, or null if input is null
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input.trim()
                .replaceAll("<script[^>]*>.*?</script>", "")
                .replaceAll("<[^>]*>", "");
    }
}

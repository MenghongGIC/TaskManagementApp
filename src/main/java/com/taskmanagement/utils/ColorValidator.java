package com.taskmanagement.utils;

public class ColorValidator {
    private static final String HEX_COLOR_PATTERN = "^#[0-9A-Fa-f]{6}$";
    
    // Default Colors
    private static final String DEFAULT_LABEL_COLOR = "#007BFF";
    private static final String DEFAULT_PROJECT_COLOR = "#6C757D";
    
    private ColorValidator() { } // Private constructor to prevent instantiation
    public static boolean isValidHexColor(String color) {
        return color != null && color.matches(HEX_COLOR_PATTERN);
    }
    public static String getLabelColorOrDefault(String color) {
        return isValidHexColor(color) ? color : DEFAULT_LABEL_COLOR;
    }
    public static String getProjectColorOrDefault(String color) {
        if (isValidHexColor(color)) {
            return color.toUpperCase();
        }
        return DEFAULT_PROJECT_COLOR;
    }
}

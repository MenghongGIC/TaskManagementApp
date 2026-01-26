package com.taskmanagement.utils;

import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

public class PriorityColorMapper {

    // Priority Constants
    private static final String PRIORITY_LOW = "Low";
    private static final String PRIORITY_MEDIUM = "Medium";
    private static final String PRIORITY_HIGH = "High";
    private static final String PRIORITY_URGENT = "Urgent";

    // Color Constants (Hex values)
    private static final String COLOR_LOW = "#28A745";      // Green
    private static final String COLOR_MEDIUM = "#FFC107";   // Amber
    private static final String COLOR_HIGH = "#FD7E14";     // Orange
    private static final String COLOR_URGENT = "#DC3545";   // Red
    private static final String COLOR_DEFAULT = "#6C757D";  // Gray

    // Priority Color Map
    private static final Map<String, String> PRIORITY_COLORS = createColorMap();

    private PriorityColorMapper() {}

    private static Map<String, String> createColorMap() {
        Map<String, String> colors = new HashMap<>();
        colors.put(PRIORITY_LOW, COLOR_LOW);
        colors.put(PRIORITY_MEDIUM, COLOR_MEDIUM);
        colors.put(PRIORITY_HIGH, COLOR_HIGH);
        colors.put(PRIORITY_URGENT, COLOR_URGENT);
        return java.util.Collections.unmodifiableMap(colors);
    }

    public static String getColorHex(String priority) {
        return PRIORITY_COLORS.getOrDefault(priority, COLOR_DEFAULT);
    }

    public static Color getColor(String priority) {
        String hex = getColorHex(priority);
        return Color.web(hex);
    }
    public static String getCssColor(String priority) {
        return getColorHex(priority);
    }

    public static boolean isValidPriority(String priority) {
        return PRIORITY_COLORS.containsKey(priority);
    }

    public static String[] getSupportedPriorities() {
        return PRIORITY_COLORS.keySet().toArray(new String[0]);
    }
}

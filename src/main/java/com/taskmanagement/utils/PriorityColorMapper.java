package com.taskmanagement.utils;

import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for mapping task priorities to color values for UI display.
 * Provides consistent color representation across the application for different priority levels.
 */
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

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with static methods only.
     */
    private PriorityColorMapper() {
        // Utility class, no instantiation
    }

    // ============ Initialization ============

    /**
     * Create the priority color mapping.
     *
     * @return immutable map of priority levels to hex colors
     */
    private static Map<String, String> createColorMap() {
        Map<String, String> colors = new HashMap<>();
        colors.put(PRIORITY_LOW, COLOR_LOW);
        colors.put(PRIORITY_MEDIUM, COLOR_MEDIUM);
        colors.put(PRIORITY_HIGH, COLOR_HIGH);
        colors.put(PRIORITY_URGENT, COLOR_URGENT);
        return java.util.Collections.unmodifiableMap(colors);
    }

    // ============ Color Getter Methods ============

    /**
     * Get hex color for a priority level.
     *
     * @param priority the priority level (Low, Medium, High, Urgent)
     * @return hex color string (e.g., "#28A745"), or default gray if priority unknown
     */
    public static String getColorHex(String priority) {
        return PRIORITY_COLORS.getOrDefault(priority, COLOR_DEFAULT);
    }

    /**
     * Get JavaFX Color object for a priority level.
     *
     * @param priority the priority level
     * @return Color object, or default gray if priority unknown
     */
    public static Color getColor(String priority) {
        String hex = getColorHex(priority);
        return Color.web(hex);
    }

    /**
     * Get CSS color string for a priority level.
     *
     * @param priority the priority level
     * @return CSS color value (e.g., "#28A745")
     */
    public static String getCssColor(String priority) {
        return getColorHex(priority);
    }

    // ============ Validation Methods ============

    /**
     * Check if a priority level is recognized.
     *
     * @param priority the priority level to check
     * @return true if priority is in the supported list
     */
    public static boolean isValidPriority(String priority) {
        return PRIORITY_COLORS.containsKey(priority);
    }

    /**
     * Get all supported priority levels.
     *
     * @return array of priority names
     */
    public static String[] getSupportedPriorities() {
        return PRIORITY_COLORS.keySet().toArray(new String[0]);
    }
}

package com.taskmanagement.service;

import java.util.List;

import com.taskmanagement.model.Label;
import com.taskmanagement.utils.CurrentUser;

/**
 * Service for managing labels and label colors
 * Provides predefined label sets and color validation
 */
public class LabelService {

    // Default Colors
    private static final String DEFAULT_COLOR = "#007BFF";
    
    // Standard Color Palette
    private static final String COLOR_RED = "#DC3545";
    private static final String COLOR_GREEN = "#28A745";
    private static final String COLOR_BLUE = "#007BFF";
    private static final String COLOR_PURPLE = "#6F42C1";
    private static final String COLOR_YELLOW = "#FFC107";
    private static final String COLOR_PINK = "#E83E8C";
    private static final String COLOR_GRAY = "#6C757D";
    private static final String COLOR_CYAN = "#17A2B8";
    private static final String COLOR_ORANGE = "#FD7E14";
    
    // Common Label Names
    private static final String LABEL_BUG = "Bug";
    private static final String LABEL_FEATURE = "Feature";
    private static final String LABEL_DOCUMENTATION = "Documentation";
    private static final String LABEL_ENHANCEMENT = "Enhancement";
    private static final String LABEL_URGENT = "Urgent";
    private static final String LABEL_BLOCKED = "Blocked";
    private static final String LABEL_IN_REVIEW = "In Review";
    private static final String LABEL_ON_HOLD = "On Hold";
    
    // Color Validation Pattern
    private static final String HEX_COLOR_PATTERN = "^#[0-9A-Fa-f]{6}$";
    
    // Error Messages
    private static final String ERR_NOT_LOGGED_IN = "User must be logged in";
    private static final String ERR_LABEL_NAME_REQUIRED = "Label name is required";

    public LabelService() {
    }

    /**
     * Create a label with specified name and color
     * 
     * @param name the label name
     * @param color the hex color code
     * @return the created label
     * @throws SecurityException if user is not logged in
     * @throws IllegalArgumentException if name is empty
     */
    public Label createLabel(String name, String color) {
        if (!CurrentUser.isLoggedIn()) {
            throw new SecurityException(ERR_NOT_LOGGED_IN);
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(ERR_LABEL_NAME_REQUIRED);
        }

        return new Label(name.trim(), color);
    }

    /**
     * Create a label with default color
     * 
     * @param name the label name
     * @return the created label with default color
     */
    public Label createLabel(String name) {
        return createLabel(name, DEFAULT_COLOR);
    }

    /**
     * Get commonly used predefined labels
     * 
     * @return list of common labels
     */
    public List<Label> getCommonLabels() {
        return List.of(
                new Label(LABEL_BUG, COLOR_RED),
                new Label(LABEL_FEATURE, COLOR_GREEN),
                new Label(LABEL_DOCUMENTATION, COLOR_PURPLE),
                new Label(LABEL_ENHANCEMENT, COLOR_YELLOW),
                new Label(LABEL_URGENT, COLOR_PINK),
                new Label(LABEL_BLOCKED, COLOR_GRAY),
                new Label(LABEL_IN_REVIEW, COLOR_CYAN),
                new Label(LABEL_ON_HOLD, COLOR_ORANGE)
        );
    }

    /**
     * Get standard color palette with names
     * 
     * @return list of color options
     */
    public List<Label> getColorOptions() {
        return List.of(
                new Label("Red", COLOR_RED),
                new Label("Green", COLOR_GREEN),
                new Label("Blue", COLOR_BLUE),
                new Label("Purple", COLOR_PURPLE),
                new Label("Yellow", COLOR_YELLOW),
                new Label("Pink", COLOR_PINK),
                new Label("Gray", COLOR_GRAY),
                new Label("Cyan", COLOR_CYAN),
                new Label("Orange", COLOR_ORANGE)
        );
    }

    /**
     * Find a common label by name (case-insensitive)
     * 
     * @param name the label name to search for
     * @return the matching label, or null if not found
     */
    public Label findLabelByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        return getCommonLabels().stream()
                .filter(l -> l.getName().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Validate if a color string is a valid hex color code
     * 
     * @param color the color string to validate
     * @return true if valid hex color format, false otherwise
     */
    public boolean isValidColor(String color) {
        return color != null && color.matches(HEX_COLOR_PATTERN);
    }
}

package com.taskmanagement.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.InputStream;

/**
 * Icon manager for loading and applying SVG-based Heroicons to JavaFX components.
 * Provides utility methods for loading icons with custom sizing and applying them to buttons and labels.
 */
public class HeroiconsIconManager {

    // Path and Size Constants
    private static final String ICONS_PATH = "/com/taskmanagement/icons/";
    private static final int DEFAULT_ICON_SIZE = 24;

    // Error Messages
    private static final String ERR_ICON_NOT_FOUND = "❌ Icon not found: ";
    private static final String ERR_LOADING_ICON = "❌ Error loading icon: ";
    private static final String ERR_SETTING_BUTTON_ICON = "❌ Error setting button icon: ";
    private static final String ERR_SETTING_LABEL_ICON = "❌ Error setting label icon: ";

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with static methods only.
     */
    private HeroiconsIconManager() {
        // Utility class, no instantiation
    }

    // ============ Icon Enum ============

    /**
     * Enumeration of available Heroicons.
     * Each icon represents an SVG file in the resources/icons directory.
     */
    public enum Icon {
        CLIPBOARD("clipboard.svg"),
        PLUS("plus.svg"),
        REFRESH("refresh.svg"),
        ELLIPSIS("ellipsis.svg"),
        ARROW_LEFT("arrow-left.svg"),
        DASHBOARD("dashboard.svg"),
        FOLDER("folder.svg"),
        USER("user.svg"),
        COG("cog.svg");

        private final String fileName;

        /**
         * Construct an Icon enum value.
         *
         * @param fileName the SVG filename
         */
        Icon(String fileName) {
            this.fileName = fileName;
        }

        /**
         * Get the SVG filename for this icon.
         *
         * @return the filename
         */
        public String getFileName() {
            return fileName;
        }
    }

    // ============ Icon Loading Methods ============

    /**
     * Load an icon with default size (24x24).
     *
     * @param icon the icon to load
     * @return an ImageView containing the icon
     */
    public static ImageView loadIcon(Icon icon) {
        return loadIcon(icon, DEFAULT_ICON_SIZE);
    }

    /**
     * Load an icon with custom size.
     *
     * @param icon the icon to load
     * @param size the size in pixels (width and height)
     * @return an ImageView containing the icon with specified size
     */
    public static ImageView loadIcon(Icon icon, int size) {
        try {
            InputStream inputStream = HeroiconsIconManager.class.getResourceAsStream(ICONS_PATH + icon.getFileName());
            if (inputStream == null) {
                System.err.println(ERR_ICON_NOT_FOUND + icon.getFileName());
                return createPlaceholder();
            }

            Image image = new Image(inputStream);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            return imageView;
        } catch (Exception e) {
            System.err.println(ERR_LOADING_ICON + icon.getFileName());
            e.printStackTrace();
            return createPlaceholder();
        }
    }

    // ============ Button Icon Methods ============

    /**
     * Set an icon on a button with default size (24x24).
     *
     * @param button the button to update
     * @param icon the icon to set
     */
    public static void setButtonIcon(Button button, Icon icon) {
        setButtonIcon(button, icon, DEFAULT_ICON_SIZE);
    }

    /**
     * Set an icon on a button with custom size.
     *
     * @param button the button to update
     * @param icon the icon to set
     * @param size the icon size in pixels
     */
    public static void setButtonIcon(Button button, Icon icon, int size) {
        try {
            ImageView imageView = loadIcon(icon, size);
            String text = button.getText();
            button.setGraphic(imageView);
            if (text != null && !text.isEmpty()) {
                button.setText(text);
            }
        } catch (Exception e) {
            System.err.println(ERR_SETTING_BUTTON_ICON + e.getMessage());
        }
    }

    // ============ Label Icon Methods ============

    /**
     * Set an icon on a label with default size (24x24).
     *
     * @param label the label to update
     * @param icon the icon to set
     */
    public static void setLabelIcon(Label label, Icon icon) {
        setLabelIcon(label, icon, DEFAULT_ICON_SIZE);
    }

    /**
     * Set an icon on a label with custom size.
     *
     * @param label the label to update
     * @param icon the icon to set
     * @param size the icon size in pixels
     */
    public static void setLabelIcon(Label label, Icon icon, int size) {
        try {
            ImageView imageView = loadIcon(icon, size);
            String text = label.getText();
            label.setGraphic(imageView);
            if (text != null && !text.isEmpty()) {
                label.setText(text);
            }
        } catch (Exception e) {
            System.err.println(ERR_SETTING_LABEL_ICON + e.getMessage());
        }
    }

    // ============ Helper Methods ============

    /**
     * Create a placeholder ImageView when icon loading fails.
     *
     * @return a gray placeholder ImageView
     */
    private static ImageView createPlaceholder() {
        ImageView placeholder = new ImageView();
        placeholder.setFitWidth(DEFAULT_ICON_SIZE);
        placeholder.setFitHeight(DEFAULT_ICON_SIZE);
        placeholder.setStyle("-fx-fill: #999;");
        return placeholder;
    }

    /**
     * Get the full resource path for an icon.
     *
     * @param icon the icon
     * @return the full resource path
     */
    public static String getIconPath(Icon icon) {
        return ICONS_PATH + icon.getFileName();
    }
}

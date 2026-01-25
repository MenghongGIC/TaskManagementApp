package com.taskmanagement.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.taskmanagement.constants.AppConstants;
import java.io.IOException;
import java.util.Optional;

/**
 * Centralized UI utility for common JavaFX operations
 * Provides alert dialogs, scene loading, and styling helpers
 */
public class UIUtils {

    // Alert Title Constants
    private static final String TITLE_ERROR = "Error";
    private static final String TITLE_SUCCESS = "Success";
    private static final String TITLE_UNSAVED = "Unsaved Changes";
    private static final String TITLE_DELETE_CONFIRM = "Confirm Deletion";
    private static final String TITLE_DISCARD = "Discard Changes";
    private static final String TITLE_FXML_ERROR = "FXML Load Error";
    
    // Alert Message Constants
    private static final String MSG_UNSAVED_CHANGES = "You have unsaved data. Do you want to close without saving?";
    private static final String MSG_DELETE_CONFIRM = "Are you sure you want to delete '%s'? This action cannot be undone.";
    private static final String MSG_DISCARD_CHANGES = "Do you want to discard your changes and close?";
    private static final String MSG_FXML_FAILED = "Failed to load FXML: ";

    // Styling Constants
    private static final String STYLE_ERROR_TEXT = "-fx-text-fill: #e74c3c; -fx-font-size: 12;";
    private static final String STYLE_SUCCESS_TEXT = "-fx-text-fill: #27ae60; -fx-font-size: 12;";

    private UIUtils() {
        // Utility class, no instantiation
    }

    // ===== Error/Success/Warning Alerts =====
    /**
     * Show an error alert dialog
     * 
     * @param title the alert title
     * @param message the error message
     */
    public static void showError(String title, String message) {
        showAlert(AlertType.ERROR, title, message);
    }

    /**
     * Show an error alert dialog with default title
     * 
     * @param message the error message
     */
    public static void showError(String message) {
        showError(TITLE_ERROR, message);
    }

    /**
     * Show a success/information alert dialog
     * 
     * @param title the alert title
     * @param message the success message
     */
    public static void showSuccess(String title, String message) {
        showAlert(AlertType.INFORMATION, title, message);
    }

    /**
     * Show a success alert dialog with default title
     * 
     * @param message the success message
     */
    public static void showSuccess(String message) {
        showSuccess(TITLE_SUCCESS, message);
    }

    /**
     * Show a warning alert dialog
     * 
     * @param title the alert title
     * @param message the warning message
     */
    public static void showWarning(String title, String message) {
        showAlert(AlertType.WARNING, title, message);
    }

    /**
     * Show a confirmation alert dialog
     * 
     * @param title the alert title
     * @param message the confirmation message
     */
    public static void showConfirmation(String title, String message) {
        showAlert(AlertType.CONFIRMATION, title, message);
    }

    /**
     * Generic alert dialog display
     * 
     * @param type the alert type
     * @param title the alert title
     * @param message the alert message
     */
    private static void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ===== Confirmation Dialogs =====
    /**
     * Show unsaved changes confirmation dialog
     * 
     * @return true if user confirms, false otherwise
     */
    public static boolean showUnsavedChangesConfirmation() {
        Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
        confirmDialog.setTitle(TITLE_UNSAVED);
        confirmDialog.setHeaderText("Are you sure you want to close?");
        confirmDialog.setContentText(MSG_UNSAVED_CHANGES);
        Optional<ButtonType> result = confirmDialog.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Show delete confirmation dialog
     * 
     * @param itemName the name of the item to delete
     * @return true if user confirms deletion, false otherwise
     */
    public static boolean showDeleteConfirmation(String itemName) {
        Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
        confirmDialog.setTitle(TITLE_DELETE_CONFIRM);
        confirmDialog.setHeaderText("Delete " + itemName + "?");
        confirmDialog.setContentText(String.format(MSG_DELETE_CONFIRM, itemName));
        Optional<ButtonType> result = confirmDialog.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Show discard changes confirmation dialog
     * 
     * @return true if user confirms discard, false otherwise
     */
    public static boolean showDiscardChangesConfirmation() {
        Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
        confirmDialog.setTitle(TITLE_DISCARD);
        confirmDialog.setHeaderText("You have unsaved changes");
        confirmDialog.setContentText(MSG_DISCARD_CHANGES);
        Optional<ButtonType> result = confirmDialog.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Show custom confirmation dialog
     * 
     * @param title the dialog title
     * @param header the dialog header
     * @param content the dialog content
     * @return true if user confirms, false otherwise
     */
    public static boolean showCustomConfirmation(String title, String header, String content) {
        Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
        confirmDialog.setTitle(title);
        confirmDialog.setHeaderText(header);
        confirmDialog.setContentText(content);
        Optional<ButtonType> result = confirmDialog.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    // ===== FXML Loading and Scene Management =====
    /**
     * Load FXML file from classpath
     * 
     * @param fxmlPath the FXML file path (classpath resource)
     * @return the loaded Parent node, or null if loading failed
     */
    public static Parent loadFXML(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(UIUtils.class.getResource(fxmlPath));
            return loader.load();
        } catch (IOException e) {
            showError(TITLE_FXML_ERROR, MSG_FXML_FAILED + fxmlPath);
            e.printStackTrace(System.err);
            return null;
        }
    }

    /**
     * Switch to a new scene
     * 
     * @param stage the stage to update
     * @param fxmlPath the FXML file path to load
     * @param title the window title
     */
    public static void switchScene(Stage stage, String fxmlPath, String title) {
        Parent root = loadFXML(fxmlPath);
        if (root != null) {
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        }
    }

    // ===== Status/Priority Styling =====
    /**
     * Get hex color for task status
     * 
     * @param status the task status
     * @return the hex color code
     */
    public static String getStatusColor(String status) {
        if (status == null) return "";
        return switch (status) {
            case AppConstants.STATUS_TODO -> "#3498db";
            case AppConstants.STATUS_IN_PROGRESS -> "#f39c12";
            case AppConstants.STATUS_DONE -> "#27ae60";
            default -> "#95a5a6";
        };
    }

    /**
     * Get JavaFX CSS style string for task status
     * 
     * @param status the task status
     * @return the CSS style string
     */
    public static String getStatusStyle(String status) {
        String color = getStatusColor(status);
        return String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-padding: 4px 8px; -fx-border-radius: 4;", color);
    }

    /**
     * Get hex color for task priority
     * 
     * @param priority the task priority
     * @return the hex color code
     */
    public static String getPriorityColor(String priority) {
        if (priority == null) return "";
        return switch (priority) {
            case AppConstants.PRIORITY_LOW -> "#27ae60";
            case AppConstants.PRIORITY_MEDIUM -> "#f39c12";
            case AppConstants.PRIORITY_HIGH -> "#e74c3c";
            default -> "#95a5a6";
        };
    }

    /**
     * Get JavaFX CSS style string for task priority
     * 
     * @param priority the task priority
     * @return the CSS style string
     */
    public static String getPriorityStyle(String priority) {
        String color = getPriorityColor(priority);
        return String.format("-fx-text-fill: %s; -fx-font-weight: bold;", color);
    }

    /**
     * Get text color CSS for task priority
     * 
     * @param priority the task priority
     * @return the text color CSS
     */
    public static String getPriorityTextColor(String priority) {
        return switch (priority) {
            case AppConstants.PRIORITY_LOW -> "-fx-text-fill: #27ae60;";
            case AppConstants.PRIORITY_MEDIUM -> "-fx-text-fill: #f39c12;";
            case AppConstants.PRIORITY_HIGH -> "-fx-text-fill: #e74c3c;";
            default -> "-fx-text-fill: #95a5a6;";
        };
    }

    /**
     * Get background and text color CSS for task priority
     * 
     * @param priority the task priority
     * @return the background and text CSS
     */
    public static String getPriorityBackground(String priority) {
        return switch (priority) {
            case AppConstants.PRIORITY_LOW -> "-fx-background-color: #d5f4e6; -fx-text-fill: #27ae60;";
            case AppConstants.PRIORITY_MEDIUM -> "-fx-background-color: #fdebd0; -fx-text-fill: #f39c12;";
            case AppConstants.PRIORITY_HIGH -> "-fx-background-color: #fadbd8; -fx-text-fill: #e74c3c;";
            default -> "-fx-background-color: #ecf0f1;";
        };
    }

    // ===== Label Styling =====
    /**
     * Set error style on a label
     * 
     * @param label the label to style
     * @param message the error message to display
     */
    public static void setErrorStyle(Label label, String message) {
        label.setText(message);
        label.setStyle(STYLE_ERROR_TEXT);
    }

    /**
     * Set success style on a label
     * 
     * @param label the label to style
     * @param message the success message to display
     */
    public static void setSuccessStyle(Label label, String message) {
        label.setText(message);
        label.setStyle(STYLE_SUCCESS_TEXT);
    }
    public static void clearLabel(Label label) {
        label.setText("");
        label.setStyle("");
    }

    public static void applyHoverEffect(Node node) {
        node.setOnMouseEntered(e -> node.setStyle("-fx-opacity: 0.8;"));
        node.setOnMouseExited(e -> node.setStyle("-fx-opacity: 1.0;"));
    }
    public static boolean isFieldEmpty(String field) {
        return field == null || field.trim().isEmpty();
    }

    public static String formatStatus(String status) {
        if (status == null) return "";
        return status.replace("_", " ");
    }
    public static String formatPriority(String priority) {
        if (priority == null) return "";
        return priority.substring(0, 1).toUpperCase() + priority.substring(1).toLowerCase();
    }
}

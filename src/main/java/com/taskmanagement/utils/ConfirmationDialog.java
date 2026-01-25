package com.taskmanagement.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Utility class for showing confirmation dialogs throughout the application
 */
public class ConfirmationDialog {

    /**
     * Show confirmation dialog for unsaved changes when closing a dialog
     * @return true if user confirms, false otherwise
     */
    public static boolean showUnsavedChangesConfirmation() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Unsaved Changes");
        confirmDialog.setHeaderText("Are you sure you want to close?");
        confirmDialog.setContentText("You have unsaved data. Do you want to close without saving?");
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Show confirmation dialog for deleting an item
     * @param itemName The name of the item to delete
     * @return true if user confirms deletion, false otherwise
     */
    public static boolean showDeleteConfirmation(String itemName) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Delete " + itemName + "?");
        confirmDialog.setContentText("Are you sure you want to delete '" + itemName + "'? This action cannot be undone.");
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Show confirmation dialog for discarding changes
     * @return true if user confirms, false otherwise
     */
    public static boolean showDiscardChangesConfirmation() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Discard Changes");
        confirmDialog.setHeaderText("You have unsaved changes");
        confirmDialog.setContentText("Do you want to discard your changes and close?");
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Show confirmation dialog for a custom action
     * @param title Dialog title
     * @param header Dialog header text
     * @param content Dialog content text
     * @return true if user confirms, false otherwise
     */
    public static boolean showCustomConfirmation(String title, String header, String content) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle(title);
        confirmDialog.setHeaderText(header);
        confirmDialog.setContentText(content);
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Show warning dialog
     * @param title Dialog title
     * @param header Dialog header text
     * @param content Dialog content text
     */
    public static void showWarning(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Show error dialog
     * @param title Dialog title
     * @param header Dialog header text
     * @param content Dialog content text
     */
    public static void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Show success/information dialog
     * @param title Dialog title
     * @param header Dialog header text
     * @param content Dialog content text
     */
    public static void showSuccess(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

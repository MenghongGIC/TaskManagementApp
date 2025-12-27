package com.taskmanagement.utils;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Simple and reliable navigation manager for switching between screens.
 * 
 * Features:
 * - Loads any FXML file
 * - Applies global CSS if available
 * - Sets window title and behavior
 * - Smooth transitions
 * - Convenience methods for common screens
 */
public final class NavigationManager {

    private static final String CSS_PATH = "/css/style.css";

    private NavigationManager() {
        // Utility class - prevent instantiation
    }

    /**
     * Core method: Load an FXML screen into the stage
     */
    public static void navigateTo(Stage stage, String fxmlPath, String windowTitle, boolean maximized) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Add global CSS if it exists
            String css = NavigationManager.class.getResource(CSS_PATH).toExternalForm();
            if (css != null) {
                scene.getStylesheets().add(css);
            }

            // Smooth transition
            stage.hide();
            stage.setScene(scene);
            stage.setTitle(windowTitle);
            stage.setMaximized(maximized);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlPath);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    /**
     * Overloaded version - defaults to maximized
     */
    public static void navigateTo(Stage stage, String fxmlPath, String windowTitle) {
        navigateTo(stage, fxmlPath, windowTitle, true);
    }

    /**
     * Go to Dashboard (after login)
     */
    public static void goToDashboard(Stage stage) {
        navigateTo(stage, "/fxml/main/MainDashboard.fxml", "Task Manager - Dashboard", true);
    }

    /**
     * Go back to Login screen (after logout)
     */
    public static void goToLogin(Stage stage) {
        navigateTo(stage, "/fxml/auth/AuthPanel.fxml", "Task Manager - Login", false);
    }

    /**
     * Go to Profile screen
     */
    public static void goToProfile(Stage stage) {
        navigateTo(stage, "/fxml/user/ProfilePanel.fxml", "Task Manager - Profile", true);
    }

    /**
     * Utility: Get Stage from any Window (useful in event handlers)
     */
    public static Stage getCurrentStage(javafx.stage.Window window) {
        return (window instanceof Stage) ? (Stage) window : null;
    }
}
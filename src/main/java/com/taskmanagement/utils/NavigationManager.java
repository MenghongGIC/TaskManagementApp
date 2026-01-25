package com.taskmanagement.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Stack;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Centralized navigation manager for JavaFX scenes
 * Handles scene switching, navigation history, and FXML loading
 */
public final class NavigationManager {

    // Resource Paths
    private static final String CSS_PATH = "/css/style.css";
    
    // Navigation History
    private static final Stack<NavigationHistory> navigationHistory = new Stack<>();
    private static Stage currentStage;

    /**
     * Internal class for tracking navigation history
     */
    private static class NavigationHistory {
        String fxmlPath;
        String windowTitle;
        boolean maximized;

        NavigationHistory(String fxmlPath, String windowTitle, boolean maximized) {
            this.fxmlPath = fxmlPath;
            this.windowTitle = windowTitle;
            this.maximized = maximized;
        }
    }

    /**
     * Prevent instantiation of static utility class
     */
    private NavigationManager() {
    }

    /**
     * Navigate to a new scene with full control
     * 
     * @param stage the stage to navigate from
     * @param fxmlPath the FXML file path to load
     * @param windowTitle the window title to display
     * @param maximized whether to maximize the window
     */
    public static void navigateTo(Stage stage, String fxmlPath, String windowTitle, boolean maximized) {
        try {
            // Save current navigation to history before navigating
            if (currentStage != null && stage == currentStage && currentStage.getScene() != null) {
                String currentTitle = currentStage.getTitle();
                // Only add to history if we have a valid previous screen
                if (currentTitle != null && !currentTitle.isEmpty()) {
                    navigationHistory.push(new NavigationHistory(fxmlPath, currentTitle, currentStage.isMaximized()));
                }
            }

            currentStage = stage;

            URL fxmlResource = NavigationManager.class.getResource(fxmlPath);
            if (fxmlResource == null) {
                throw new IOException("FXML file not found: " + fxmlPath);
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlResource);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            String css = NavigationManager.class.getResource(CSS_PATH).toExternalForm();
            if (css != null) {
                scene.getStylesheets().add(css);
            }

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
     * Navigate to a new scene (maximized by default)
     * 
     * @param stage the stage to navigate from
     * @param fxmlPath the FXML file path to load
     * @param windowTitle the window title to display
     */
    public static void navigateTo(Stage stage, String fxmlPath, String windowTitle) {
        navigateTo(stage, fxmlPath, windowTitle, true);
    }

    /**
     * Navigate to a new scene using the current stage
     * 
     * @param fxmlPath the FXML file path to load
     * @param windowTitle the window title to display
     */
    public static void navigateTo(String fxmlPath, String windowTitle) {
        if (currentStage != null) {
            navigateTo(currentStage, fxmlPath, windowTitle, true);
        } else {
            System.err.println("No current stage available for navigation");
        }
    }

    /**
     * Navigate to dashboard
     * 
     * @param stage the stage to navigate from
     */
    public static void goToDashboard(Stage stage) {
        navigateTo(stage, "/fxml/main/MainDashboard.fxml", "Task Manager - Dashboard", true);
    }

    /**
     * Navigate to login screen
     * 
     * @param stage the stage to navigate from
     */
    public static void goToLogin(Stage stage) {
        navigateTo(stage, "/fxml/auth/AuthPanel.fxml", "Task Manager - Login", false);
    }

    /**
     * Navigate to user profile
     * 
     * @param stage the stage to navigate from
     */
    public static void goToProfile(Stage stage) {
        navigateTo(stage, "/fxml/main/Profile.fxml", "Task Manager - Profile", true);
    }

    /**
     * Navigate to admin panel (admin only)
     * 
     * @param stage the stage to navigate from
     * @throws SecurityException if user is not admin
     */
    public static void goToAdminPanel(Stage stage) {
        if (!CurrentUser.isAdmin()) {
            throw new SecurityException("Access Denied: Admin privileges required");
        }
        navigateTo(stage, "/fxml/main/UserManagementPanel.fxml", "Task Manager - Admin Panel", true);
    }

    public static Stage getCurrentStage(javafx.stage.Window window) {
        return (window instanceof Stage) ? (Stage) window : null;
    }
    public static void goBack() {
        if (currentStage != null && !navigationHistory.isEmpty()) {
            NavigationHistory previous = navigationHistory.pop();
            navigateTo(currentStage, previous.fxmlPath, previous.windowTitle, previous.maximized);
        }
    }

    public static boolean canGoBack() {
        return !navigationHistory.isEmpty();
    }
    public static void clearHistory() {
        navigationHistory.clear();
    }
}
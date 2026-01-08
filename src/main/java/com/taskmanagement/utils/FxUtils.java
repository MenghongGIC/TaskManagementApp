package com.taskmanagement.utils;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class FxUtils {

    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static Parent loadFXML(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(FxUtils.class.getResource(fxmlPath));
            return loader.load();
        } catch (IOException e) {
            showError("FXML Load Error", "Failed to load FXML: " + fxmlPath);
            e.printStackTrace(System.err);
            return null;
        }
    }
    public static void switchScene(Stage stage, String fxmlPath, String title) {
        Parent root = loadFXML(fxmlPath);
        if (root != null) {
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        }
    }
    // public static void loadMainDashboard(Stage stage){
    //     switchScene(stage, "/com/taskmanagement/fxml/Primary.fxml", "Task Management Dashboard");
    // }
}

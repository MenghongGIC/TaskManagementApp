package com.taskmanagement;

import java.io.IOException;
import java.sql.Connection;

import com.taskmanagement.database.DBConnection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("auth/LoginView"), 400, 600);
        stage.setScene(scene);
        stage.setTitle("Task Management System");
        stage.setWidth(1440);
        stage.setHeight(1080);
        stage.setResizable(true);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/taskmanagement/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        System.out.println("=== Testing Database Connection ===");
        try {
            DBConnection dbConnection = DBConnection.getInstance();
            Connection connection = dbConnection.getConnection();
            
            if (connection != null && !connection.isClosed()) {
                System.out.println("✓ Database connection verified successfully!");
                System.out.println("✓ Application is ready to start.");
            } else {
                System.out.println("✗ Database connection failed: Connection is null or closed.");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("✗ Database connection test failed:");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        
        System.out.println("=== Starting Application ===\n");
        launch(args);
    }
}
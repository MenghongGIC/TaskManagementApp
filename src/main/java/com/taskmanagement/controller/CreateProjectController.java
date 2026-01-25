package com.taskmanagement.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import com.taskmanagement.model.Project;
import com.taskmanagement.service.ProjectService;
import com.taskmanagement.utils.ConfirmationDialog;

/**
 * Controller for creating a new project
 */
public class CreateProjectController {
    
    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private TextField colorField;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private Label validationLabel;
    
    private ProjectService projectService;
    private Stage dialogStage;
    private Runnable onProjectCreated; // Callback to refresh parent
    
    @FXML
    public void initialize() {
        projectService = new ProjectService();
        
        // Setup buttons
        saveBtn.setOnAction(e -> handleSave());
        cancelBtn.setOnAction(e -> handleCancel());
        
        // Set default color
        colorField.setText("#3498db");
        
        // Clear validation label
        validationLabel.setText("");
    }
    
    /**
     * Set the dialog stage for closing
     */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }
    
    /**
     * Set callback to refresh parent when project is created
     */
    public void setOnProjectCreated(Runnable callback) {
        this.onProjectCreated = callback;
    }
    
    /**
     * Validate form inputs
     */
    private boolean validateForm() {
        validationLabel.setText("");
        
        String name = nameField.getText().trim();
        String color = colorField.getText().trim();
        
        // Name validation
        if (name.isEmpty()) {
            validationLabel.setText("⚠️ Project name is required");
            nameField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            return false;
        } else {
            nameField.setStyle("");
        }
        
        // Color validation (optional but must be valid if provided)
        if (!color.isEmpty() && !isValidHexColor(color)) {
            validationLabel.setText("⚠️ Invalid color format. Use #RRGGBB (e.g., #3498db)");
            colorField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            return false;
        } else {
            colorField.setStyle("");
        }
        
        return true;
    }
    
    /**
     * Validate hex color format
     */
    private boolean isValidHexColor(String color) {
        return color.matches("^#[0-9a-fA-F]{6}$");
    }
    
    /**
     * Handle save button click
     */
    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }
        
        try {
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            String color = colorField.getText().trim();
            
            if (color.isEmpty()) {
                color = "#3498db"; // Default color
            }
            
            System.out.println("💾 Creating project: " + name);
            
            // Create project via service
            Project newProject = projectService.createProject(
                name,
                description.isEmpty() ? null : description,
                color
            );
            
            System.out.println("✅ Project created: " + newProject.getName());
            showAlert("Success", "✅ Project '" + name + "' created successfully!", Alert.AlertType.INFORMATION);
            
            // Execute callback to refresh parent view
            if (onProjectCreated != null) {
                onProjectCreated.run();
            }
            
            // Close dialog
            dialogStage.close();
            
        } catch (Exception e) {
            System.err.println("❌ Error creating project: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to create project: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Handle cancel button click
     */
    @FXML
    private void handleCancel() {
        // Check if there's unsaved data
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        
        if (!name.isEmpty() || !description.isEmpty()) {
            if (ConfirmationDialog.showUnsavedChangesConfirmation()) {
                System.out.println("❌ Create project cancelled");
                dialogStage.close();
            }
        } else {
            System.out.println("❌ Create project cancelled");
            dialogStage.close();
        }
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

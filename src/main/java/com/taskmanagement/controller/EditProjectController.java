package com.taskmanagement.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import com.taskmanagement.model.Project;
import com.taskmanagement.service.ProjectService;
import java.util.Optional;

public class EditProjectController {
    
    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private TextField colorField;
    @FXML private Rectangle colorPreview;
    @FXML private Label headerLabel;
    @FXML private Label validationLabel;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private Button undoBtn;
    
    private Project originalProject;
    private Project workingProject;
    private ProjectService projectService;
    private Stage stage;
    private Runnable onSaveCallback;
    
    
    private String originalName;
    private String originalDescription;
    private String originalColor;
    
    public EditProjectController() {
        this.projectService = new ProjectService();
    }
    
    @FXML
    public void initialize() {
        System.out.println("🔧 EditProjectController initialized");
        
        
        saveBtn.setOnAction(e -> handleSave());
        cancelBtn.setOnAction(e -> handleCancel());
        undoBtn.setOnAction(e -> handleUndo());
        
        
        colorField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateColorPreview(newVal);
            validateForm();
        });
        
        
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        
        
        descriptionField.setWrapText(true);
        
        validationLabel.setText("");
        validationLabel.setStyle("-fx-text-fill: #e74c3c;");
    }
    
    

    public void setProject(Project project, Stage stage) {
        this.originalProject = project;
        this.workingProject = new Project();
        this.stage = stage;
        
        
        workingProject.setId(project.getId());
        workingProject.setName(project.getName());
        workingProject.setDescription(project.getDescription());
        workingProject.setColor(project.getColor());
        
        
        originalName = project.getName();
        originalDescription = project.getDescription();
        originalColor = project.getColor();
        
        populateFields();
        updateColorPreview(colorField.getText());
        
        System.out.println("📝 Editing project: " + project.getName());
    }
    
    

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }
    
    

    private void populateFields() {
        if (workingProject == null) return;
        
        Platform.runLater(() -> {
            headerLabel.setText("Edit Project: " + originalProject.getName());
            nameField.setText(workingProject.getName());
            descriptionField.setText(workingProject.getDescription() != null ? workingProject.getDescription() : "");
            colorField.setText(workingProject.getColor() != null ? workingProject.getColor() : "#3498db");
        });
    }
    
    

    private void updateColorPreview(String hexColor) {
        if (hexColor == null || hexColor.isEmpty()) {
            hexColor = "#3498db"; 
        }
        
        try {
            Color color = Color.web(hexColor);
            if (colorPreview != null) {
                colorPreview.setFill(color);
            }
        } catch (IllegalArgumentException e) {
            
            System.out.println("ℹ️ Invalid color format: " + hexColor);
        }
    }
    
    

    private boolean validateForm() {
        String name = nameField.getText().trim();
        String color = colorField.getText().trim();
        
        
        validationLabel.setText("");
        
        
        if (name.isEmpty()) {
            validationLabel.setText("⚠️ Project name is required");
            nameField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            return false;
        } else {
            nameField.setStyle("");
        }
        
        
        if (!color.isEmpty() && !isValidHexColor(color)) {
            validationLabel.setText("⚠️ Invalid color format. Use #RRGGBB (e.g., #3498db)");
            colorField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            return false;
        } else {
            colorField.setStyle("");
        }
        
        return true;
    }
    
    

    private boolean isValidHexColor(String color) {
        if (color == null || color.isEmpty()) {
            return true; 
        }
        return color.matches("^#[0-9a-fA-F]{6}$");
    }
    
    

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }
        
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String color = colorField.getText().trim();
        
        if (color.isEmpty()) {
            color = "#3498db"; 
        }
        
        
        boolean nameChanged = !name.equals(originalName);
        boolean descChanged = !description.equals(originalDescription != null ? originalDescription : "");
        boolean colorChanged = !color.equals(originalColor != null ? originalColor : "#3498db");
        
        if (!nameChanged && !descChanged && !colorChanged) {
            System.out.println("ℹ️ No changes detected");
            stage.close();
            return;
        }
        
        try {
            System.out.println("💾 Saving project: " + name);
            System.out.println("   Changes: Name=" + nameChanged + ", Desc=" + descChanged + ", Color=" + colorChanged);
            
            
            workingProject.setName(name);
            workingProject.setDescription(description);
            workingProject.setColor(color);
            
            
            projectService.updateProject(workingProject);
            
            
            originalProject.setName(name);
            originalProject.setDescription(description);
            originalProject.setColor(color);
            
            System.out.println("✅ Project saved successfully");
            showAlert("Success", "✅ Project '" + name + "' updated successfully!", Alert.AlertType.INFORMATION);
            
            
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            
            
            stage.close();
            
        } catch (Exception e) {
            System.err.println("❌ Error saving project: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to save project: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    

    @FXML
    private void handleCancel() {
        System.out.println("❌ Edit cancelled");
        
        
        if (hasChanges()) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Discard Changes");
            confirmAlert.setHeaderText("You have unsaved changes");
            confirmAlert.setContentText("Do you want to discard your changes and close?");
            
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                System.out.println("↺ Keeping dialog open");
                return; 
            }
        }
        
        stage.close();
    }
    
    

    @FXML
    private void handleUndo() {
        System.out.println("↶ Undoing changes");
        
        
        nameField.setText(originalName);
        descriptionField.setText(originalDescription != null ? originalDescription : "");
        colorField.setText(originalColor != null ? originalColor : "#3498db");
        
        validationLabel.setText("↶ Changes reverted to original");
        validationLabel.setStyle("-fx-text-fill: #3498db;");
        
        
        nameField.setStyle("");
        colorField.setStyle("");
    }
    
    

    private boolean hasChanges() {
        String currentName = nameField.getText().trim();
        String currentDesc = descriptionField.getText().trim();
        String currentColor = colorField.getText().trim();
        
        if (currentColor.isEmpty()) {
            currentColor = "#3498db";
        }
        
        return !currentName.equals(originalName) ||
               !currentDesc.equals(originalDescription != null ? originalDescription : "") ||
               !currentColor.equals(originalColor != null ? originalColor : "#3498db");
    }
    
    

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

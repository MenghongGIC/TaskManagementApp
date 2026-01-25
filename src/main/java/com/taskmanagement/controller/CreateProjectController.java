package com.taskmanagement.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.taskmanagement.service.ProjectService;
import com.taskmanagement.utils.UIUtils;

public class CreateProjectController {
    
    private static final String DEFAULT_COLOR = "#3498db";
    private static final String COLOR_REGEX = "^#[0-9a-fA-F]{6}$";
    private static final String ERROR_STYLE = "-fx-border-color: #e74c3c; -fx-border-width: 2;";
    private static final String NORMAL_STYLE = "";
    
    private static final String MSG_NAME_REQUIRED = "Project name is required";
    private static final String MSG_COLOR_INVALID = "Invalid color format";
    
    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private TextField colorField;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private Label validationLabel;
    
    private ProjectService projectService;
    private Stage dialogStage;
    private Runnable onProjectCreated;
    
    @FXML
    public void initialize() {
        projectService = new ProjectService();
        setupButtons();
        setupDefaults();
    }
    
    private void setupButtons() {
        saveBtn.setOnAction(e -> handleSave());
        cancelBtn.setOnAction(e -> handleCancel());
    }
    
    private void setupDefaults() {
        colorField.setText(DEFAULT_COLOR);
        clearValidation();
    }
    
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public void setOnProjectCreated(Runnable callback) {
        this.onProjectCreated = callback;
    }
    
    private void clearValidation() {
        validationLabel.setText("");
    }
    
    private void setValidationError(String message) {
        validationLabel.setText("⚠️ " + message);
    }
    
    private boolean validateForm() {
        clearValidation();
        
        String name = nameField.getText().trim();
        String color = colorField.getText().trim();
        
        if (!validateName(name)) {
            return false;
        }
        
        if (!validateColor(color)) {
            return false;
        }
        
        return true;
    }
    
    private boolean validateName(String name) {
        if (name.isEmpty()) {
            setValidationError(MSG_NAME_REQUIRED);
            setFieldError(nameField);
            return false;
        }
        clearFieldError(nameField);
        return true;
    }
    
    private boolean validateColor(String color) {
        if (color.isEmpty()) {
            return true; // Optional field
        }
        if (!isValidHexColor(color)) {
            setValidationError(MSG_COLOR_INVALID);
            setFieldError(colorField);
            return false;
        }
        clearFieldError(colorField);
        return true;
    }
    
    private void setFieldError(TextField field) {
        field.setStyle(ERROR_STYLE);
    }
    
    private void clearFieldError(TextField field) {
        field.setStyle(NORMAL_STYLE);
    }
    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }
        
        try {
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            String color = getColor();
            
            projectService.createProject(
                name,
                description.isEmpty() ? null : description,
                color
            );
            
            UIUtils.showSuccess("Success", "Project '" + name + "' created successfully!");
            if (onProjectCreated != null) {
                onProjectCreated.run();
            }
            dialogStage.close();
            
        } catch (Exception e) {
            UIUtils.showError("Error", "Failed to create project: " + e.getMessage());
        }
    }
    
    private String getColor() {
        String color = colorField.getText().trim();
        return color.isEmpty() ? DEFAULT_COLOR : color;
    }
    @FXML
    private void handleCancel() {
        if (hasUnsavedChanges()) {
            if (UIUtils.showUnsavedChangesConfirmation()) {
                dialogStage.close();
            }
        } else {
            dialogStage.close();
        }
    }
    
    private boolean hasUnsavedChanges() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        return !name.isEmpty() || !description.isEmpty();
    }
    
    private boolean isValidHexColor(String color) {
        return color.matches(COLOR_REGEX);
    }
}
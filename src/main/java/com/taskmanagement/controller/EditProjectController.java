package com.taskmanagement.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import com.taskmanagement.model.Project;
import com.taskmanagement.service.ProjectService;
import com.taskmanagement.utils.UIUtils;
import java.util.Optional;

public class EditProjectController {
    
    // Style Constants
    private static final String ERROR_STYLE = "-fx-border-color: #e74c3c; -fx-border-width: 2;";
    private static final String DEFAULT_COLOR = "#3498db";
    private static final String ERROR_TEXT_COLOR = "-fx-text-fill: #e74c3c;";
    private static final String SUCCESS_TEXT_COLOR = "-fx-text-fill: #3498db;";
    private static final String HEX_COLOR_REGEX = "^#[0-9a-fA-F]{6}$";
    
    // Input Fields
    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private TextField colorField;
    
    // Display Elements
    @FXML private Rectangle colorPreview;
    @FXML private Label headerLabel;
    @FXML private Label validationLabel;
    
    // Control Buttons
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
        validationLabel.setStyle(ERROR_TEXT_COLOR);
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
        
        System.out.println("Editing project: " + project.getName());
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
            colorField.setText(workingProject.getColor() != null ? workingProject.getColor() : DEFAULT_COLOR);
        });
    }
    
    

    private void updateColorPreview(String hexColor) {
        String validColor = (hexColor == null || hexColor.isEmpty()) ? DEFAULT_COLOR : hexColor;
        try {
            if (colorPreview != null) {
                colorPreview.setFill(Color.web(validColor));
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid color format: " + validColor);
        }
    }
    
    

    private boolean validateForm() {
        resetFieldStyles();
        validationLabel.setText("");
        
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showValidationError(nameField, "Project name is required");
            return false;
        }
        
        String color = colorField.getText().trim();
        if (!color.isEmpty() && !isValidHexColor(color)) {
            showValidationError(colorField, "Invalid color format.");
            return false;
        }
        
        return true;
    }
    
    private void showValidationError(TextField field, String message) {
        field.setStyle(ERROR_STYLE);
        validationLabel.setText(message);
        validationLabel.setStyle(ERROR_TEXT_COLOR);
    }
    
    private void resetFieldStyles() {
        nameField.setStyle("");
        colorField.setStyle("");
    }
    
    private boolean isValidHexColor(String color) {
        return color != null && !color.isEmpty() && color.matches(HEX_COLOR_REGEX);
    }
    private static class FormData {
        String name;
        String description;
        String color;
        FormData(String name, String description, String color) {
            this.name = name;
            this.description = description;
            this.color = color;
        }
    }
    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }
        
        FormData formData = extractFormData();
        if (!hasActualChanges(formData)) {
            System.out.println("No changes detected");
            stage.close();
            return;
        }
        
        try {
            updateProjectWithFormData(formData);
            System.out.println("Project saved successfully");
            showAlert("Success", "Project '" + formData.name + "' updated successfully!", Alert.AlertType.INFORMATION);
            
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            stage.close();
        } catch (Exception e) {
            System.err.println("Error saving project: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to save project: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleCancel() {
        System.out.println("Edit cancelled");
        if (hasChanges() && !showConfirmDiscardDialog()) {
            System.out.println("↺ Keeping dialog open");
            return;
        }
        stage.close();
    }
    
    private boolean showConfirmDiscardDialog() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Discard Changes");
        confirmAlert.setHeaderText("You have unsaved changes");
        confirmAlert.setContentText("Do you want to discard your changes and close?");
        Optional<ButtonType> result = confirmAlert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    @FXML
    private void handleUndo() {
        System.out.println("↶ Undoing changes");
        nameField.setText(originalName);
        descriptionField.setText(originalDescription != null ? originalDescription : "");
        colorField.setText(originalColor != null ? originalColor : DEFAULT_COLOR);
        validationLabel.setText("↶ Changes reverted to original");
        validationLabel.setStyle(SUCCESS_TEXT_COLOR);
        resetFieldStyles();
    }
    
    

    private FormData extractFormData() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String color = colorField.getText().trim().isEmpty() ? DEFAULT_COLOR : colorField.getText().trim();
        return new FormData(name, description, color);
    }
    
    private boolean hasActualChanges(FormData formData) {
        String origDesc = originalDescription != null ? originalDescription : "";
        String origColor = originalColor != null ? originalColor : DEFAULT_COLOR;
        
        boolean nameChanged = !formData.name.equals(originalName);
        boolean descChanged = !formData.description.equals(origDesc);
        boolean colorChanged = !formData.color.equals(origColor);
        
        if (nameChanged || descChanged || colorChanged) {
            System.out.println("Changes: Name=" + nameChanged + ", Desc=" + descChanged + ", Color=" + colorChanged);
            return true;
        }
        return false;
    }
    
    private void updateProjectWithFormData(FormData formData) {
        System.out.println("Saving project: " + formData.name);
        workingProject.setName(formData.name);
        workingProject.setDescription(formData.description);
        workingProject.setColor(formData.color);
        
        projectService.updateProject(workingProject);
        
        originalProject.setName(formData.name);
        originalProject.setDescription(formData.description);
        originalProject.setColor(formData.color);
    }
    
    private boolean hasChanges() {
        return hasActualChanges(extractFormData());
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        switch (type) {
            case ERROR -> UIUtils.showError(title, message);
            case INFORMATION -> UIUtils.showSuccess(title, message);
            case WARNING -> UIUtils.showWarning(title, message);
            default -> UIUtils.showConfirmation(title, message);
        }
    }
}

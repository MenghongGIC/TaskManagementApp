package com.taskmanagement.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.List;

import com.taskmanagement.model.Project;
import com.taskmanagement.service.ProjectService;
import com.taskmanagement.utils.UIUtils;

public class ProjectListController {
    
    // Error Messages
    private static final String ERROR_LOADING_PROJECTS = "Error loading projects: ";
    
    // Messages
    private static final String MSG_EMPTY_FILTER = "Showing all projects";
    
    @FXML private TextField searchField; 
    @FXML private TableView<Project> projectsTable;
    @FXML private TableColumn<Project, String> nameColumn;
    @FXML private TableColumn<Project, String> descriptionColumn;
    @FXML private TableColumn<Project, ?> createdByColumn;
    @FXML private TableColumn<Project, ?> tasksCountColumn;
    
    private ProjectService projectService;
    private ObservableList<Project> projectsList;
    private FilteredList<Project> filteredList;

    @FXML
    public void initialize() {
        projectService = new ProjectService();
        setupUI();
        loadProjects();
    }
    
    private void setupUI() {
        setupSearchListener();
    }
    
    private void loadProjects() {
        try {
            List<Project> projects = projectService.getAllProjects();
            projectsList = FXCollections.observableArrayList(projects);
            filteredList = new FilteredList<>(projectsList, p -> true);
            projectsTable.setItems(filteredList);
        } catch (Exception e) {
            showErrorAlert(ERROR_LOADING_PROJECTS, e);
        }
    }
    
    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> 
            filterProjects(newValue)
        );
    }
    
    private void filterProjects(String searchText) {
        if (isNullOrEmpty(searchText)) {
            filteredList.setPredicate(p -> true);
        } else {
            filteredList.setPredicate(createProjectPredicate(searchText));
        }
    }
    
    private java.util.function.Predicate<Project> createProjectPredicate(String searchText) {
        String lowerCaseFilter = searchText.toLowerCase();
        return project -> 
            project.getName().toLowerCase().contains(lowerCaseFilter) ||
            (project.getDescription() != null && 
             project.getDescription().toLowerCase().contains(lowerCaseFilter));
    }
    
    @FXML
    private void clearSearch() {
        searchField.clear();
        filteredList.setPredicate(p -> true);
    }
    
    private boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }
    
    private void showErrorAlert(String message, Exception e) {
        System.err.println(message + e.getMessage());
        e.printStackTrace();
        UIUtils.showError("Error", message + e.getMessage());
    }
}

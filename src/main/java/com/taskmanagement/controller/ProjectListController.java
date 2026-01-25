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

public class ProjectListController {
    
    @FXML
    private TextField searchField;
    
    @FXML
    private TableView<Project> projectsTable;
    
    @FXML
    private TableColumn<Project, String> nameColumn;
    
    @FXML
    private TableColumn<Project, String> descriptionColumn;
    
    @FXML
    private TableColumn<Project, ?> createdByColumn;
    
    @FXML
    private TableColumn<Project, ?> tasksCountColumn;
    
    private ProjectService projectService;
    private ObservableList<Project> projectsList;
    private FilteredList<Project> filteredList;

    @FXML
    public void initialize() {
        projectService = new ProjectService();
        loadProjects();
        setupSearchListener();
    }
    
    private void loadProjects() {
        try {
            List<Project> projects = projectService.getAllProjects();
            projectsList = FXCollections.observableArrayList(projects);
            filteredList = new FilteredList<>(projectsList, p -> true);
            projectsTable.setItems(filteredList);
        } catch (Exception e) {
            System.err.println("Error loading projects: " + e.getMessage());
        }
    }
    
    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProjects(newValue);
        });
    }
    
    private void filterProjects(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredList.setPredicate(p -> true);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            filteredList.setPredicate(project -> 
                project.getName().toLowerCase().contains(lowerCaseFilter) ||
                (project.getDescription() != null && project.getDescription().toLowerCase().contains(lowerCaseFilter))
            );
        }
    }
    
    @FXML
    private void clearSearch() {
        searchField.clear();
        filteredList.setPredicate(p -> true);
    }
}

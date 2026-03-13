package com.taskmanagement.controller;

import com.taskmanagement.model.ActivityLog;
import com.taskmanagement.service.ActivityLogService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ActivityLogController {

    @FXML private TableView<ActivityLog> activityTable;
    @FXML private TableColumn<ActivityLog, Long> idColumn;
    @FXML private TableColumn<ActivityLog, String> actionColumn;
    @FXML private TableColumn<ActivityLog, String> entityTypeColumn;
    @FXML private TableColumn<ActivityLog, String> entityNameColumn;
    @FXML private TableColumn<ActivityLog, String> userColumn;
    @FXML private TableColumn<ActivityLog, String> detailsColumn;
    @FXML private TableColumn<ActivityLog, String> timestampColumn;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        setupTableColumns();
        loadActivities();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        entityTypeColumn.setCellValueFactory(new PropertyValueFactory<>("entityType"));
        entityNameColumn.setCellValueFactory(new PropertyValueFactory<>("entityName"));
        
        // Custom cell factory for user column
        userColumn.setCellValueFactory(cellData -> {
            ActivityLog log = cellData.getValue();
            String username = log.getUser() != null ? log.getUser().getUsername() : "System";
            return new javafx.beans.property.SimpleStringProperty(username);
        });
        
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
        
        // Custom cell factory for timestamp column
        timestampColumn.setCellValueFactory(cellData -> {
            ActivityLog log = cellData.getValue();
            String formatted = log.getTimestamp().format(formatter);
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
    }

    private void loadActivities() {
        try {
            List<ActivityLog> activities = ActivityLogService.getAllActivities();
            ObservableList<ActivityLog> observableActivities = FXCollections.observableArrayList(activities);
            activityTable.setItems(observableActivities);
        } catch (Exception e) {
            System.err.println("Error loading activity logs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshActivities() {
        loadActivities();
    }

    @FXML
    private void clearAllActivities() {
        try {
            ActivityLogService.clearActivities();
            loadActivities();
        } catch (Exception e) {
            System.err.println("Error clearing activity logs: " + e.getMessage());
        }
    }
}

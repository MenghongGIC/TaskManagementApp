package com.taskmanagement.controller;

import com.taskmanagement.model.ActivityLog;
import com.taskmanagement.service.ActivityLogService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;

public class ActivityController {

    @FXML private TableView<ActivityLog> activityTable;
    @FXML private TableColumn<ActivityLog, String> timeColumn;
    @FXML private TableColumn<ActivityLog, String> actionColumn;
    @FXML private TableColumn<ActivityLog, String> entityColumn;
    @FXML private TableColumn<ActivityLog, String> nameColumn;
    @FXML private TableColumn<ActivityLog, String> actorColumn;
    @FXML private TableColumn<ActivityLog, String> detailsColumn;
    @FXML private Label statusLabel;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        timeColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getTimestamp() == null ? "" : data.getValue().getTimestamp().format(FORMATTER)
        ));
        actionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAction()));
        entityColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEntityType()));
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEntityName()));
        actorColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getUser() == null ? "System" : data.getValue().getUser().getUsername()
        ));
        detailsColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getDetails() == null ? "" : data.getValue().getDetails()
        ));

        refresh();
    }

    @FXML
    private void refresh() {
        activityTable.getItems().setAll(ActivityLogService.getAllActivities());
        statusLabel.setText("Loaded " + activityTable.getItems().size() + " activity records");
    }

    @FXML
    private void clearAll() {
        ActivityLogService.clearActivities();
        refresh();
    }
}

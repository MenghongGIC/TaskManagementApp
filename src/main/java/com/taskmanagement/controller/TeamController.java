package com.taskmanagement.controller;

import com.taskmanagement.App;
import com.taskmanagement.model.Team;
import com.taskmanagement.service.TeamService;
import com.taskmanagement.utils.UIUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TeamController {

    @FXML private TableView<Team> teamTable;
    @FXML private TableColumn<Team, String> teamNameColumn;
    @FXML private TableColumn<Team, String> teamLeadColumn;
    @FXML private TableColumn<Team, String> memberCountColumn;
    @FXML private TableColumn<Team, String> createdDateColumn;
    @FXML private Label statusLabel;

    private final TeamService teamService = new TeamService();

    @FXML
    public void initialize() {
        teamNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTeamName()));
        teamLeadColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getTeamLead() != null ? data.getValue().getTeamLead().getUsername() : "-"
        ));
        memberCountColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getMemberCount())));
        createdDateColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getCreatedAt() != null ? data.getValue().getCreatedAt().toLocalDate().toString() : "-"
        ));

        loadTeams();

        teamTable.setRowFactory(tv -> {
            TableRow<Team> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openTeamDetails(row.getItem());
                }
            });
            return row;
        });
    }

    @FXML
    private void refreshTeams() {
        loadTeams();
    }

    @FXML
    private void handleCreateTeam() {
        openTeamForm(null);
    }

    @FXML
    private void handleEditTeam() {
        Team selected = teamTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtils.showWarning("No team selected", "Select a team first");
            return;
        }
        openTeamForm(selected);
    }

    @FXML
    private void handleViewTeam() {
        Team selected = teamTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtils.showWarning("No team selected", "Select a team first");
            return;
        }
        openTeamDetails(selected);
    }

    @FXML
    private void handleRemoveTeam() {
        Team selected = teamTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtils.showWarning("No team selected", "Select a team first");
            return;
        }

        boolean confirmed = UIUtils.showCustomConfirmation(
                "Delete Team",
                "Are you sure you want to delete this team?",
                selected.getTeamName()
        );
        if (!confirmed) {
            return;
        }

        try {
            teamService.deleteTeam(selected.getTeamId());
            loadTeams();
        } catch (Exception e) {
            UIUtils.showError("Delete failed", e.getMessage());
        }
    }

    private void loadTeams() {
        try {
            teamTable.getItems().setAll(teamService.getAllTeams());
            statusLabel.setText("Loaded " + teamTable.getItems().size() + " teams");
        } catch (Exception e) {
            teamTable.getItems().clear();
            statusLabel.setText("Failed to load teams: " + e.getMessage());
        }
    }

    private void openTeamForm(Team team) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/taskmanagement/fxml/team/TeamForm.fxml"));
            VBox root = loader.load();
            TeamFormController controller = loader.getController();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(team == null ? "Create Team" : "Edit Team");
            dialog.setScene(new Scene(root, 640, 560));

            controller.setDialogStage(dialog);
            controller.setOnSaved(this::loadTeams);
            controller.setTeam(team);

            dialog.showAndWait();
        } catch (Exception e) {
            UIUtils.showError("Error", "Failed to open team form: " + e.getMessage());
        }
    }

    private void openTeamDetails(Team team) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/taskmanagement/fxml/team/TeamDetailView.fxml"));
            VBox root = loader.load();
            TeamDetailController controller = loader.getController();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Team Details");
            dialog.setScene(new Scene(root, 620, 520));

            controller.setDialogStage(dialog);
            controller.setOnTeamChanged(this::loadTeams);
            controller.loadTeam(team);

            dialog.showAndWait();
        } catch (Exception e) {
            UIUtils.showError("Error", "Failed to open team details: " + e.getMessage());
        }
    }
}

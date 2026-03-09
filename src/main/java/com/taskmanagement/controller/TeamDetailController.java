package com.taskmanagement.controller;

import com.taskmanagement.model.Team;
import com.taskmanagement.model.User;
import com.taskmanagement.service.TeamService;
import com.taskmanagement.utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class TeamDetailController {

    @FXML private Label teamNameLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label teamLeadLabel;
    @FXML private Label createdDateLabel;
    @FXML private Label memberCountLabel;
    @FXML private ListView<String> membersList;

    private final TeamService teamService = new TeamService();
    private Team team;
    private Stage dialogStage;
    private Runnable onTeamChanged;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setOnTeamChanged(Runnable onTeamChanged) {
        this.onTeamChanged = onTeamChanged;
    }

    public void loadTeam(Team team) {
        this.team = team;
        if (team == null) {
            return;
        }

        teamNameLabel.setText(team.getTeamName());
        descriptionLabel.setText(team.getDescription() == null || team.getDescription().isBlank() ? "-" : team.getDescription());
        teamLeadLabel.setText(team.getTeamLead() != null ? team.getTeamLead().getUsername() : "-");
        createdDateLabel.setText(team.getCreatedAt() != null ? team.getCreatedAt().toLocalDate().toString() : "-");
        memberCountLabel.setText(String.valueOf(team.getMemberCount()));
        membersList.getItems().setAll(team.getMembers().stream().map(User::getUsername).toList());
    }

    @FXML
    private void handleRemoveTeam() {
        if (team == null) {
            return;
        }

        boolean confirmed = UIUtils.showCustomConfirmation(
                "Delete Team",
                "Are you sure you want to delete this team?",
                team.getTeamName()
        );

        if (!confirmed) {
            return;
        }

        try {
            teamService.deleteTeam(team.getTeamId());
            UIUtils.showSuccess("Team deleted", team.getTeamName());
            if (onTeamChanged != null) {
                onTeamChanged.run();
            }
            closeDialog();
        } catch (Exception e) {
            UIUtils.showError("Delete failed", e.getMessage());
        }
    }

    @FXML
    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}

package com.taskmanagement.controller;

import com.taskmanagement.model.Team;
import com.taskmanagement.model.User;
import com.taskmanagement.service.TeamService;
import com.taskmanagement.utils.UIUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeamFormController {

    @FXML private TextField teamNameField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<User> teamLeadCombo;
    @FXML private ListView<User> membersList;

    private final TeamService teamService = new TeamService();

    private Team team;
    private Stage dialogStage;
    private Runnable onSaved;

    @FXML
    public void initialize() {
        List<User> users = teamService.getAvailableUsers();
        teamLeadCombo.setItems(FXCollections.observableArrayList(users));
        membersList.setItems(FXCollections.observableArrayList(users));
        membersList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    public void setTeam(Team team) {
        this.team = team;
        if (team == null) {
            return;
        }
        teamNameField.setText(team.getTeamName());
        descriptionField.setText(team.getDescription());
        teamLeadCombo.getSelectionModel().select(team.getTeamLead());

        for (User member : team.getMembers()) {
            membersList.getSelectionModel().select(member);
        }
    }

    @FXML
    private void handleCreateTeam() {
        try {
            User lead = teamLeadCombo.getValue();
            List<Long> memberIds = selectedMemberIds();
            Team created = teamService.createTeam(
                    teamNameField.getText(),
                    descriptionField.getText(),
                    lead != null ? lead.getId() : null,
                    memberIds
            );
            UIUtils.showSuccess("Team created", created.getTeamName());
            if (onSaved != null) {
                onSaved.run();
            }
            close();
        } catch (Exception e) {
            UIUtils.showError("Create team failed", e.getMessage());
        }
    }

    @FXML
    private void handleUpdateTeam() {
        if (team == null || team.getTeamId() == null) {
            UIUtils.showWarning("No team", "Select a team to update");
            return;
        }

        try {
            User lead = teamLeadCombo.getValue();
            teamService.updateTeam(
                    team.getTeamId(),
                    teamNameField.getText(),
                    descriptionField.getText(),
                    lead != null ? lead.getId() : null,
                    selectedMemberIds()
            );
            UIUtils.showSuccess("Team updated", teamNameField.getText());
            if (onSaved != null) {
                onSaved.run();
            }
            close();
        } catch (Exception e) {
            UIUtils.showError("Update team failed", e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        close();
    }

    private List<Long> selectedMemberIds() {
        List<Long> ids = new ArrayList<>();
        for (User user : membersList.getSelectionModel().getSelectedItems()) {
            ids.add(user.getId());
        }

        User lead = teamLeadCombo.getValue();
        if (lead != null && !ids.contains(lead.getId())) {
            ids.add(lead.getId());
        }

        return ids;
    }

    private void close() {
        Optional.ofNullable(dialogStage).ifPresent(Stage::close);
    }
}

package com.taskmanagement.controller;

import com.taskmanagement.model.Project;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.Optional;

class DialogProjectForm {

    Optional<ProjectPayload> open(String title, Project existing) {
        Dialog<ProjectPayload> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nameField = new TextField(existing == null ? "" : existing.getName());
        TextArea descriptionField = new TextArea(existing == null ? "" : existing.getDescription());
        TextField colorField = new TextField(existing == null ? "#3498db" : existing.getColor());

        nameField.setPromptText("Project name");
        descriptionField.setPromptText("Description");
        colorField.setPromptText("Hex color");

        VBox box = new VBox(10,
                new Label("Name"), nameField,
                new Label("Description"), descriptionField,
                new Label("Color"), colorField
        );
        box.setPrefWidth(360);
        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return new ProjectPayload(nameField.getText(), descriptionField.getText(), colorField.getText());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    record ProjectPayload(String name, String description, String color) {}
}

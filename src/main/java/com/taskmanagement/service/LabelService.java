package com.taskmanagement.service;

import java.util.List;

import com.taskmanagement.model.Label;
import com.taskmanagement.utils.CurrentUser;

public class LabelService {

    public LabelService() {
    }

    public Label createLabel(String name, String color) {
        if (!CurrentUser.isLoggedIn()) {
            throw new SecurityException("User must be logged in");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Label name is required");
        }

        Label label = new Label(name.trim(), color);
        return label;
    }

    public Label createLabel(String name) {
        return createLabel(name, "#007BFF");
    }

    public List<Label> getCommonLabels() {
        return List.of(
                new Label("Bug", "#DC3545"),
                new Label("Feature", "#28A745"),
                new Label("Documentation", "#6F42C1"),
                new Label("Enhancement", "#FFC107"),
                new Label("Urgent", "#E83E8C"),
                new Label("Blocked", "#6C757D"),
                new Label("In Review", "#17A2B8"),
                new Label("On Hold", "#FD7E14")
        );
    }

    public List<Label> getColorOptions() {
        return List.of(
                new Label("Red", "#DC3545"),
                new Label("Green", "#28A745"),
                new Label("Blue", "#007BFF"),
                new Label("Purple", "#6F42C1"),
                new Label("Yellow", "#FFC107"),
                new Label("Pink", "#E83E8C"),
                new Label("Gray", "#6C757D"),
                new Label("Cyan", "#17A2B8"),
                new Label("Orange", "#FD7E14")
        );
    }

    public Label findLabelByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        return getCommonLabels().stream()
                .filter(l -> l.getName().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElse(null);
    }

    public boolean isValidColor(String color) {
        return color != null && color.matches("^#[0-9A-Fa-f]{6}$");
    }
}

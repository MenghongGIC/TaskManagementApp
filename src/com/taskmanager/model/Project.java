package com.taskmanager.model;

import java.util.Objects;

//Project model class for Task Management App

public class Project {
    private int id;
    private String name;
    private String description;
    private String color;           // Hex color like "#FF5733", "#3498DB"
    private int workspaceId;

    public Project() {}

    public Project(String name, String description, String color, int workspaceId) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.workspaceId = workspaceId;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(int workspaceId) {
        this.workspaceId = workspaceId;
    }

    // ==================== Smart Helper Methods ====================

    /**
     * Returns the color if valid, otherwise a default gray
     * Prevents null/empty color crashes in UI
     */
    public String getColorOrDefault() {
        if (color == null || color.trim().isEmpty() || !color.matches("^#[0-9A-Fa-f]{6}$")) {
            return "#6C757D"; // Bootstrap secondary gray
        }
        return color;
    }

    /**
     * Returns true if project has a valid name
     */
    public boolean isValid() {
        return name != null && !name.trim().isEmpty();
    }

    /**
     * Returns display name with color indicator (for combo boxes, lists)
     */
    public String getDisplayName() {
        return name + "  " + getColorOrDefault();
    }

    
    @Override
    public String toString() {
        return name + " (ID: " + id + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project project)) return false;
        return id == project.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
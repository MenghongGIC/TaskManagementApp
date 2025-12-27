package com.taskmanagement.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.taskmanagement.utils.CurrentUser;

public class Project {
    private Long id;
    private String name;
    private String description;
    private String color;
    private User createdBy;
    private Team team;
    private LocalDateTime createdAt;
    private Set<Task> tasks = new HashSet<>();

    public Project() {
        this.createdAt = LocalDateTime.now();
    }

    public Project(String name, User createdBy) {
        this();
        this.name = name;
        this.createdBy = createdBy;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name != null ? name.trim() : null; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<Task> getTasks() {
        return Collections.unmodifiableSet(tasks);
    }

    public void addTask(Task task) {
        if (task != null) {
            tasks.add(task);
            task.setProject(this);
        }
    }

    public int getTaskCount() { return tasks.size(); }

    public String getColorOrDefault() {
        if (color == null || !color.matches("^#[0-9A-Fa-f]{6}$")) {
            return "#6C757D";
        }
        return color.toUpperCase();
    }

    public boolean canEdit() {
        if (!CurrentUser.isLoggedIn()) return false;
        if (CurrentUser.isAdmin()) return true;
        if (createdBy != null && createdBy.getId().equals(CurrentUser.getId())) return true;
        if (team != null && team.isMemberCurrentUser()) return true;
        return false;
    }
    public boolean canDelete() {
        if (!CurrentUser.isLoggedIn()) return false;
        if (CurrentUser.isAdmin()) return true;
        if (createdBy != null && createdBy.getId().equals(CurrentUser.getId())) return true;
        if (team != null && team.isMemberCurrentUser()) return true;
        return false;
    }


    @Override
    public String toString() {
        return name + (team != null ? " [" + team.getName() + "]" : "") + " (" + getTaskCount() + " tasks)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project project)) return false;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
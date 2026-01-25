package com.taskmanagement.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.taskmanagement.utils.ColorValidator;
import com.taskmanagement.utils.PermissionChecker;

public class Project {
    private Long id;
    private String name;
    private String description;
    private String color;
    private User createdBy;
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
        return ColorValidator.getProjectColorOrDefault(color);
    }

    public boolean canEdit() {
        return PermissionChecker.canEditProject(this);
    }
    
    public boolean canDelete() {
        return PermissionChecker.canDeleteProject(this);
    }


    @Override
    public String toString() {
        return name + " (" + getTaskCount() + " tasks)";
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
package com.taskmanagement.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.taskmanagement.utils.DateUtils;
import com.taskmanagement.utils.PermissionChecker;

public class Task {
    private Long id;
    private String title;
    private String description;
    private String status = "To Do";
    private String priority = "Medium";
    private LocalDate dueDate;
    private Project project;
    private User assignee;
    private User createdBy;
    private LocalDateTime createdAt = LocalDateTime.now();
    private Set<Label> labels = new HashSet<>();

    public Task() {}

    public Task(String title, Project project, User createdBy) {
        this.title = title;
        this.project = project;
        this.createdBy = createdBy;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title != null ? title.trim() : null; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public User getAssignee() { return assignee; }
    public void setAssignee(User assignee) { this.assignee = assignee; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<Label> getLabels() { return Collections.unmodifiableSet(labels); }
    public void addLabel(Label label) { if (label != null) labels.add(label); }

    // UI & Logic Helpers
    public boolean isOverdue() { return DateUtils.isOverdue(dueDate); }
    public boolean isDueToday() { return DateUtils.isToday(dueDate); }
    public String getDueDateLabel() { return DateUtils.getSmartDateLabel(dueDate); }
    public boolean isCompleted() { return "Done".equalsIgnoreCase(status); }
    public boolean isUnassigned() { return assignee == null; }

    public boolean canEdit() {
        return PermissionChecker.canEditTask(this);
    }
    
    public boolean canDelete() {
        return PermissionChecker.canDeleteTask(this);
    }

    @Override
    public String toString() {
        return title + " [" + status + "] " + getDueDateLabel() + (isOverdue() ? " (OVERDUE)" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
package com.taskmanager.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

//Task model class for Task Management App
public class Task {
    private int id;
    private String title;
    private String description;
    private String status;           // "To Do", "In Progress", "Done"
    private String priority;         // "Low", "Medium", "High", "Critical"
    private LocalDate dueDate;
    private int projectId;
    private Integer assigneeId;      // Integer so it can be null (unassigned)
    private LocalDateTime createdAt; // Automatically set by database


    public Task() {}
    public Task(String title, String description, String status, String priority, LocalDate dueDate, int projectId, Integer assigneeId) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.projectId = projectId;
        this.assigneeId = assigneeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public Integer getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Integer assigneeId) {
        this.assigneeId = assigneeId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ==================== Smart Helper Methods ====================
    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDate.now());
    }

    public boolean isCompleted() {
        return "Done".equalsIgnoreCase(status);
    }

    public boolean isUnassigned() {
        return assigneeId == null;
    }

    public boolean isHighPriority() {
        return "High".equalsIgnoreCase(priority) || "Critical".equalsIgnoreCase(priority);
    }

    // ==================== Display Methods ====================
    @Override
    public String toString() {
        return String.format("%s [%s] - Due: %s%s",
                title,
                status,
                dueDate != null ? dueDate.toString() : "No due date",
                isOverdue() ? " (OVERDUE!)" : "");
    }

    public String getStatusWithIcon() {
        return switch (status.toLowerCase()) {
            case "done"        -> "Completed";
            case "in progress" -> "In Progress";
            case "to do"       -> "To Do";
            default            -> status;
        };
    }

    public String getPriorityColor() {
        return switch (priority.toLowerCase()) {
            case "critical" -> "Critical";
            case "high"     -> "High";
            case "medium"   -> "Medium";
            default         -> "Low";
        };
    }
}
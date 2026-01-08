package com.taskmanagement.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.taskmanagement.utils.DateUtils;
import com.taskmanagement.utils.CurrentUser;
public class Comment {
    private Long id;
    private Task task;           // Full Task object (better than just taskId)
    private User author;         // Full User object (better than just authorId)
    private String message;
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public Comment() {}

    public Comment(Task task, User author, String message) {
        this.task = task;
        this.author = author;
        this.message = message.trim();
    }

    public Comment(Long id, Task task, User author, String message, LocalDateTime createdAt) {
        this.id = id;
        this.task = task;
        this.author = author;
        this.message = message;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Long getTaskId() {
        return task != null ? task.getId() : null;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Long getAuthorId() {
        return author != null ? author.getId() : null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message != null ? message.trim() : null;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFormattedDate() {
        return DateUtils.formatDateTime(createdAt);
    }

    public String getRelativeTime() {
        return DateUtils.getRelativeTime(createdAt);
    }

    public String getShortRelativeTime() {
        return DateUtils.getShortRelativeTime(createdAt);
    }

    // === Convenience Methods ===

    public boolean isValid() {
        return message != null && !message.isEmpty();
    }

    public boolean isAuthoredByCurrentUser() {
        return author != null && CurrentUser.isLoggedIn() && author.getId().equals(CurrentUser.getId());
    }

    // === toString for debugging / lists ===
    @Override
    public String toString() {
        String authorName = author != null ? author.getUsername() : "Unknown";
        return String.format("%s: %s (%s)", authorName, message, getRelativeTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment comment)) return false;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
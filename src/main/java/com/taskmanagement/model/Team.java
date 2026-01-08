package com.taskmanagement.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import com.taskmanagement.utils.CurrentUser;
public class Team {
    private Long id;
    private String name;
    private String description;
    private User createdBy;
    private LocalDateTime createdAt;
    final private Set<User> members = new HashSet<>();

    public Team() {
        this.createdAt = LocalDateTime.now();
    }

    public Team(String name, User createdBy) {
        this();
        this.name = name;
        this.createdBy = createdBy;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<User> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public boolean addMember(User user) {
        return user != null && members.add(user);
    }

    public boolean removeMember(User user) {
        return user != null && members.remove(user);
    }

    public boolean isMemberCurrentUser() {
        return CurrentUser.isLoggedIn() &&
               members.contains(CurrentUser.getInstance());
    }

    public int getMemberCount() { return members.size(); }

    @Override
    public String toString() {
        return name + " (" + getMemberCount() + " members)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team team)) return false;
        return Objects.equals(id, team.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
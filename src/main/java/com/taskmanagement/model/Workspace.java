package com.taskmanagement.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Workspace {
    private Long workspaceId;
    private String workspaceName;
    private String description;
    private User owner;
    private final Set<User> members = new LinkedHashSet<>();
    private final Set<Project> projects = new LinkedHashSet<>();
    private final Set<Team> teams = new LinkedHashSet<>();
    private LocalDateTime createdAt = LocalDateTime.now();

    public Workspace() {
    }

    public Workspace(String workspaceName, String description, User owner) {
        this.workspaceName = workspaceName;
        this.description = description;
        this.owner = owner;
        if (owner != null) {
            this.members.add(owner);
        }
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName != null ? workspaceName.trim() : null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
        if (owner != null) {
            this.members.add(owner);
        }
    }

    public Set<User> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public void setMembers(Set<User> members) {
        this.members.clear();
        if (members != null) {
            this.members.addAll(members);
        }
        if (owner != null) {
            this.members.add(owner);
        }
    }

    public void addMember(User member) {
        if (member != null) {
            members.add(member);
        }
    }

    public void removeMember(User member) {
        if (member == null) {
            return;
        }
        if (owner != null && Objects.equals(owner.getId(), member.getId())) {
            return;
        }
        members.remove(member);
    }

    public Set<Project> getProjects() {
        return Collections.unmodifiableSet(projects);
    }

    public void addProject(Project project) {
        if (project != null) {
            projects.add(project);
        }
    }

    public Set<Team> getTeams() {
        return Collections.unmodifiableSet(teams);
    }

    public void addTeam(Team team) {
        if (team != null) {
            teams.add(team);
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return workspaceName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Workspace that)) return false;
        return Objects.equals(workspaceId, that.workspaceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workspaceId);
    }
}

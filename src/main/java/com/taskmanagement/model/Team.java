package com.taskmanagement.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Team {
    private Long teamId;
    private String teamName;
    private String description;
    private User teamLead;
    private final Set<User> members = new LinkedHashSet<>();
    private LocalDateTime createdAt = LocalDateTime.now();

    public Team() {
    }

    public Team(String teamName, String description, User teamLead) {
        this.teamName = teamName;
        this.description = description;
        this.teamLead = teamLead;
        if (teamLead != null) {
            this.members.add(teamLead);
        }
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName != null ? teamName.trim() : null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getTeamLead() {
        return teamLead;
    }

    public void setTeamLead(User teamLead) {
        this.teamLead = teamLead;
        if (teamLead != null) {
            this.members.add(teamLead);
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
        if (teamLead != null) {
            this.members.add(teamLead);
        }
    }

    public void addMember(User member) {
        if (member != null) {
            this.members.add(member);
        }
    }

    public void removeMember(User member) {
        if (member == null) {
            return;
        }
        if (teamLead != null && Objects.equals(teamLead.getId(), member.getId())) {
            return;
        }
        this.members.remove(member);
    }

    public int getMemberCount() {
        return this.members.size();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return teamName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team team)) return false;
        return Objects.equals(teamId, team.teamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId);
    }
}

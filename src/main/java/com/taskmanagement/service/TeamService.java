package com.taskmanagement.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.taskmanagement.model.Team;
import com.taskmanagement.model.User;
import com.taskmanagement.repository.TeamRepository;
import com.taskmanagement.utils.CurrentUser;

public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService() {
        this.teamRepository = new TeamRepository();
    }

    public Team createTeam(String name, String description) {
        if (!CurrentUser.canManageTeams()) {
            throw new SecurityException("You don't have permission to create teams");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Team name is required");
        }

        User currentUser = CurrentUser.getInstance();
        if (currentUser == null) {
            throw new IllegalStateException("No user logged in");
        }

        Team team = new Team(name.trim(), currentUser);
        team.setDescription(description);
        team.setCreatedAt(LocalDateTime.now());
        team.addMember(currentUser);

        return teamRepository.save(team);
    }

    public Team getTeamById(Long id) {
        Team team = teamRepository.findById(id);
        if (team == null) return null;

        if (!canViewTeam(team)) {
            throw new SecurityException("You don't have permission to view this team");
        }
        return team;
    }

    public List<Team> getAllTeams() {
        List<Team> teams = teamRepository.findAll();
        return teams.stream()
                .filter(this::canViewTeam)
                .collect(Collectors.toList());
    }

    public List<Team> getTeamsByCreator(Long userId) {
        return teamRepository.findAll().stream()
                .filter(t -> t.getCreatedBy() != null && t.getCreatedBy().getId().equals(userId))
                .filter(this::canViewTeam)
                .collect(Collectors.toList());
    }

    public List<Team> getTeamsMemberOf() {
        if (!CurrentUser.isLoggedIn()) {
            return List.of();
        }

        User current = CurrentUser.getInstance();
        return teamRepository.findAll().stream()
                .filter(t -> t.getMembers().contains(current))
                .collect(Collectors.toList());
    }

    public Team updateTeam(Team updatedTeam) {
        Team existing = getTeamById(updatedTeam.getId());
        if (existing == null || !canEditTeam(existing)) {
            throw new SecurityException("You don't have permission to edit this team");
        }

        existing.setName(updatedTeam.getName());
        existing.setDescription(updatedTeam.getDescription());

        return teamRepository.save(existing);
    }

    public void deleteTeam(Long id) {
        Team team = getTeamById(id);
        if (team == null || !canDeleteTeam(team)) {
            throw new SecurityException("You don't have permission to delete this team");
        }

        teamRepository.delete(id);
    }

    public void addMemberToTeam(Long teamId, Long userId) {
        Team team = getTeamById(teamId);
        if (team == null || !canEditTeam(team)) {
            throw new SecurityException("You don't have permission to manage this team");
        }

        User user = new User();
        user.setId(userId);
        team.addMember(user);
        teamRepository.save(team);
    }

    public void removeMemberFromTeam(Long teamId, Long userId) {
        Team team = getTeamById(teamId);
        if (team == null || !canEditTeam(team)) {
            throw new SecurityException("You don't have permission to manage this team");
        }

        User user = new User();
        user.setId(userId);
        team.removeMember(user);
        teamRepository.save(team);
    }

    public int getMemberCount(Long teamId) {
        Team team = getTeamById(teamId);
        return team != null ? team.getMemberCount() : 0;
    }

    public boolean isMemberOfTeam(Long teamId, Long userId) {
        Team team = getTeamById(teamId);
        if (team == null) return false;

        return team.getMembers().stream()
                .anyMatch(m -> m.getId().equals(userId));
    }

    private boolean canViewTeam(Team team) {
        if (!CurrentUser.isLoggedIn()) return false;
        if (CurrentUser.isAdmin()) return true;

        User current = CurrentUser.getInstance();
        return team.getMembers().contains(current) ||
               (team.getCreatedBy() != null && team.getCreatedBy().getId().equals(current.getId()));
    }

    private boolean canEditTeam(Team team) {
        if (!CurrentUser.isLoggedIn()) return false;
        if (!CurrentUser.canManageTeams()) return false;
        if (CurrentUser.isAdmin()) return true;

        User current = CurrentUser.getInstance();
        return team.getCreatedBy() != null && team.getCreatedBy().getId().equals(current.getId());
    }

    private boolean canDeleteTeam(Team team) {
        if (!CurrentUser.isLoggedIn()) return false;
        if (CurrentUser.isAdmin()) return true;

        return canEditTeam(team);
    }
}

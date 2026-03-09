package com.taskmanagement.service;

import com.taskmanagement.model.Team;
import com.taskmanagement.model.User;
import com.taskmanagement.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class TeamService {
    private static final List<Team> teams = new ArrayList<>();
    private static final AtomicLong idGenerator = new AtomicLong(1L);

    private final UserRepository userRepository;

    public TeamService() {
        this.userRepository = new UserRepository();
    }

    public List<Team> getAllTeams() {
        return new ArrayList<>(teams);
    }

    public Team getTeamById(Long teamId) {
        if (teamId == null) {
            return null;
        }
        return teams.stream()
                .filter(t -> teamId.equals(t.getTeamId()))
                .findFirst()
                .orElse(null);
    }

    public Team createTeam(String teamName, String description, Long teamLeadId, List<Long> memberIds) {
        validateTeamName(teamName);

        User teamLead = findUser(teamLeadId);
        Team team = new Team(teamName.trim(), description, teamLead);
        team.setTeamId(idGenerator.getAndIncrement());
        team.setCreatedAt(LocalDateTime.now());

        Set<User> members = resolveMembers(memberIds);
        if (teamLead != null) {
            members.add(teamLead);
        }
        team.setMembers(members);

        teams.add(team);
        ActivityLogService.logActivity("TEAM_CREATED", "TEAM", team.getTeamId(), team.getTeamName());
        return team;
    }

    public Team updateTeam(Long teamId, String teamName, String description, Long teamLeadId, List<Long> memberIds) {
        Team existing = requireTeam(teamId);
        validateTeamName(teamName);

        User teamLead = findUser(teamLeadId);
        Set<User> members = resolveMembers(memberIds);
        if (teamLead != null) {
            members.add(teamLead);
        }

        existing.setTeamName(teamName);
        existing.setDescription(description);
        existing.setTeamLead(teamLead);
        existing.setMembers(members);

        ActivityLogService.logActivity("TEAM_UPDATED", "TEAM", existing.getTeamId(), existing.getTeamName());
        return existing;
    }

    public void deleteTeam(Long teamId) {
        Team existing = requireTeam(teamId);
        teams.remove(existing);
        ActivityLogService.logActivity("TEAM_DELETED", "TEAM", existing.getTeamId(), existing.getTeamName());
    }

    public List<User> getAvailableUsers() {
        return userRepository.findAll();
    }

    private Team requireTeam(Long teamId) {
        Team team = getTeamById(teamId);
        if (team == null) {
            throw new IllegalArgumentException("Team not found");
        }
        return team;
    }

    private void validateTeamName(String teamName) {
        if (teamName == null || teamName.trim().isEmpty()) {
            throw new IllegalArgumentException("Team name is required");
        }
    }

    private User findUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId);
    }

    private Set<User> resolveMembers(List<Long> memberIds) {
        Set<User> members = new LinkedHashSet<>();
        if (memberIds == null) {
            return members;
        }
        for (Long memberId : memberIds) {
            User member = findUser(memberId);
            if (member != null) {
                members.add(member);
            }
        }
        return members;
    }
}

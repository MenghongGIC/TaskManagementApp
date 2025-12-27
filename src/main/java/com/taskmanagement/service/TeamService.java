package com.taskmanagement.service;

import com.taskmanagement.repository.TeamRepository;

public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService() {
        this.teamRepository = new TeamRepository();
    }
}

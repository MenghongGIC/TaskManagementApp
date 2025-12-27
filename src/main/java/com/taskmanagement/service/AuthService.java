package com.taskmanagement.service;

import com.taskmanagement.repository.UserRepository;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService() {
        this.userRepository = new UserRepository();
    }
}

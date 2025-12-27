package com.taskmanagement.service;

import com.taskmanagement.repository.TaskRepository;

public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService() {
        this.taskRepository = new TaskRepository();
    }
}

package com.taskmanagement.service;

import com.taskmanagement.repository.CommentRepository;

public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService() {
        this.commentRepository = new CommentRepository();
    }
}

package com.taskmanager.model;
import java.time.LocalDateTime;
@SuppressWarnings("unused")

public class Comment {
    private int id;
    private int taskId;
    private int authorId;
    private String message;
    private LocalDateTime date;
}
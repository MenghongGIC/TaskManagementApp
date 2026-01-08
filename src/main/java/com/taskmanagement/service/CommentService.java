package com.taskmanagement.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.taskmanagement.model.Comment;
import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import com.taskmanagement.repository.CommentRepository;
import com.taskmanagement.utils.CurrentUser;

public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService() {
        this.commentRepository = new CommentRepository();
    }
    // add comment to task
    public Comment addComment(Long taskId, String message) {
        if (!CurrentUser.isLoggedIn()) {
            throw new SecurityException("User must be logged in to comment");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment message is required");
        }

        User author = CurrentUser.getInstance();
        Task task = new Task();
        task.setId(taskId);

        Comment comment = new Comment(task, author, message.trim());
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }
    // get comment by id
    public Comment getCommentById(Long id) {
        Comment comment = commentRepository.findById(id);
        if (comment != null && !canViewComment(comment)) {
            throw new SecurityException("You don't have permission to view this comment");
        }
        return comment;
    }
    
    public List<Comment> getCommentsByTask(Long taskId) {
        List<Comment> comments = commentRepository.findByTaskId(taskId);
        return comments.stream().filter(this::canViewComment)
                                .collect(Collectors.toList());
    }
    // update comment
    public Comment updateComment(Long commentId, String newMessage) {
        if (newMessage == null || newMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment message is required");
        }

        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("Comment not found");
        }

        if (!canEditComment(comment)) {
            throw new SecurityException("You don't have permission to edit this comment");
        }

        comment.setMessage(newMessage.trim());
        return commentRepository.update(comment);
    }
    // delete comment
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("Comment not found");
        }

        if (!canEditComment(comment)) {
            throw new SecurityException("You don't have permission to delete this comment");
        }

        commentRepository.delete(commentId);
    }

    public int getCommentCountByTask(Long taskId) {
        return commentRepository.findByTaskId(taskId).size();
    }

    public List<Comment> getRecentComments(int limit) {
        return commentRepository.findRecent(limit);
    }

    private boolean canViewComment(Comment comment) {
        if (!CurrentUser.isLoggedIn()) return false;
        if (CurrentUser.isAdmin()) return true;
        return true;
    }

    private boolean canEditComment(Comment comment) {
        if (!CurrentUser.isLoggedIn()) return false;
        if (CurrentUser.isAdmin()) return true;

        User current = CurrentUser.getInstance();
        return comment.getAuthor() != null && comment.getAuthor().getId().equals(current.getId());
    }
}

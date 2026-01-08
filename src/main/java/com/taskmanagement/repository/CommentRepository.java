package com.taskmanagement.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.taskmanagement.model.Comment;
import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;

public class CommentRepository extends BaseRepository {
    
    public Comment save(Comment comment) {
        String sql = """
            INSERT INTO Comments (content, task_id, author_id)
            VALUES (?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, comment.getMessage());
            pstmt.setLong(2, comment.getTask().getId());
            pstmt.setLong(3, comment.getAuthor().getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating comment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    comment.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating comment failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saving comment", e);
        }
        return comment;
    }
    public List<Comment> findByTaskId(Long taskId) {
        List<Comment> comments = new ArrayList<>();
        String sql = """
            SELECT c.*, 
                   u.id AS author_id, u.username AS author_name,
                   t.id AS task_id
            FROM Comments c
            JOIN Users u ON c.author_id = u.id
            JOIN Tasks t ON c.task_id = t.id
            WHERE c.task_id = ?
            ORDER BY c.created_at ASC
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, taskId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapRowToComment(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading comments for task ID: " + taskId, e);
        }
        return comments;
    }

    public void delete(Long commentId) {
        String sql = "DELETE FROM Comments WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, commentId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting comment ID: " + commentId, e);
        }
    }

    public Comment findById(Long commentId) {
        String sql = """
            SELECT c.*, 
                   u.id AS author_id, u.username AS author_name,
                   t.id AS task_id
            FROM Comments c
            JOIN Users u ON c.author_id = u.id
            JOIN Tasks t ON c.task_id = t.id
            WHERE c.id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, commentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToComment(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding comment ID: " + commentId, e);
        }
        return null;
    }

    public Comment update(Comment comment) {
        String sql = """
            UPDATE Comments 
            SET content = ?
            WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, comment.getMessage());
            pstmt.setLong(2, comment.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating comment ID: " + comment.getId(), e);
        }
        return comment;
    }

    public List<Comment> findRecent(int limit) {
        List<Comment> comments = new ArrayList<>();
        String sql = """
            SELECT c.*, 
                   u.id AS author_id, u.username AS author_name,
                   t.id AS task_id
            FROM Comments c
            JOIN Users u ON c.author_id = u.id
            JOIN Tasks t ON c.task_id = t.id
            ORDER BY c.created_at DESC
            LIMIT ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapRowToComment(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading recent comments", e);
        }
        return comments;
    }

    public void deleteByTaskId(Long taskId) {
        String sql = "DELETE FROM Comments WHERE task_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, taskId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting comments for task: " + taskId);
        }
    }

    private Comment mapRowToComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();

        comment.setId(rs.getLong("id"));
        comment.setMessage(rs.getString("content"));

        User author = new User();
        author.setId(rs.getLong("author_id"));
        author.setUsername(rs.getString("author_name"));
        comment.setAuthor(author);

        Task task = new Task();
        task.setId(rs.getLong("task_id"));
        comment.setTask(task);

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            comment.setCreatedAt(ts.toLocalDateTime());
        } else {
            comment.setCreatedAt(LocalDateTime.now());
        }

        return comment;
    }
}
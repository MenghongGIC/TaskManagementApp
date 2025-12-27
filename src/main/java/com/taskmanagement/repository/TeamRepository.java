package com.taskmanagement.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.taskmanagement.model.Team;
import com.taskmanagement.model.User;

public class TeamRepository extends BaseRepository {

    private final UserRepository userRepository = new UserRepository();

    public Team save(Team team) {
        String teamSql = """
            INSERT INTO Teams (name, description, created_by)
            VALUES (?, ?, ?)
            """;

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Save team
            try (PreparedStatement pstmt = conn.prepareStatement(teamSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, team.getName());
                pstmt.setString(2, team.getDescription());
                pstmt.setLong(3, team.getCreatedBy().getId());

                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        team.setId(rs.getLong(1));
                    }
                }
            }

            // Save members
            saveTeamMembers(conn, team);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try { 
                    conn.rollback(); 
                } catch (SQLException ex) { 
                    ex.printStackTrace(System.err); }
            }
            throw new RuntimeException("Error saving team: " + team.getName(), e);
        } finally {
            if (conn != null) {
                try { 
                    conn.setAutoCommit(true); conn.close(); 
                } catch (SQLException ex) { 
                    ex.printStackTrace(System.err); }
            }
        }
        return team;
    }

    public List<Team> findAll() {
        List<Team> teams = new ArrayList<>();
        String sql = """
            SELECT t.*, u.id AS creator_id, u.username AS creator_name
            FROM Teams t
            JOIN Users u ON t.created_by = u.id
            ORDER BY t.name
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                teams.add(mapRowToTeam(rs));
            }

            // Load members for each team
            for (Team team : teams) {
                loadTeamMembers(team);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading teams", e);
        }
        return teams;
    }

    public Team findById(Long id) {
        String sql = """
            SELECT t.*, u.id AS creator_id, u.username AS creator_name
            FROM Teams t
            JOIN Users u ON t.created_by = u.id
            WHERE t.id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Team team = mapRowToTeam(rs);
                    loadTeamMembers(team);
                    return team;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding team by ID: " + id, e);
        }
        return null;
    }

    public void delete(Long id) {
        String sql = "DELETE FROM Teams WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting team ID: " + id, e);
        }
    }

    public Team mapRowToTeam(ResultSet rs) throws SQLException {
        Team team = new Team();
        team.setId(rs.getLong("id"));
        team.setName(rs.getString("name"));
        team.setDescription(rs.getString("description"));

        User creator = new User();
        creator.setId(rs.getLong("creator_id"));
        creator.setUsername(rs.getString("creator_name"));
        team.setCreatedBy(creator);

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) team.setCreatedAt(ts.toLocalDateTime());

        return team;
    }

    public void loadTeamMembers(Team team) {
        String sql = """
            SELECT u.*
            FROM Users u
            JOIN TeamMembers tm ON u.id = tm.user_id
            WHERE tm.team_id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, team.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User member = userRepository.mapRowToUser(rs);
                    team.addMember(member);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to load members for team: " + team.getName());
        }
    }

    private void saveTeamMembers(Connection conn, Team team) throws SQLException {
        // Clear existing members
        String deleteSql = "DELETE FROM TeamMembers WHERE team_id = ?";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
            deleteStmt.setLong(1, team.getId());
            deleteStmt.executeUpdate();
        }

        // Insert current members
        String insertSql = "INSERT INTO TeamMembers (team_id, user_id) VALUES (?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            for (User member : team.getMembers()) {
                insertStmt.setLong(1, team.getId());
                insertStmt.setLong(2, member.getId());
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        }
    }
}
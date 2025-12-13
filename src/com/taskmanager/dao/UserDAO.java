package com.taskmanager.dao;

import com.taskmanager.database.*;
import com.taskmanager.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Login method (most used)
    public User findByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet resultset = ps.executeQuery();
            if (resultset.next()) {
                User user = new User();
                user.setId(resultset.getInt("id"));
                user.setName(resultset.getString("name"));
                user.setEmail(resultset.getString("email"));
                user.setRole(resultset.getString("role"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    // Get all users (for Manager to assign tasks)
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY name";

        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultset = statement.executeQuery(sql)) {

            while (resultset.next()) {
                User user = new User();
                user.setId(resultset.getInt("id"));
                user.setName(resultset.getString("name"));
                user.setEmail(resultset.getString("email"));
                user.setRole(resultset.getString("role"));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return users;
    }
}
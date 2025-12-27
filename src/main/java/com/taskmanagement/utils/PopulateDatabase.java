package com.taskmanagement.utils;

import java.sql.Connection;
import java.sql.Statement;

import com.taskmanagement.database.DBConnection;

public class PopulateDatabase {

    public static void main(String[] args) {
        System.out.println("=== Populating Database with Sample Data ===\n");
        
        try {
            DBConnection dbConnection = DBConnection.getInstance();
            Connection connection = dbConnection.getConnection();
            
            if (connection != null && !connection.isClosed()) {
                Statement stmt = connection.createStatement();
                
                // Admin user
                String adminUser = "INSERT INTO Users (username, password_hash, email, role) VALUES ('admin', 'admin12345', 'admin@gmail.com', 'ADMIN')";
                
                // Sample users
                String[] sampleUsers = {
                    "INSERT INTO Users (username, password_hash, email, role) VALUES ('john', 'password123', 'john@gmail.com', 'USER')",
                    "INSERT INTO Users (username, password_hash, email, role) VALUES ('jane', 'password123', 'jane@gmail.com', 'USER')",
                    "INSERT INTO Users (username, password_hash, email, role) VALUES ('bob', 'password123', 'bob@gmail.com', 'USER')"
                };
                
                // Labels
                String[] labels = {
                    "INSERT INTO Labels (name, color) VALUES ('Bug', '#DC3545')",
                    "INSERT INTO Labels (name, color) VALUES ('Feature', '#28A745')",
                    "INSERT INTO Labels (name, color) VALUES ('Urgent', '#FD7E14')",
                    "INSERT INTO Labels (name, color) VALUES ('Improvement', '#FFC107')",
                    "INSERT INTO Labels (name, color) VALUES ('Documentation', '#17A2B8')"
                };
                
                // Team
                String team = "INSERT INTO Teams (name, description, created_by) VALUES ('Development Team', 'Main development team', 1)";
                
                // Team members
                String[] teamMembers = {
                    "INSERT INTO TeamMembers (team_id, user_id) VALUES (1, 1)",
                    "INSERT INTO TeamMembers (team_id, user_id) VALUES (1, 2)",
                    "INSERT INTO TeamMembers (team_id, user_id) VALUES (1, 3)"
                };
                
                // Projects
                String[] projects = {
                    "INSERT INTO Projects (name, description, color, created_by, team_id) VALUES ('Task Management App', 'A comprehensive task management application', '#007BFF', 1, 1)",
                    "INSERT INTO Projects (name, description, color, created_by, team_id) VALUES ('Mobile App', 'Mobile application development project', '#28A745', 1, 1)",
                    "INSERT INTO Projects (name, description, color, created_by, team_id) VALUES ('Website Redesign', 'Redesigning the company website', '#FFC107', 1, 1)"
                };
                
                // Tasks
                String[] tasks = {
                    "INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by) VALUES ('Setup Database Schema', 'Create all necessary database tables and relationships', 'Done', 'High', '2025-12-20', 1, 2, 1)",
                    "INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by) VALUES ('Create Login UI', 'Design and implement user login interface', 'Done', 'High', '2025-12-21', 1, 2, 1)",
                    "INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by) VALUES ('Implement Drag and Drop', 'Add drag-and-drop functionality for task management', 'In Progress', 'High', '2025-12-28', 1, 3, 1)",
                    "INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by) VALUES ('Add User Registration', 'Create registration form for new users', 'Done', 'Medium', '2025-12-25', 1, 3, 1)",
                    "INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by) VALUES ('Create Dashboard', 'Build the main Kanban dashboard', 'In Progress', 'High', '2025-12-30', 1, 2, 1)",
                    "INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by) VALUES ('Add Task Categories', 'Implement task categorization feature', 'To Do', 'Medium', '2026-01-10', 1, NULL, 1)",
                    "INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by) VALUES ('Implement Notifications', 'Add notification system for task updates', 'To Do', 'Low', '2026-01-15', 1, NULL, 1)",
                    "INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by) VALUES ('Database Performance Optimization', 'Optimize database queries and indexes', 'To Do', 'Low', '2026-01-20', 1, NULL, 1)",
                    "INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by) VALUES ('Design Mobile UI', 'Create mockups and design for mobile application', 'In Progress', 'High', '2026-01-15', 2, 2, 1)",
                    "INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by) VALUES ('Setup Mobile Development Environment', 'Configure development tools and SDKs', 'Done', 'High', '2025-12-27', 2, 3, 1)",
                    "INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by) VALUES ('Implement Mobile Authentication', 'Add login and registration for mobile app', 'To Do', 'High', '2026-02-01', 2, NULL, 1)",
                    "INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by) VALUES ('Design New Website Layout', 'Create modern website design', 'In Progress', 'High', '2026-01-20', 3, 2, 1)",
                    "INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by) VALUES ('Content Migration', 'Migrate existing content to new website', 'To Do', 'Medium', '2026-02-05', 3, NULL, 1)",
                    "INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by) VALUES ('Setup Hosting', 'Configure web hosting and domain', 'To Do', 'High', '2026-01-30', 3, 3, 1)"
                };
                
                // Comments
                String[] comments = {
                    "INSERT INTO Comments (content, task_id, author_id) VALUES ('This task is almost complete. Need to test on different browsers.', 3, 2)",
                    "INSERT INTO Comments (content, task_id, author_id) VALUES ('Great progress on the dashboard! Keep it up.', 5, 1)",
                    "INSERT INTO Comments (content, task_id, author_id) VALUES ('We should prioritize this for the next sprint.', 6, 1)",
                    "INSERT INTO Comments (content, task_id, author_id) VALUES ('Mobile design looks great! Moving forward with development.', 9, 2)"
                };
                
                // Execute all statements
                try {
                    System.out.println("✓ Inserting admin user...");
                    try {
                        stmt.executeUpdate(adminUser);
                    } catch (Exception e) {
                        // Admin already exists, skip
                    }
                    
                    System.out.println("✓ Inserting sample users...");
                    for (String user : sampleUsers) {
                        try {
                            stmt.executeUpdate(user);
                        } catch (Exception e) {
                            // User already exists, skip
                        }
                    }
                    
                    System.out.println("✓ Inserting labels...");
                    for (String label : labels) {
                        try {
                            stmt.executeUpdate(label);
                        } catch (Exception e) {
                            // Label already exists, skip
                        }
                    }
                    
                    System.out.println("✓ Inserting team...");
                    try {
                        stmt.executeUpdate(team);
                    } catch (Exception e) {
                        // Team already exists, skip
                    }
                    
                    System.out.println("✓ Inserting team members...");
                    for (String member : teamMembers) {
                        try {
                            stmt.executeUpdate(member);
                        } catch (Exception e) {
                            // Member already exists, skip
                        }
                    }
                    
                    System.out.println("✓ Inserting projects...");
                    for (String project : projects) {
                        try {
                            stmt.executeUpdate(project);
                        } catch (Exception e) {
                            // Project already exists, skip
                        }
                    }
                    
                    System.out.println("✓ Inserting tasks...");
                    for (String task : tasks) {
                        try {
                            stmt.executeUpdate(task);
                        } catch (Exception e) {
                            // Task already exists, skip
                        }
                    }
                    
                    System.out.println("✓ Inserting comments...");
                    for (String comment : comments) {
                        try {
                            stmt.executeUpdate(comment);
                        } catch (Exception e) {
                            // Comment already exists, skip
                        }
                    }
                    
                    System.out.println("\n✓ Database population complete!");
                    System.out.println("\nSample Data Summary:");
                    System.out.println("  - 4 Users (1 Admin + 3 regular users)");
                    System.out.println("  - 5 Labels");
                    System.out.println("  - 1 Team with 3 members");
                    System.out.println("  - 3 Projects");
                    System.out.println("  - 14 Tasks");
                    System.out.println("  - 4 Comments");
                    System.out.println("\nLogin Credentials:");
                    System.out.println("  Admin: username=admin, password=admin12345");
                    System.out.println("  User: username=john, password=password123");
                    System.out.println("  User: username=jane, password=password123");
                    System.out.println("  User: username=bob, password=password123");
                    
                } catch (Exception e) {
                    System.err.println("Error inserting data: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    stmt.close();
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

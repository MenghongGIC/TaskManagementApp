-- Task Management System - Simplified Seed Data
-- Only: Users (Admin, Employees), Tasks

USE TaskManagementDB;
GO

SET NOCOUNT ON;
GO

PRINT 'Loading seed data into simplified schema...'
GO

-- ========== INSERT USERS ==========
PRINT 'Inserting Users...'
GO

-- Admin User
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE username = 'admin')
    INSERT INTO dbo.Users (username, email, password_hash, role, position, created_at)
    VALUES ('admin', 'admin@example.com', 'admin12345', 'ADMIN', 'System Administrator', SYSDATETIME());

-- Employee Users
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE username = 'john')
    INSERT INTO dbo.Users (username, email, password_hash, role, position, created_at)
    VALUES ('john', 'john@example.com', 'password123', 'USER', 'Backend Developer', SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE username = 'jane')
    INSERT INTO dbo.Users (username, email, password_hash, role, position, created_at)
    VALUES ('jane', 'jane@example.com', 'password123', 'USER', 'QA Engineer', SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE username = 'bob')
    INSERT INTO dbo.Users (username, email, password_hash, role, position, created_at)
    VALUES ('bob', 'bob@example.com', 'password123', 'USER', 'UX Designer', SYSDATETIME());

PRINT '  ✓ Users inserted'
GO

-- ========== INSERT LABELS ==========
PRINT 'Inserting Labels...'
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Labels WHERE name = 'Bug')
BEGIN
    INSERT INTO dbo.Labels (name, color) VALUES
    ('Bug', '#DC3545'),
    ('Feature', '#28A745'),
    ('Urgent', '#FD7E14'),
    ('Improvement', '#FFC107'),
    ('Documentation', '#17A2B8');
    PRINT '  ✓ Labels inserted'
END
GO

-- ========== INSERT TASKS ==========
PRINT 'Inserting Tasks...'
GO

DECLARE @adminId BIGINT;
DECLARE @johnId BIGINT;
DECLARE @janeId BIGINT;
DECLARE @bobId BIGINT;

SET @adminId = (SELECT TOP 1 id FROM dbo.Users WHERE username = 'admin');
SET @johnId = (SELECT TOP 1 id FROM dbo.Users WHERE username = 'john');
SET @janeId = (SELECT TOP 1 id FROM dbo.Users WHERE username = 'jane');
SET @bobId = (SELECT TOP 1 id FROM dbo.Users WHERE username = 'bob');

-- Task 1: Design Login Page
IF NOT EXISTS (SELECT 1 FROM dbo.Tasks WHERE title = 'Design Login Page')
    INSERT INTO dbo.Tasks (title, description, status, priority, due_date, assignee_id, created_by, created_at, updated_at)
    VALUES ('Design Login Page', 'Create responsive login UI with validation', 'In Progress', 'High', 
            DATEADD(day, 3, CAST(GETDATE() AS DATE)), @bobId, @adminId, SYSDATETIME(), SYSDATETIME());

-- Task 2: Fix Bug in Dashboard
IF NOT EXISTS (SELECT 1 FROM dbo.Tasks WHERE title = 'Fix Bug in Dashboard')
    INSERT INTO dbo.Tasks (title, description, status, priority, due_date, assignee_id, created_by, created_at, updated_at)
    VALUES ('Fix Bug in Dashboard', 'Charts not rendering correctly on Firefox', 'To Do', 'Critical', 
            CAST(GETDATE() AS DATE), @johnId, @adminId, SYSDATETIME(), SYSDATETIME());

-- Task 3: Write Unit Tests
IF NOT EXISTS (SELECT 1 FROM dbo.Tasks WHERE title = 'Write Unit Tests')
    INSERT INTO dbo.Tasks (title, description, status, priority, due_date, assignee_id, created_by, created_at, updated_at)
    VALUES ('Write Unit Tests', 'Add unit tests for task service', 'To Do', 'Medium', 
            DATEADD(day, 5, CAST(GETDATE() AS DATE)), @janeId, @adminId, SYSDATETIME(), SYSDATETIME());

-- Task 4: Update Documentation
IF NOT EXISTS (SELECT 1 FROM dbo.Tasks WHERE title = 'Update Documentation')
    INSERT INTO dbo.Tasks (title, description, status, priority, due_date, assignee_id, created_by, created_at, updated_at)
    VALUES ('Update Documentation', 'Update API documentation for new endpoints', 'Blocked', 'Low', 
            DATEADD(day, 7, CAST(GETDATE() AS DATE)), @johnId, @adminId, SYSDATETIME(), SYSDATETIME());

-- Task 5: Performance Optimization
IF NOT EXISTS (SELECT 1 FROM dbo.Tasks WHERE title = 'Performance Optimization')
    INSERT INTO dbo.Tasks (title, description, status, priority, due_date, assignee_id, created_by, created_at, updated_at)
    VALUES ('Performance Optimization', 'Optimize database queries for better response time', 'Done', 'Medium', 
            DATEADD(day, -2, CAST(GETDATE() AS DATE)), @johnId, @adminId, SYSDATETIME(), SYSDATETIME());

-- Task 6: Admin - Setup CI/CD Pipeline
IF NOT EXISTS (SELECT 1 FROM dbo.Tasks WHERE title = 'Setup CI/CD Pipeline')
    INSERT INTO dbo.Tasks (title, description, status, priority, due_date, assignee_id, created_by, created_at, updated_at)
    VALUES ('Setup CI/CD Pipeline', 'Configure automated deployment pipeline', 'In Progress', 'High', 
            DATEADD(day, 2, CAST(GETDATE() AS DATE)), @janeId, @adminId, SYSDATETIME(), SYSDATETIME());

PRINT '  ✓ Tasks inserted'
GO

-- ========== INSERT TASK LABELS ==========
PRINT 'Assigning Labels to Tasks...'
GO

DECLARE @bugLabelId BIGINT;
DECLARE @featureLabelId BIGINT;
DECLARE @urgentLabelId BIGINT;

SET @bugLabelId = (SELECT TOP 1 id FROM dbo.Labels WHERE name = 'Bug');
SET @featureLabelId = (SELECT TOP 1 id FROM dbo.Labels WHERE name = 'Feature');
SET @urgentLabelId = (SELECT TOP 1 id FROM dbo.Labels WHERE name = 'Urgent');

-- Assign Bug label to "Fix Bug in Dashboard"
IF NOT EXISTS (SELECT 1 FROM dbo.TaskLabels WHERE task_id = (SELECT id FROM dbo.Tasks WHERE title = 'Fix Bug in Dashboard') AND label_id = @bugLabelId)
BEGIN
    INSERT INTO dbo.TaskLabels (task_id, label_id)
    SELECT id, @bugLabelId FROM dbo.Tasks WHERE title = 'Fix Bug in Dashboard';
END

-- Assign Urgent label to "Fix Bug in Dashboard"
IF NOT EXISTS (SELECT 1 FROM dbo.TaskLabels WHERE task_id = (SELECT id FROM dbo.Tasks WHERE title = 'Fix Bug in Dashboard') AND label_id = @urgentLabelId)
BEGIN
    INSERT INTO dbo.TaskLabels (task_id, label_id)
    SELECT id, @urgentLabelId FROM dbo.Tasks WHERE title = 'Fix Bug in Dashboard';
END

PRINT '  ✓ Labels assigned to tasks'
GO

-- ========== VERIFICATION ==========
PRINT ''
PRINT '========== DATA VERIFICATION =========='
GO

PRINT 'Users:'
SELECT id, username, role, position FROM dbo.Users ORDER BY role DESC, username;

PRINT ''
PRINT 'Tasks Summary:'
SELECT 
    status,
    COUNT(*) as [Count],
    STRING_AGG(title, ', ') as [Tasks]
FROM dbo.Tasks
GROUP BY status
ORDER BY status;

PRINT ''
PRINT 'Tasks with Assignees:'
SELECT 
    t.title,
    t.status,
    t.priority,
    COALESCE(u.username, 'Unassigned') as [Assigned To],
    t.due_date
FROM dbo.Tasks t
LEFT JOIN dbo.Users u ON t.assignee_id = u.id
ORDER BY t.due_date DESC;

GO

PRINT ''
PRINT '========== SEED DATA LOADED SUCCESSFULLY =========='
PRINT 'System is ready for use!'
GO

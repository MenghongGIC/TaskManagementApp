USE TaskManagementDB;
GO

SET NOCOUNT ON;
GO

IF OBJECT_ID('dbo.Users', 'U') IS NULL
BEGIN
    RAISERROR('Schema not found. Run init_db_from_models.sql first.', 16, 1);
    RETURN;
END
GO

-- Declare all variables upfront to avoid scope issues with GO separators
DECLARE @adminId BIGINT;
DECLARE @johnId BIGINT;
DECLARE @janeId BIGINT;
DECLARE @bobId BIGINT;
DECLARE @workspaceEngineeringId BIGINT;
DECLARE @workspaceProductId BIGINT;
DECLARE @projTaskMgmt BIGINT;
DECLARE @projMobile BIGINT;
DECLARE @projWebsite BIGINT;

-- Users
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE username = 'admin')
    INSERT INTO dbo.Users (username, email, password_hash, role, position)
    VALUES ('admin', 'admin@gmail.com', 'admin12345', 'ADMIN', 'System Admin');

IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE username = 'john')
    INSERT INTO dbo.Users (username, email, password_hash, role, position)
    VALUES ('john', 'john@gmail.com', 'password123', 'USER', 'Backend Engineer');

IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE username = 'jane')
    INSERT INTO dbo.Users (username, email, password_hash, role, position)
    VALUES ('jane', 'jane@gmail.com', 'password123', 'USER', 'QA Engineer');

IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE username = 'bob')
    INSERT INTO dbo.Users (username, email, password_hash, role, position)
    VALUES ('bob', 'bob@gmail.com', 'password123', 'USER', 'UX Designer');

-- Populate variable IDs after users are inserted
SET @adminId = (SELECT TOP 1 id FROM dbo.Users WHERE username = 'admin');
SET @johnId = (SELECT TOP 1 id FROM dbo.Users WHERE username = 'john');
SET @janeId = (SELECT TOP 1 id FROM dbo.Users WHERE username = 'jane');
SET @bobId = (SELECT TOP 1 id FROM dbo.Users WHERE username = 'bob');

-- Labels
IF NOT EXISTS (SELECT 1 FROM dbo.Labels WHERE name = 'Bug')
BEGIN
    INSERT INTO dbo.Labels (name, color) VALUES
    ('Bug', '#DC3545'),
    ('Feature', '#28A745'),
    ('Urgent', '#FD7E14'),
    ('Improvement', '#FFC107'),
    ('Documentation', '#17A2B8');
END

-- Projects

IF @adminId IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM dbo.Projects WHERE name = 'Task Management App')
        INSERT INTO dbo.Projects (name, description, color, created_by)
        VALUES ('Task Management App', 'Core task tracking application', '#007BFF', @adminId);

    IF NOT EXISTS (SELECT 1 FROM dbo.Projects WHERE name = 'Mobile App')
        INSERT INTO dbo.Projects (name, description, color, created_by)
        VALUES ('Mobile App', 'Cross-platform mobile application project', '#28A745', @adminId);

    IF NOT EXISTS (SELECT 1 FROM dbo.Projects WHERE name = 'Website Redesign')
        INSERT INTO dbo.Projects (name, description, color, created_by)
        VALUES ('Website Redesign', 'Company website UI refresh', '#FFC107', @adminId);
END

-- Workspaces

IF @adminId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dbo.Workspaces WHERE workspace_name = 'Engineering Hub')
    INSERT INTO dbo.Workspaces (workspace_name, description, owner_id)
    VALUES ('Engineering Hub', 'Engineering delivery workspace', @adminId);

IF @johnId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dbo.Workspaces WHERE workspace_name = 'Product Studio')
    INSERT INTO dbo.Workspaces (workspace_name, description, owner_id)
    VALUES ('Product Studio', 'Product and design workspace', @johnId);

SET @workspaceEngineeringId = (SELECT TOP 1 id FROM dbo.Workspaces WHERE workspace_name = 'Engineering Hub');
SET @workspaceProductId = (SELECT TOP 1 id FROM dbo.Workspaces WHERE workspace_name = 'Product Studio');

-- Teams

IF @johnId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dbo.Teams WHERE team_name = 'Platform Team')
    INSERT INTO dbo.Teams (team_name, description, team_lead_id)
    VALUES ('Platform Team', 'Backend and DevOps ownership', @johnId);

IF @janeId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dbo.Teams WHERE team_name = 'QA Team')
    INSERT INTO dbo.Teams (team_name, description, team_lead_id)
    VALUES ('QA Team', 'Testing and release quality', @janeId);

IF @bobId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dbo.Teams WHERE team_name = 'UX Team')
    INSERT INTO dbo.Teams (team_name, description, team_lead_id)
    VALUES ('UX Team', 'Design system and UX consistency', @bobId);

-- WorkspaceMembers
INSERT INTO dbo.WorkspaceMembers (workspace_id, user_id)
SELECT w.id, u.id
FROM dbo.Workspaces w
JOIN dbo.Users u ON u.username IN ('admin', 'john', 'jane')
WHERE w.workspace_name = 'Engineering Hub'
  AND NOT EXISTS (
    SELECT 1 FROM dbo.WorkspaceMembers wm
    WHERE wm.workspace_id = w.id AND wm.user_id = u.id
  );

INSERT INTO dbo.WorkspaceMembers (workspace_id, user_id)
SELECT w.id, u.id
FROM dbo.Workspaces w
JOIN dbo.Users u ON u.username IN ('john', 'bob')
WHERE w.workspace_name = 'Product Studio'
  AND NOT EXISTS (
    SELECT 1 FROM dbo.WorkspaceMembers wm
    WHERE wm.workspace_id = w.id AND wm.user_id = u.id
  );

-- TeamMembers
INSERT INTO dbo.TeamMembers (team_id, user_id)
SELECT t.id, u.id
FROM dbo.Teams t
JOIN dbo.Users u ON u.username IN ('admin', 'john')
WHERE t.team_name = 'Platform Team'
  AND NOT EXISTS (
    SELECT 1 FROM dbo.TeamMembers tm
    WHERE tm.team_id = t.id AND tm.user_id = u.id
  );

INSERT INTO dbo.TeamMembers (team_id, user_id)
SELECT t.id, u.id
FROM dbo.Teams t
JOIN dbo.Users u ON u.username IN ('jane', 'bob')
WHERE t.team_name = 'QA Team'
  AND NOT EXISTS (
    SELECT 1 FROM dbo.TeamMembers tm
    WHERE tm.team_id = t.id AND tm.user_id = u.id
  );

INSERT INTO dbo.TeamMembers (team_id, user_id)
SELECT t.id, u.id
FROM dbo.Teams t
JOIN dbo.Users u ON u.username IN ('john', 'bob')
WHERE t.team_name = 'UX Team'
  AND NOT EXISTS (
    SELECT 1 FROM dbo.TeamMembers tm
    WHERE tm.team_id = t.id AND tm.user_id = u.id
  );

-- WorkspaceProjects
INSERT INTO dbo.WorkspaceProjects (workspace_id, project_id)
SELECT w.id, p.id
FROM dbo.Workspaces w
JOIN dbo.Projects p ON p.name IN ('Task Management App', 'Mobile App')
WHERE w.workspace_name = 'Engineering Hub'
  AND NOT EXISTS (
    SELECT 1 FROM dbo.WorkspaceProjects wp
    WHERE wp.workspace_id = w.id AND wp.project_id = p.id
  );

INSERT INTO dbo.WorkspaceProjects (workspace_id, project_id)
SELECT w.id, p.id
FROM dbo.Workspaces w
JOIN dbo.Projects p ON p.name = 'Website Redesign'
WHERE w.workspace_name = 'Product Studio'
  AND NOT EXISTS (
    SELECT 1 FROM dbo.WorkspaceProjects wp
    WHERE wp.workspace_id = w.id AND wp.project_id = p.id
  );

-- WorkspaceTeams
INSERT INTO dbo.WorkspaceTeams (workspace_id, team_id)
SELECT w.id, t.id
FROM dbo.Workspaces w
JOIN dbo.Teams t ON t.team_name IN ('Platform Team', 'QA Team')
WHERE w.workspace_name = 'Engineering Hub'
  AND NOT EXISTS (
    SELECT 1 FROM dbo.WorkspaceTeams wt
    WHERE wt.workspace_id = w.id AND wt.team_id = t.id
  );

INSERT INTO dbo.WorkspaceTeams (workspace_id, team_id)
SELECT w.id, t.id
FROM dbo.Workspaces w
JOIN dbo.Teams t ON t.team_name = 'UX Team'
WHERE w.workspace_name = 'Product Studio'
  AND NOT EXISTS (
    SELECT 1 FROM dbo.WorkspaceTeams wt
    WHERE wt.workspace_id = w.id AND wt.team_id = t.id
  );

-- Tasks - populate project IDs
SET @projTaskMgmt = (SELECT TOP 1 id FROM dbo.Projects WHERE name = 'Task Management App');
SET @projMobile = (SELECT TOP 1 id FROM dbo.Projects WHERE name = 'Mobile App');
SET @projWebsite = (SELECT TOP 1 id FROM dbo.Projects WHERE name = 'Website Redesign');

IF @projTaskMgmt IS NOT NULL AND @adminId IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM dbo.Tasks WHERE title = 'Setup Database Schema')
    BEGIN
        INSERT INTO dbo.Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by)
        VALUES
        ('Setup Database Schema', 'Create tables and relationships', 'Done', 'High', '2026-03-05', @projTaskMgmt, @johnId, @adminId),
        ('Implement Kanban Board', 'Build drag-and-drop task board', 'In Progress', 'High', '2026-03-20', @projTaskMgmt, @janeId, @adminId),
        ('Add Notifications', 'Task assignment and reminder notifications', 'To Do', 'Medium', '2026-03-28', @projTaskMgmt, NULL, @adminId);
    END
END

IF @projMobile IS NOT NULL AND @adminId IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM dbo.Tasks WHERE title = 'Design Mobile UI')
    BEGIN
        INSERT INTO dbo.Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by)
        VALUES
        ('Design Mobile UI', 'Prepare mobile design prototype', 'In Progress', 'High', '2026-03-18', @projMobile, @bobId, @adminId),
        ('Implement Mobile Auth', 'Login/register flow on mobile', 'To Do', 'High', '2026-03-25', @projMobile, @johnId, @adminId);
    END
END

IF @projWebsite IS NOT NULL AND @adminId IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM dbo.Tasks WHERE title = 'Redesign Landing Page')
    BEGIN
        INSERT INTO dbo.Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by)
        VALUES
        ('Redesign Landing Page', 'Modernize homepage look and feel', 'In Progress', 'Medium', '2026-03-19', @projWebsite, @bobId, @adminId),
        ('Migrate Website Content', 'Move old content to new templates', 'To Do', 'Low', '2026-03-30', @projWebsite, @janeId, @adminId);
    END
END

-- TaskLabels
INSERT INTO dbo.TaskLabels (task_id, label_id)
SELECT t.id, l.id
FROM dbo.Tasks t
JOIN dbo.Labels l ON l.name IN ('Feature', 'Urgent')
WHERE t.title = 'Implement Kanban Board'
  AND NOT EXISTS (
    SELECT 1 FROM dbo.TaskLabels tl WHERE tl.task_id = t.id AND tl.label_id = l.id
  );

INSERT INTO dbo.TaskLabels (task_id, label_id)
SELECT t.id, l.id
FROM dbo.Tasks t
JOIN dbo.Labels l ON l.name IN ('Documentation')
WHERE t.title = 'Migrate Website Content'
  AND NOT EXISTS (
    SELECT 1 FROM dbo.TaskLabels tl WHERE tl.task_id = t.id AND tl.label_id = l.id
  );

-- Activity Log
INSERT INTO dbo.ActivityLog (action, entity_type, entity_id, entity_name, user_id, details)
SELECT 'CREATE', 'PROJECT', p.id, p.name, @adminId, 'Initial sample project seeded'
FROM dbo.Projects p
WHERE p.name IN ('Task Management App', 'Mobile App', 'Website Redesign')
  AND NOT EXISTS (
    SELECT 1 FROM dbo.ActivityLog a
    WHERE a.entity_type = 'PROJECT' AND a.entity_id = p.id AND a.action = 'CREATE'
  );
GO

PRINT 'Sample data seeded successfully.';
GO

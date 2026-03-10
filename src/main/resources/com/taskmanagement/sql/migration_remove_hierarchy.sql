-- Task Management System - Migration from Hierarchical to Simplified
-- Removes: Workspace, Team, Project hierarchy
-- Updates: Tasks table to remove project dependencies

USE TaskManagementDB;
GO

SET NOCOUNT ON;
GO

PRINT '========== MIGRATION: Remove Workspace, Team, Project Hierarchy =========='
GO

-- Step 1: Drop dependent tables first (in order of dependencies)
PRINT 'Step 1: Dropping dependent tables...'
GO

IF OBJECT_ID('dbo.WorkspaceTeams', 'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.WorkspaceTeams;
    PRINT '  ✓ Dropped WorkspaceTeams'
END
GO

IF OBJECT_ID('dbo.WorkspaceProjects', 'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.WorkspaceProjects;
    PRINT '  ✓ Dropped WorkspaceProjects'
END
GO

IF OBJECT_ID('dbo.WorkspaceMembers', 'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.WorkspaceMembers;
    PRINT '  ✓ Dropped WorkspaceMembers'
END
GO

IF OBJECT_ID('dbo.TeamMembers', 'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.TeamMembers;
    PRINT '  ✓ Dropped TeamMembers'
END
GO

-- Step 2: Update Tasks table - remove project_id foreign key constraint
PRINT 'Step 2: Updating Tasks table...'
GO

-- First, get and drop the foreign key constraint
IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS WHERE TABLE_NAME = 'Tasks' AND CONSTRAINT_NAME LIKE 'FK_Tasks%project%')
BEGIN
    ALTER TABLE dbo.Tasks 
    DROP CONSTRAINT FK_Tasks_Projects_project;
    PRINT '  ✓ Dropped FK_Tasks_Projects_project constraint'
END
GO

-- Remove the project_id column if it exists
IF EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Tasks' AND COLUMN_NAME = 'project_id')
BEGIN
    ALTER TABLE dbo.Tasks 
    DROP COLUMN project_id;
    PRINT '  ✓ Dropped project_id column from Tasks'
END
GO

-- Add updated_at column if it doesn''t exist
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Tasks' AND COLUMN_NAME = 'updated_at')
BEGIN
    ALTER TABLE dbo.Tasks 
    ADD updated_at DATETIME2 NOT NULL CONSTRAINT DF_Tasks_updated_at DEFAULT SYSDATETIME();
    PRINT '  ✓ Added updated_at column to Tasks'
END
GO

-- Step 3: Drop the main hierarchy tables
PRINT 'Step 3: Dropping hierarchy tables...'
GO

IF OBJECT_ID('dbo.Teams', 'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.Teams;
    PRINT '  ✓ Dropped Teams'
END
GO

IF OBJECT_ID('dbo.Projects', 'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.Projects;
    PRINT '  ✓ Dropped Projects'
END
GO

IF OBJECT_ID('dbo.Workspaces', 'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.Workspaces;
    PRINT '  ✓ Dropped Workspaces'
END
GO

-- Step 4: Verify final schema
PRINT 'Step 4: Verifying final schema...'
GO

SELECT 
    'Remaining Tables:' as [Status],
    COUNT(*) as [Count]
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = 'dbo' AND TABLE_TYPE = 'BASE TABLE'
GROUP BY 'Remaining Tables:';

PRINT ''
PRINT 'Tables in database:'
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = 'dbo' AND TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;

GO

PRINT ''
PRINT '========== MIGRATION COMPLETE =========='
PRINT 'Task Management System simplified: Tasks are now the top-level entity'
PRINT 'Remaining tables: Users, Tasks, Labels, TaskLabels, ActivityLog'
GO

USE TaskManagementDB;
GO

SET NOCOUNT ON;
GO

DECLARE @sql NVARCHAR(MAX) = N'';

-- Drop all foreign key constraints first
SELECT @sql += N'ALTER TABLE ' 
    + QUOTENAME(SCHEMA_NAME(parent.schema_id)) + N'.' + QUOTENAME(parent.name)
    + N' DROP CONSTRAINT ' + QUOTENAME(fk.name) + N';' + CHAR(10)
FROM sys.foreign_keys fk
JOIN sys.tables parent ON fk.parent_object_id = parent.object_id
WHERE parent.is_ms_shipped = 0;

IF LEN(@sql) > 0
BEGIN
    PRINT 'Dropping foreign key constraints...';
    EXEC sp_executesql @sql;
END

SET @sql = N'';

-- Drop all user tables
SELECT @sql += N'DROP TABLE '
    + QUOTENAME(SCHEMA_NAME(t.schema_id)) + N'.' + QUOTENAME(t.name)
    + N';' + CHAR(10)
FROM sys.tables t
WHERE t.is_ms_shipped = 0;

IF LEN(@sql) > 0
BEGIN
    PRINT 'Dropping tables...';
    EXEC sp_executesql @sql;
    PRINT 'All user tables were dropped successfully.';
END
ELSE
BEGIN
    PRINT 'No user tables found to drop.';
END
GO

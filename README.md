## How to Run with SQL Server

1. Install Microsoft SQL Server (Express is free) or use Azure SQL
2. Run the script `resources/sql/init_database_sqlserver.sql` in SSMS or Azure Data Studio
3. Update credentials in `src/com/taskmanager/database/DBConnection.java`
4. Add the Microsoft JDBC driver to `/lib/` (mssql-jdbc-*.jar)
5. Build the JAR:
   ```bash
   javac -cp "lib/*" -d bin src/com/taskmanager/**/*.java
   jar cfm TaskManagerApp.jar MANIFEST.MF -C bin/ . lib/*
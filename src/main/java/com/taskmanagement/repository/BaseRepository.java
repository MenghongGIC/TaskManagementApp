package com.taskmanagement.repository;

import java.sql.Connection;

import com.taskmanagement.database.DBConnection;

public class BaseRepository {
    
    protected Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }
}

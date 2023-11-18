package com.mygdx.game;

import java.sql.*;

public class DBManager {
    private Connection connection;
    
    public DBManager() {
        // Initialize database connection
        try {
        	Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\baran\\libGDX-repo\\2d-arena-db\\2d-arena-shooter-database.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
        createScoresTable();
    }
    
    public void createScoresTable() {
        try {
            Statement statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS scores (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "score INTEGER NOT NULL," +
                    "date DATE DEFAULT CURRENT_DATE)";
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void saveScore(int score) {
        try {
            String sql = "INSERT INTO scores (score, date) VALUES (?, DATE('now'))";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, score);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public ResultSet getScores() {
        ResultSet resultSet = null;
        try {
            String sql = "SELECT * FROM scores ORDER BY score DESC LIMIT 5";
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
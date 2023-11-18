package com.mygdx.game;

import java.sql.*;

public class DBManager {
    private Connection connection;
    
    public DBManager() {
        // Initialize database connection
        try {
        	Class.forName("org.sqlite.JDBC"); //  load and register the sqlite JDBC driver
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
            		"difficulty INTEGER NOT NULL," +
                    "score INTEGER NOT NULL," +
                    "date DATE DEFAULT CURRENT_DATE)";
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void saveScore(int difficulty, int score) {
        try {
            String sql = "INSERT INTO scores (difficulty, score, date) VALUES (?, ?, DATE('now'))";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, difficulty);
            statement.setInt(2, score);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public ResultSet getScores() {
        ResultSet resultSet = null;
        try {
            String sql = "SELECT * FROM scores ORDER BY difficulty DESC, score DESC LIMIT 5";
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
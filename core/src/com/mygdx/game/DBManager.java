package com.mygdx.game;

import java.io.File;
import java.sql.*;

public class DBManager {
    private Connection connection;
    
    public DBManager() {
        String dbFileName = "../2d-arena-shooter-db/2d-arena-shooter-database.db"; // SQLite database file name
        File dbFile = new File(dbFileName);

        // Initialize database connection
        try {
            if(!dbFile.exists()){
                dbFile.getParentFile().mkdirs(); // Create parent directories
            }

            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            createScoresTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            String sql = "SELECT * FROM scores ORDER BY 2*(difficulty*difficulty)/((difficulty+1)*(difficulty+1))*score DESC LIMIT 5";
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
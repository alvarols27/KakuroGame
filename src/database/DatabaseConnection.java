package database;

import java.sql.*;
import java.io.File;

public class DatabaseConnection {
    private static Connection connection = null;
    private static final String DATABASE_URL = "jdbc:sqlite:database/kakuro.db";
    
    private DatabaseConnection() {}
    
    public static Connection getConnection() {
        try {
            // Ensure database directory exists
            File dbDir = new File("database");
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }
            
            // Only create a new connection if the existing one is closed or null
            if (connection == null || connection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DATABASE_URL);
                
                //enable foreign keys
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");
                }
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed");
            e.printStackTrace();
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Database connection closed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection = null;
        }
    }
}
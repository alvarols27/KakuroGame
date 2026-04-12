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
                
                boolean tablesExist = false;
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='players'")) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        tablesExist = true;
                    }
                }
                
                if (!tablesExist) {
                    initializeDatabase();
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
    
    private static void initializeDatabase() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS players (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "is_guest BOOLEAN DEFAULT 0" +
            ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS game_results (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "player_id INTEGER NOT NULL," +
                "time_played INTEGER NOT NULL," +
                "difficulty TEXT NOT NULL CHECK(difficulty IN ('Easy', 'Medium', 'Hard'))," +
                "hints_used INTEGER DEFAULT 0," +                       
                "date DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE" +
            ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS best_scores (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "player_id INTEGER NOT NULL," +
                "difficulty TEXT NOT NULL CHECK(difficulty IN ('Easy', 'Medium', 'Hard'))," +
                "best_time INTEGER NOT NULL," +
                "date_achieved DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE," +
                "UNIQUE(player_id, difficulty)" +
            ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS saved_games (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "player_id INTEGER NOT NULL," +
                "difficulty TEXT NOT NULL CHECK(difficulty IN ('Easy', 'Hard'))," +
                "timer INTEGER NOT NULL," +
                "grid_data TEXT NOT NULL," +
                "hints_remaining INTEGER NOT NULL," +
                "hints_used INTEGER DEFAULT 0," +
                "start_date DATETIME," +
                "last_saved DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "move_history TEXT," +
                "FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE" +
            ");");
            System.out.println("Database tables initialized successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
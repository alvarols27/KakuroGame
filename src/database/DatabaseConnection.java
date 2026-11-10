package database;

import java.sql.*;

public class DatabaseConnection {
    private static Connection connection = null;

    private static final String DATABASE_URL =
            "jdbc:postgresql://localhost:5432/kakurogame";
    private static final String USER     = "alvarolimaymantasoria";
    private static final String PASSWORD = "";

    private DatabaseConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found. Add postgresql-42.7.4.jar to the classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
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
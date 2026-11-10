package database;

import model.Player;
import java.sql.*;
import java.util.Optional;

public class PlayerDAO {

    public static Optional<Player> login(String username, String password) {
        String sql = "SELECT * FROM players WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Player player = new Player(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
                return Optional.of(player);
            }

        } catch (SQLException e) {
            System.err.println("SQL Error in login: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static void register(String username, String password) {
        String sql = "INSERT INTO players (username, password) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();          // <-- was missing

        } catch (SQLException e) {
            System.err.println("SQL Error in register: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM players WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Player getPlayerById(int id) {
        String sql = "SELECT * FROM players WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Player(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
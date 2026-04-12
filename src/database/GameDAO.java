package database;

import model.GameResult;
import model.Player;
import java.sql.*;
import java.util.*;

public class GameDAO {
    
    public static void saveGameResult(Player player, int timePlayed, String difficulty, int numHintsUsed) {
        if (player.getId() == 0) return; // Guest players don't save
        
        String sql = "INSERT INTO game_results (player_id, time_played, difficulty, hints_used) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, player.getId());
            pstmt.setInt(2, timePlayed);
            pstmt.setString(3, difficulty);
            pstmt.setInt(4, numHintsUsed);
            pstmt.executeUpdate();
            
            // Update best score if applicable
            updateBestScore(player.getId(), difficulty, timePlayed);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void updateBestScore(int playerId, String difficulty, int timePlayed) {
        String sql = "INSERT INTO best_scores (player_id, difficulty, best_time, date_achieved) " +
                     "VALUES (?, ?, ?, CURRENT_TIMESTAMP) " +
                     "ON CONFLICT(player_id, difficulty) DO UPDATE SET " +
                     "best_time = CASE WHEN excluded.best_time < best_time THEN excluded.best_time ELSE best_time END, " +
                     "date_achieved = CASE WHEN excluded.best_time < best_time THEN CURRENT_TIMESTAMP ELSE date_achieved END";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            pstmt.setString(2, difficulty);
            pstmt.setInt(3, timePlayed);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static List<GameResult> getPlayerGameResults(int playerId) {
        List<GameResult> results = new ArrayList<>();
        String sql = "SELECT * FROM game_results WHERE player_id = ? ORDER BY date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                GameResult gr = new GameResult(
                    rs.getInt("player_id"),
                    "", // username will be filled separately
                    rs.getInt("time_played"),
                    rs.getString("difficulty"),
                    rs.getInt("hints_used")
                );
                gr.setDate(rs.getTimestamp("date"));
                results.add(gr);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
    
    public static List<GameResult> getBestScoresByDifficulty(String difficulty) {
        List<GameResult> results = new ArrayList<>();
        String sql = "SELECT p.username, gr.* FROM game_results gr " +
                     "JOIN players p ON gr.player_id = p.id " +
                     "WHERE gr.difficulty = ? " +
                     "ORDER BY gr.time_played ASC LIMIT 10";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, difficulty);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                GameResult gr = new GameResult(
                    rs.getInt("player_id"),
                    rs.getString("username"),
                    rs.getInt("time_played"),
                    rs.getString("difficulty"),
                    rs.getInt("hints_used")
                );
                gr.setDate(rs.getTimestamp("date"));
                results.add(gr);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}
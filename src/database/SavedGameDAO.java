package database;

import model.*;
import model.difficulty.*;
import interfaces.Difficulty;

import com.google.gson.Gson;
import java.sql.*;
import java.util.*;

public class SavedGameDAO {
    private static Gson gson = new Gson();
    
    public static void saveGameSession(GameSession session) {
        int gameSessionId = session.getSessionId();
        
        String sql;
        if (gameSessionId > 0) {
            //if gameSession exists
            sql = "UPDATE saved_games SET difficulty = ?, timer = ?, grid_data = ?, " +
              "hints_remaining = ?, hints_used = ?, start_date = ?, last_saved = CURRENT_TIMESTAMP " +
              "WHERE id = ?";
        } else {
            // create another one
            sql = "INSERT INTO saved_games (player_id, difficulty, timer, grid_data, " +
              "hints_remaining, hints_used, start_date, last_saved) " +
              "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            //convert grid to JSON for storage
            String gridJson = gson.toJson(session.getGrid());
            
            if (gameSessionId > 0) {
                pstmt.setString(1, session.getDifficultyName());
                pstmt.setInt(2, session.getTimer());
                pstmt.setString(3, gridJson);
                pstmt.setInt(4, session.getHintsRemaining());
                pstmt.setInt(5, session.getHintsUsed());
                pstmt.setTimestamp(6, new Timestamp(session.getStartDate().getTime()));
                pstmt.setInt(7, gameSessionId); //where gameSessionId is...
            } else {
                //INSERT
                pstmt.setInt(1, session.getPlayer().getId());
                pstmt.setString(2, session.getDifficultyName());
                pstmt.setInt(3, session.getTimer());
                pstmt.setString(4, gridJson);
                pstmt.setInt(5, session.getHintsRemaining());
                pstmt.setInt(6, session.getHintsUsed());
                pstmt.setTimestamp(7, new Timestamp(session.getStartDate().getTime()));
            }
            pstmt.executeUpdate();

            //if new GameSession ID
            if (gameSessionId <= 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        session.setSessionId(generatedKeys.getInt(1));
                    }
                }

                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static List<SavedGameInfo> getSavedGames(int playerId) {
        List<SavedGameInfo> result = new ArrayList<>();
        String sql = "SELECT id, difficulty, timer, last_saved FROM saved_games WHERE player_id = ? ORDER BY last_saved DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new SavedGameInfo(
                    rs.getInt("id"),
                    rs.getString("difficulty"),
                    rs.getInt("timer"),
                    rs.getTimestamp("last_saved")
                    // getGridSizeForDifficulty(rs.getString("difficulty"))
                ));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static GameSession loadGameSession(int sessionId) {
        String sql = "SELECT * FROM saved_games WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sessionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Difficulty difficulty = createDifficulty(rs.getString("difficulty"));
                Player tempPlayer = new Player(rs.getInt("player_id"), "", "");
                
                // Use the constructor that takes all parameters
                GameSession session = new GameSession(
                    rs.getInt("id"),
                    tempPlayer,
                    difficulty,
                    null, // grid will be restored separately
                    rs.getInt("timer"),
                    rs.getInt("hints_remaining"),
                    rs.getInt("hints_used"),
                    "SAVED",
                    rs.getTimestamp("start_date")
                );
                
                // Restore grid from JSON
                String gridJson = rs.getString("grid_data");
                Grid grid = gson.fromJson(gridJson, Grid.class);
                session.setGrid(grid);
                
                return session;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void deleteSavedGame(int sessionId) {
        String sql = "DELETE FROM saved_games WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sessionId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static Difficulty createDifficulty(String diff) {
        switch (diff) {
            case "Medium": return new MediumDifficulty();
            case "Hard": return new HardDifficulty();
            default: return new EasyDifficulty();
        }
    }
}
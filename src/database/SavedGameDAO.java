package database;

import model.*;
import model.difficulty.*;
import interfaces.Difficulty;
import interfaces.SavedGameInfo;

import java.util.*;
import java.io.*;

//this is mostly for SAVE/LOAD game
public class SavedGameDAO {
    private static final String SAVED_GAMES_FILE = "saved_games.dat";
    private static List<SavedGameData> savedGames = new ArrayList<>();
    
    static {
        loadSavedGames();
    }
    
    private static void loadSavedGames() {
        File file = new File(SAVED_GAMES_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVED_GAMES_FILE))) {
                savedGames = (List<SavedGameData>) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void saveSavedGames() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVED_GAMES_FILE))) {
            oos.writeObject(savedGames);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void saveGameSession(GameSession session) {
        // Delete existing session for this player if exists
        savedGames.removeIf(sg -> sg.getPlayerId() == session.getPlayer().getId());
        
        SavedGameData data = new SavedGameData(
            session.getPlayer().getId(),
            session.getDifficultyName(),
            session.getTimer(),
            session.getGrid(),
            session.getHintsRemaining(),
            session.getHintsUsed(),
            session.getStartDate(),
            new Date()
        );
        savedGames.add(data);
        saveSavedGames();
    }
    
    public static List<SavedGameInfo> getSavedGames(int playerId) {
        List<SavedGameInfo> result = new ArrayList<>();
        int id = 1;
        for (SavedGameData data : savedGames) {
            if (data.getPlayerId() == playerId) {
                result.add(new SavedGameInfo(
                    id++,
                    data.getDifficulty(),
                    data.getTimer(),
                    data.getLastSavedDate()
                ));
            }
        }
        return result;
    }
    
    public static GameSession loadGameSession(int sessionId) {
        // Find the session (simplified - in real implementation, use ID)
        for (SavedGameData data : savedGames) {
            if (data.getPlayerId() > 0) { // Simplified matching
                Difficulty difficulty;
                switch (data.getDifficulty()) {
                    case "Medium":
                        difficulty = new MediumDifficulty();
                        break;
                    case "Hard":
                        difficulty = new HardDifficulty();
                        break;
                    default:
                        difficulty = new EasyDifficulty();
                }
                
                Player tempPlayer = new Player(data.getPlayerId(), "", "");
                GameSession session = new GameSession(tempPlayer, difficulty);
                session.setGrid(data.getGrid());
                session.incrementTimer();
                // Note: hints remaining/used would need proper restoration
                return session;
            }
        }
        return null;
    }
    
    public static void deleteSavedGame(int sessionId) {
        // Simplified - would use proper ID in real implementation
        if (sessionId > 0 && sessionId <= savedGames.size()) {
            savedGames.remove(sessionId - 1);
            saveSavedGames();
        }
    }
    
    public static void deleteAllSavedGamesForPlayer(int playerId) {
        savedGames.removeIf(sg -> sg.getPlayerId() == playerId);
        saveSavedGames();
    }
    
    // Inner class for serialization
    static class SavedGameData implements Serializable {
        private static final long serialVersionUID = 1L;
        private int playerId;
        private String difficulty;
        private int timer;
        private Grid grid;
        private int hintsRemaining;
        private int hintsUsed;
        private Date startDate;
        private Date lastSavedDate;
        
        public SavedGameData(int playerId, String difficulty, int timer, Grid grid,
                             int hintsRemaining, int hintsUsed, Date startDate, Date lastSavedDate) {
            this.playerId = playerId;
            this.difficulty = difficulty;
            this.timer = timer;
            this.grid = grid;
            this.hintsRemaining = hintsRemaining;
            this.hintsUsed = hintsUsed;
            this.startDate = startDate;
            this.lastSavedDate = lastSavedDate;
        }
        
        public int getPlayerId() { return playerId; }
        public String getDifficulty() { return difficulty; }
        public int getTimer() { return timer; }
        public Grid getGrid() { return grid; }
        public int getHintsRemaining() { return hintsRemaining; }
        public int getHintsUsed() { return hintsUsed; }
        public Date getStartDate() { return startDate; }
        public Date getLastSavedDate() { return lastSavedDate; }
    }
}

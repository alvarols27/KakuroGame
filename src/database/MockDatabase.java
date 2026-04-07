package database;

import model.Player;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockDatabase {
    private static List<Player> players = new ArrayList<>();
    private static List<GameResult> gameResults = new ArrayList<>();

    private static final String GAME_RESULTS_FILE = "game_results.dat";
    private static final String PLAYERS_FILE = "players.dat";

    static {
        loadPlayers();
        if (players.isEmpty()) {
            players.add(new Player(1, "demo_user", "password"));
            players.add(new Player(2, "guest", ""));
            players.add(new Player(3, "tales_user", "1234"));
            savePlayers();
        }
        loadGameResults();
    }

    private static void saveGameResults() {
        try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(GAME_RESULTS_FILE))) {
            oos.writeObject(gameResults);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearAllGameResults() {
    gameResults.clear();
    saveGameResults();
    System.out.println("All game results cleared!");
}

    @SuppressWarnings("unchecked")
    private static void loadGameResults() {
        java.io.File file = new java.io.File(GAME_RESULTS_FILE);
        if (file.exists()) {
            try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.FileInputStream(GAME_RESULTS_FILE))) {
                gameResults = (List<GameResult>) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void savePlayers() {
        try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(PLAYERS_FILE))) {
            oos.writeObject(players);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadPlayers() {
        java.io.File file = new java.io.File(PLAYERS_FILE);
        if (file.exists()) {
            try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.FileInputStream(PLAYERS_FILE))) {
                players = (List<Player>) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Player> getPlayers() {
        return players;
    }

    public static void addPlayer(Player player) {
        players.add(player);
        savePlayers();
    }

    public static List<GameResult> getGameResults() {
        return gameResults;
    }

    public static void addGameResult(GameResult record) {
        gameResults.add(record);
        saveGameResults();
    }

    public static class GameResult implements java.io.Serializable {
        public int playerId;
        public String username;
        public int timePlayed;
        public String difficulty;
        public int numHintsUsed;
        public Date date;

        public GameResult(int playerId, String username, int timePlayed, String difficulty, int numHintsUsed) {
            this.playerId = playerId;
            this.username = username;
            this.timePlayed = timePlayed;
            this.difficulty = difficulty;
            this.numHintsUsed = numHintsUsed;
            this.date = new Date();
        }

        // Getters and setters
        public int getPlayerId() { return playerId; }
        public void setPlayerId(int playerId) { this.playerId = playerId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public int getTimePlayed() { return timePlayed; }
        public void setTimePlayed(int timePlayed) { this.timePlayed = timePlayed; }

        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

        public int getHintsUsed() { return numHintsUsed; }
        public void setHintsUsed(int numHintsUsed) { this.numHintsUsed = numHintsUsed; }

        public Date getDate() { return date; }
        public void setDate(Date date) { this.date = date; }
    }
}

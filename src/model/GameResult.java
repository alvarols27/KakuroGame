package model;

import java.util.Date;

public class GameResult {
    private int playerId;
    private String username;
    private int timePlayed;
    private String difficulty;
    private int hintsUsed;
    private Date date;
    
    public GameResult(int playerId, String username, int timePlayed, String difficulty, int hintsUsed) {
        this.playerId = playerId;
        this.username = username;
        this.timePlayed = timePlayed;
        this.difficulty = difficulty;
        this.hintsUsed = hintsUsed;
        this.date = new Date();
    }
    
    public int getPlayerId() { return playerId; }
    public String getUsername() { return username; }
    public int getTimePlayed() { return timePlayed; }
    public String getDifficulty() { return difficulty; }
    public int getHintsUsed() { return hintsUsed; }
    public Date getDate() { return date; }
    
    public void setDate(Date date) { this.date = date; }
}
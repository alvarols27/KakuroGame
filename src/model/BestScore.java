package model;

import java.util.Date;

public class BestScore {
    private String difficulty;
    private int bestTime;
    private Date dateAchieved;
    private int playerId;

    public BestScore(String difficulty, int bestTime, Date dateAchieved, int playerId) {
        this.difficulty = difficulty;
        this.bestTime = bestTime;
        this.dateAchieved = dateAchieved;
        this.playerId = playerId;
    }
    
    public String getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    
    public int getBestTime() {
        return bestTime;
    }
    
    public void setBestTime(int bestTime) {
        this.bestTime = bestTime;
    }
    
    public Date getDateAchieved() {
        return dateAchieved;
    }
    
    public void setDateAchieved(Date dateAchieved) {
        this.dateAchieved = dateAchieved;
    }
    
    public int getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void updateBestScore(int time, Date date) {
        this.bestTime = time;
        this.dateAchieved = date;

        //also would save to the db
    }
}

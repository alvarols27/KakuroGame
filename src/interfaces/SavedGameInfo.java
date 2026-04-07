package interfaces;

import java.util.Date;

public class SavedGameInfo {
    private int sessionId;
    private String difficulty;
    private int timer;
    private Date lastSavedDate;
    
    public SavedGameInfo(int sessionId, String difficulty, int timer, Date lastSavedDate) {
        this.sessionId = sessionId;
        this.difficulty = difficulty;
        this.timer = timer;
        this.lastSavedDate = lastSavedDate;
    }
    
    public int getSessionId() { return sessionId; }
    public String getDifficulty() { return difficulty; }
    public int getTimer() { return timer; }
    public Date getLastSavedDate() { return lastSavedDate; }
}

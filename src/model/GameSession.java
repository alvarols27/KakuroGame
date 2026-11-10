package model;

import interfaces.Difficulty;
import java.util.*;

public class GameSession {
    private int sessionId;
    private int timer;
    private Grid grid;
    private Player player;
    private Difficulty difficulty;
    private int hintsRemaining;
    private int hintsUsed;
    private String status;
    private boolean noteMode;
    private Date startDate;
    private Date lastSavedDate;

    // Undo/Redo stacks
    private Stack<MoveRecord> undoStack;
    private Stack<MoveRecord> redoStack;

    public GameSession(Player player, Difficulty difficulty) {
        this.player = player;
        this.difficulty = difficulty;
        this.hintsRemaining = difficulty.getMaxHints();
        this.hintsUsed = 0;
        this.grid = new Grid(difficulty.getGridSize());
        this.timer = 0;
        this.status = "ACTIVE";
        this.noteMode = false;
        this.startDate = new Date();
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    // Constructor for loading saved game
    public GameSession(int sessionId, Player player, Difficulty difficulty, 
                       Grid grid, int timer, int hintsRemaining, int hintsUsed,
                       String status, Date startDate) {
        this.sessionId = sessionId;
        this.player = player;
        this.difficulty = difficulty;
        this.grid = grid;
        this.timer = timer;
        this.hintsRemaining = hintsRemaining;
        this.hintsUsed = hintsUsed;
        this.status = status;
        this.startDate = startDate;
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    public void recordMove(int row, int col, int oldValue, int newValue) {
        //clear redo stack when a new move is made
        redoStack.clear();
        
        //create and store the move
        MoveRecord move = new MoveRecord(row, col, oldValue, newValue);
        undoStack.push(move);
    }

    //undo the last move
    public MoveRecord requestUndo() {
        if (!undoStack.isEmpty()) {
            MoveRecord move = undoStack.pop();
            redoStack.push(move);
            return move;
        }
        return null;
    }

    // redo the last undone move
    public MoveRecord requestRedo() {
        if (!redoStack.isEmpty()) {
            MoveRecord move = redoStack.pop();
            undoStack.push(move);
            return move;
        }
        return null;
    }
    
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    
    public void clearMoves() {
        undoStack.clear();
        redoStack.clear();
    }

    public void incrementTimer() {
        timer++;
    }

    public void addTime(int seconds) {
        timer += seconds;
    }

    public boolean useHint() {
        if (hintsRemaining > 0) {
            hintsRemaining--;
            hintsUsed++;
            return true;
        }
        return false;
    }

    public void toggleNoteMode() {
        this.noteMode = !noteMode;
    }

    public void complete() {
        this.status = "COMPLETED";
    }

    public void save() {
        this.status = "SAVED";
    }

    // Getters and Setters
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    public Player getPlayer() { return player; }
    public Difficulty getDifficulty() { return difficulty; }
    public String getDifficultyName() { return difficulty.getName(); }
    public Grid getGrid() { return grid; }
    public void setGrid(Grid grid) { this.grid = grid; }
    public void setTimer(int timer) { this.timer = timer; }
    public int getTimer() { return timer; }
    public int getHintsRemaining() { return hintsRemaining; }
    public int getHintsUsed() { return hintsUsed; }
    public String getStatus() { return status; }
    public boolean isNoteMode() { return noteMode; }
    public Date getStartDate() { return startDate; }
    public Date getLastSavedDate() { return lastSavedDate; }
}
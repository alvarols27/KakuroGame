package model;

import interfaces.Difficulty;
import java.util.*;

public class GameSession {
    private int sessionId; //ID session (load/save)
    private int timer;
    private Grid grid;
    private Player player;
    private Difficulty difficulty;
    private int hintsRemaining;
    private int hintsUsed;
    private String status; //ACTIVE, COMPLETED, SAVED
    private boolean noteMode;
    private Date startDate;
    private Date lastSavedDate;

    private int currentPosition;
    private Stack<MoveRecord> undoStack; //CHECK NAME
    private Stack<MoveRecord> redoStack; //CHECK NAME

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
        this.currentPosition = 0;
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    //to load a game
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

    //game actions
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

    //WE COULD UPDATE THE CLASS DIAGRAM
    public void recordMove(int row, int col, int oldValue, int newValue) {
        redoStack.clear(); //just when new move is done

        MoveRecord move = new MoveRecord(currentPosition++, oldValue, newValue, row, col);
        undoStack.push(move);
    }

    public MoveRecord undo() {
        if (!undoStack.isEmpty()){
            // undoStack: [Move1, Move2]
            // move = Move3  ← stored in variable
            // redoStack: []
            MoveRecord move = undoStack.pop(); //to remove the top last move (REMOVES AND RETURNS THE TOP ITEM)
            redoStack.push(move); //puts that move into redo stack (ADDS ITEM TO THE TOP)
            return move;
        }
        return null;
    }

    public MoveRecord redo() {
        if (!redoStack.isEmpty()) {
            MoveRecord move = redoStack.pop();
            undoStack.push(move);
            return move;
        }
        return null;
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

    // Getters
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
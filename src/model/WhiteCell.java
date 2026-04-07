package model;

public class WhiteCell {
    private int currentValue;
    private int noteValue;
    private int row;
    private int column;
    
    public WhiteCell(int row, int column) {
        this.row = row;
        this.column = column;
        this.currentValue = 0;
        this.noteValue = 0;
    }
    
    public int getCurrentValue() {
        return currentValue;
    }
    
    public void setCurrentValue(int value) {
        this.currentValue = value;
    }
    
    public int getNoteValue() {
        return noteValue;
    }
    
    public void setNoteValue(int noteValue) {
        this.noteValue = noteValue;
    }
    
    public void addNoteValue(int value) {
        this.noteValue = value;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getColumn() {
        return column;
    }
}
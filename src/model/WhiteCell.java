package model;

public class WhiteCell extends Cell {
    private int currentValue;
    private int noteValue;
    private int row;
    private int column;
    
    public WhiteCell(int row, int column) {
        super(0, false);
        this.row = row;
        this.column = column;
        this.currentValue = 0;
        this.noteValue = 0;
    }
    
    @Override
    public int getValue() {
        return currentValue;
    }
    
    @Override
    public void setValue(int value) {
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

    public void clearValue() {
        this.currentValue = 0;
        this.noteValue = 0;
    }

    public boolean isValid() {
        return currentValue >= 1 && currentValue <= 9;
    }
}
package model;

public class MoveRecord {
    private int position;
    private int oldValue;
    private int newValue;
    private int row;
    private int column;
    
    public MoveRecord(int position, int oldValue, int newValue, int row, int column) {
        this.position = position;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.row = row;
        this.column = column;
    }
    
    public int getPosition() {
        return position;
    }
    
    public int getOldValue() {
        return oldValue;
    }
    
    public int getValue() {
        return newValue;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getColumn() {
        return column;
    }
}
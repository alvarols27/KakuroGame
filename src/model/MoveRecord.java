package model;

public class MoveRecord {
    private int position;
    private int value;
    private int row;
    private int column;
    
    public MoveRecord(int position, int value, int row, int column) {
        this.position = position;
        this.value = value;
        this.row = row;
        this.column = column;
    }
    
    public int getPosition() {
        return position;
    }
    
    public int getValue() {
        return value;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getColumn() {
        return column;
    }
}
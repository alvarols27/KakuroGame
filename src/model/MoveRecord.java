package model;

public class MoveRecord {
    private int row;
    private int column;
    private int oldValue;
    private int newValue;

    public MoveRecord(int row, int column, int oldValue, int newValue) {
        this.row = row;
        this.column = column;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getOldValue() {
        return oldValue;
    }
    
    public int getNewValue() {
        return newValue;
    }
}
package model;

public class BlueCell extends Cell {
    private int targetSum;
    private int row;
    private int column;
    private boolean isRed;
    
    public BlueCell(int row, int column) {
        super(0, true);
        this.row = row;
        this.column = column;
        this.targetSum = 0;
        this.isRed = false;
    }
    
    public int getTargetSum() {
        return targetSum;
    }
    
    public void setTargetSum(int targetSum) {
        this.targetSum = targetSum;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getColumn() {
        return column;
    }
    
    public boolean isRed() {
        return isRed;
    }
    
    public void setRedBackground(boolean isRed) {
        this.isRed = isRed;
    }

    public boolean isHorizontal() { return false; /* To be implemented if needed */ }
    public boolean isVertical() { return false; /* To be implemented if needed */ }
}
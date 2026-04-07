package model;

public class BlueCell {
    private int targetSum;
    private int row;
    private int column;
    private boolean isRed;
    
    public BlueCell(int row, int column) {
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
}
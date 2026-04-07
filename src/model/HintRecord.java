package model;

import java.util.Date;

public class HintRecord {
    private Date timestamp;
    private int cellRow;
    private int cellColumn;
    private int suggestedValue;
    
    public HintRecord(int row, int column, int suggestedValue) {
        this.timestamp = new Date();
        this.cellRow = row;
        this.cellColumn = column;
        this.suggestedValue = suggestedValue;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public int getCellRow() {
        return cellRow;
    }
    
    public int getCellColumn() {
        return cellColumn;
    }
    
    public int getSuggestedValue() {
        return suggestedValue;
    }
}
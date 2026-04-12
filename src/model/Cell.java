package model;

public class Cell {
    private int value;
    private boolean isReadOnly;
    private int rowSumHint;
    private int colSumHint; 
    private boolean isSelected;
    private boolean hasError;
    public Cell() {
        this.value = 0;
        this.isReadOnly = false;
    }

    public Cell(int value, boolean isReadOnly) {
        this.value = value;
        this.isReadOnly = isReadOnly;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (!isReadOnly) {
            this.value = value;
        }
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }

    public int getRowSumHint() {
        return rowSumHint;
    }

    public void setRowSumHint(int rowSumHint) {
        this.rowSumHint = rowSumHint;
        this.isReadOnly = true;
    }

    public int getColSumHint() {
        return colSumHint;
    }

    public void setColSumHint(int colSumHint) {
        this.colSumHint = colSumHint;
        this.isReadOnly = true;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean hasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }
}

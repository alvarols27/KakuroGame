package model;

public class Grid {
    private Cell[][] cells;
    private int[][] solution;
    private int size;

    public Grid(int size) {
        this.size = size;
        cells = new Cell[size][size];
        solution = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    public void setSolutionValue(int row, int col, int value) {
        solution[row][col] = value;
    }

    public int getSolutionValue(int row, int col) {
        return solution[row][col];
    }

    public void setCellValue(int row, int col, int value) {
        cells[row][col].setValue(value);
    }

    public int getCellValue(int row, int col) {
        return cells[row][col].getValue();
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public void initializeGrid() {
        // Logic handled during generation
    }

    public void validateRow(int row) { /* Validation logic */ }
    public void validateColumn(int col) { /* Validation logic */ }

    public boolean checkSolution() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!cells[i][j].isReadOnly()) {
                    if (cells[i][j].getValue() != solution[i][j]) return false;
                }
            }
        }
        return true;
    }

    public void resetGrid() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!cells[i][j].isReadOnly()) {
                    cells[i][j].setValue(0);
                }
            }
        }
    }

    public int getSize() {
        return size;
    }
}

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

    public int getSize() {
        return size;
    }
}

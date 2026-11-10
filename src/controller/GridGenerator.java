package controller;

import model.Grid;
import java.util.Random;
import interfaces.Difficulty;

public class GridGenerator {
    private static final Random random = new Random();

    public static Grid generateRandomGrid(Difficulty difficulty) {
        int size = difficulty.getGridSize();
        boolean easyMode = difficulty.getName().equals("Easy");
        return generateStructuredGrid(size, easyMode);
    }

    private static Grid generateStructuredGrid(int size, boolean easyMode) {
        Grid grid = new Grid(size);
        
        // Set all cells as read-only initially (clue cells)
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid.getCell(i, j).setReadOnly(true);
            }
        }

        if (size == 5) {
            createRandomEasyPattern(grid);
        } else if (size == 7) {
            createRandomMediumPattern(grid);
        } else if (size == 9) {
            createRandomHardPattern(grid);
        }

        if (!fillDigits(grid, size)) {
            return generateStructuredGrid(size, easyMode);
        }

        calculateClues(grid, size);

        // Pre-fill some cells for Easy mode
        if (easyMode && size == 5) {
            prefillEasyMode(grid, size);
        }

        return grid;
    }

    // ==================== EASY MODE (5x5) ====================
    private static void createRandomEasyPattern(Grid grid) {
        int patternType = random.nextInt(3);
        
        int[][] pattern;
        switch (patternType) {
            default:
                pattern = EASY_PATTERN_1;
        }
        
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                grid.getCell(i, j).setReadOnly(pattern[i][j] == 1);
            }
        }
    }

    private static final int[][] EASY_PATTERN_1 = {
        {1, 1, 1, 1, 1},
        {1, 0, 0, 1, 1},
        {1, 0, 0, 0, 1},
        {1, 0, 0, 0, 1},
        {1, 1, 1, 1, 1}
    };

    // ==================== MEDIUM MODE (7x7) ====================
    private static void createRandomMediumPattern(Grid grid) {
        int patternType = random.nextInt(3);
        
        int[][] pattern;
        switch (patternType) {
            default:
                pattern = MEDIUM_PATTERN_1;
        }
        
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                grid.getCell(i, j).setReadOnly(pattern[i][j] == 1);
            }
        }
    }
    
    private static final int[][] MEDIUM_PATTERN_1 = {
        {1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 1},
        {1, 1, 0, 0, 0, 1, 1},
        {1, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1}
    };
    
    // ==================== HARD MODE (9x9) ====================
    private static void createRandomHardPattern(Grid grid) {
        int patternType = random.nextInt(3);
        
        int[][] pattern;
        switch (patternType) {
            default:
                pattern = HARD_PATTERN_1;
                break;
        }
        
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                grid.getCell(i, j).setReadOnly(pattern[i][j] == 1);
            }
        }
    }

    private static final int[][] HARD_PATTERN_1 = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 1, 0, 0, 1, 1},
        {1, 0, 0, 0, 1, 0, 0, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 0, 0, 1, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 0, 1, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    
    // ==================== Helper Methods ====================
    private static void prefillEasyMode(Grid grid, int size) {
        for (int i = 1; i < size; i++) {
            for (int j = 1; j < size; j++) {
                if (!grid.getCell(i, j).isReadOnly() && random.nextDouble() < 0.20) {
                    grid.setCellValue(i, j, grid.getSolutionValue(i, j));
                }
            }
        }
    }

    private static boolean fillDigits(Grid grid, int size) {
        for (int i = 1; i < size; i++) {
            for (int j = 1; j < size; j++) {
                if (!grid.getCell(i, j).isReadOnly()) {
                    java.util.List<Integer> available = getAvailableDigits(grid, i, j, size);
                    if (available.isEmpty()) return false;
                    grid.setSolutionValue(i, j, available.get(random.nextInt(available.size())));
                }
            }
        }
        return true;
    }

    private static java.util.List<Integer> getAvailableDigits(Grid grid, int r, int c, int size) {
        boolean[] used = new boolean[10];
        for (int j = c - 1; j >= 0 && !grid.getCell(r, j).isReadOnly(); j--) {
            used[grid.getSolutionValue(r, j)] = true;
        }
        for (int i = r - 1; i >= 0 && !grid.getCell(i, c).isReadOnly(); i--) {
            used[grid.getSolutionValue(i, c)] = true;
        }
        
        java.util.List<Integer> avail = new java.util.ArrayList<>();
        for (int d = 1; d <= 9; d++) if (!used[d]) avail.add(d);
        return avail;
    }

    private static void calculateClues(Grid grid, int size) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid.getCell(i, j).isReadOnly()) {
                    if (j < size - 1 && !grid.getCell(i, j + 1).isReadOnly()) {
                        int rowSum = 0;
                        for (int jj = j + 1; jj < size && !grid.getCell(i, jj).isReadOnly(); jj++) {
                            rowSum += grid.getSolutionValue(i, jj);
                        }
                        grid.getCell(i, j).setRowSumHint(rowSum);
                    }
                    if (i < size - 1 && !grid.getCell(i + 1, j).isReadOnly()) {
                        int colSum = 0;
                        for (int ii = i + 1; ii < size && !grid.getCell(ii, j).isReadOnly(); ii++) {
                            colSum += grid.getSolutionValue(ii, j);
                        }
                        grid.getCell(i, j).setColSumHint(colSum);
                    }
                }
            }
        }
    }
}
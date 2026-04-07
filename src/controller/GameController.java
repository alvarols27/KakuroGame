package controller;

import view.SettingsView;
import interfaces.GameUI;
import interfaces.Difficulty;
import model.*;
import view.DashboardView;
import view.GameView;
import view.LoginView;
import database.GameDAO;
import database.SavedGameDAO;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.Timer;

public class GameController {
    private GameUI gameUI;
    private JTextField[][] textFields;
    private Grid grid;
    private Player player;
    private Difficulty difficulty;
    private GameSession session;
    private Timer timer;
    private int secondsElapsed;
    private boolean noteMode = false;
    private boolean victoryShown = false;
    private SimpleDateFormat timeFormat;

    public GameController(GameUI gameUI, Grid grid, Player player, Difficulty difficulty) {
        this(gameUI, grid, player, difficulty, null);
    }

    public GameController(GameUI gameUI, Grid grid, Player player, Difficulty difficulty, 
                          GameSession existingSession) {
        this.gameUI = gameUI;
        this.grid = grid;
        this.player = player;
        this.difficulty = difficulty;
        this.textFields = ((GameView) gameUI).getTextFields();
        this.timeFormat = new SimpleDateFormat("mm:ss");
        
        if (existingSession != null) {
            this.session = existingSession;
            this.secondsElapsed = existingSession.getTimer();
            this.grid = existingSession.getGrid();
        } else {
            this.session = new GameSession(player, difficulty);
            this.session.setGrid(grid);
            this.secondsElapsed = 0;
        }
        
        gameUI.setHintsRemaining(session.getHintsRemaining());
        startTimer();
        initListeners();
    }

    private void startTimer() {
        timer = new Timer(1000, e -> {
            secondsElapsed++;
            session.incrementTimer();
            int mins = secondsElapsed / 60;
            int secs = secondsElapsed % 60;
            gameUI.updateTimer(String.format("Time: %02d:%02d", mins, secs));
            
            //apply roasting message to game timer
            if (secondsElapsed > 900 && secondsElapsed % 5 == 0) {
                popUpRoastingMessage();
            }
        });
        timer.start();
    }

    private void initListeners() {
        gameUI.setSaveAction(e -> saveAndExit());
        gameUI.setHintAction(e -> provideHint());
        gameUI.setUndoAction(e -> handleUndo());
        gameUI.setRedoAction(e -> handleRedo());
        gameUI.setNoteModeAction(e -> toggleNoteMode());
        gameUI.setSettingsAction(e -> openSettings());
        gameUI.setDashboardAction(e -> openDashboard());
        
        //for real-time validation on every cell change
        gameUI.setCellInputListener((row, col) -> {
            if (noteMode) return; {
                String text = textFields[row][col].getText();
                int newValue = text.isEmpty() ? 0 : Integer.parseInt(text);
                int oldValue = grid.getCellValue(row, col);

                if (oldValue != newValue) {
                    session.recordMove(row, col, oldValue, newValue);
                    grid.setCellValue(row, col, newValue);
                }
                
                validateRealTime();
                checkVictory();
            }
        });
    }

    // ==================== REAL-TIME VALIDATION ====================
    private void validateRealTime() {
        int size = grid.getSize();
        
        //clear colours when white cells are empty
        // for (int i = 0; i < size; i++) {
        //     for (int j = 0; j < size; j++) {
        //         if (!grid.getCell(i, j).isReadOnly()) {
        //             gameUI.clearHighlight(i, j);
        //         }
        //         if (grid.getCell(i, j).isReadOnly()) {
        //             ((GameView) gameUI).setClueCellCorrect(i, j, false);
        //             ((GameView) gameUI).setClueCellError(i, j, false);
        //         }
        //     }
        // }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid.getCell(i, j).isReadOnly()) {
                    gameUI.resetClueCellColors(i, j);
                }
            }
        }
        
        //to track duplicates
        boolean[][] hasDuplicate = new boolean[size][size];
        
        //check for duplicates in row
        for (int row = 0; row < size; row++) {
            Map<Integer, List<Integer>> valuePositions = new HashMap<>();
            for (int col = 0; col < size; col++) {
                if (!grid.getCell(row, col).isReadOnly()) {
                    int value = grid.getCellValue(row, col);
                    if (value > 0) {
                        valuePositions.computeIfAbsent(value, k -> new ArrayList<>()).add(col);
                    }
                }
            }
            for (Map.Entry<Integer, List<Integer>> entry : valuePositions.entrySet()) {
                if (entry.getValue().size() > 1) {
                    for (int col : entry.getValue()) {
                        hasDuplicate[row][col] = true;
                    }
                }
            }
        }
        
        //check for duplicates in columns
        for (int col = 0; col < size; col++) {
            Map<Integer, List<Integer>> valuePositions = new HashMap<>();
            for (int row = 0; row < size; row++) {
                if (!grid.getCell(row, col).isReadOnly()) {
                    int value = grid.getCellValue(row, col);
                    if (value > 0) {
                        valuePositions.computeIfAbsent(value, k -> new ArrayList<>()).add(row);
                    }
                }
            }
            for (Map.Entry<Integer, List<Integer>> entry : valuePositions.entrySet()) {
                if (entry.getValue().size() > 1) {
                    for (int row : entry.getValue()) {
                        hasDuplicate[row][col] = true;
                    }
                }
            }
        }
        
        //validate clue cells
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Cell cell = grid.getCell(i, j);
                if (cell.isReadOnly()) {
                    if (cell.getRowSumHint() > 0) {
                        validateHorizontalRunWithReturn(i, j, cell.getRowSumHint());
                    }
                    if (cell.getColSumHint() > 0) {
                        validateVerticalRunWithReturn(i, j, cell.getColSumHint());
                    }
                }
            }
        }
        
        // AUTO-SUBMIT when grid is completely correct
        if (isGridCompletelyCorrect()) {
            checkVictory();
        }
    }

    private boolean validateHorizontalRunWithReturn(int clueRow, int clueCol, int targetSum) {
        int size = grid.getSize();
        Set<Integer> seen = new HashSet<>();
        int currentSum = 0;
        boolean allFilled = true;
        boolean hasDuplicate = false;
    
        for (int col = clueCol + 1; col < size && !grid.getCell(clueRow, col).isReadOnly(); col++) {
            int value = grid.getCellValue(clueRow, col);
    
            if (value == 0) {
                allFilled = false;
                continue;
            }
    
            if (!seen.add(value)) {
                hasDuplicate = true;
            }
    
            currentSum += value;
        }
    
        if (allFilled && !hasDuplicate && currentSum == targetSum) {
            //row correct => top right triangle green
            gameUI.setClueCellRowCorrect(clueRow, clueCol, true);
            return true;
        }
    
        if (hasDuplicate || currentSum > targetSum || (allFilled && currentSum != targetSum)) {
            // row has error => set top right triangle red
            gameUI.setClueCellRowError(clueRow, clueCol, true);
            return false;
        }
        
        // Not complete yet - normal color
        gameUI.resetClueCellColors(clueRow, clueCol);
        return false;
    }
    
    private boolean validateVerticalRunWithReturn(int clueRow, int clueCol, int targetSum) {
        int size = grid.getSize();
        Set<Integer> seen = new HashSet<>();
        int currentSum = 0;
        boolean allFilled = true;
        boolean hasDuplicate = false;
    
        for (int row = clueRow + 1; row < size && !grid.getCell(row, clueCol).isReadOnly(); row++) {
            int value = grid.getCellValue(row, clueCol);
    
            if (value == 0) {
                allFilled = false;
                continue;
            }
    
            if (!seen.add(value)) {
                hasDuplicate = true;
            }
    
            currentSum += value;
        }
    
        if (allFilled && !hasDuplicate && currentSum == targetSum) {
            //column is correct => set bottom left triangle green
            gameUI.setClueCellColCorrect(clueRow, clueCol, true);
            return true;
        }
    
        if (hasDuplicate || currentSum > targetSum || (allFilled && currentSum != targetSum)) {
            // column has error => set bottom left triangle red
            gameUI.setClueCellColError(clueRow, clueCol, true);
            return false;
        }
        
        // Not complete yet - normal color
        gameUI.resetClueCellColors(clueRow, clueCol);
        return false;
    }
    
    private boolean isGridCompletelyCorrect() {
        int size = grid.getSize();
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Cell cell = grid.getCell(i, j);
                
                //check white cells have correct values
                if (!cell.isReadOnly()) {
                    if (cell.getValue() != grid.getSolutionValue(i, j)) {
                        return false;
                    }
                }
            }
        }
        
        //to verify all clue cells are GREEN (sums correct)
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Cell cell = grid.getCell(i, j);
                if (cell.isReadOnly()) {

                    if (cell.getRowSumHint() > 0) {
                        int sum = calculateHorizontalSum(i, j);
                        if (sum != cell.getRowSumHint()) {
                            return false;
                        }
                    }

                    if (cell.getColSumHint() > 0) {
                        int sum = calculateVerticalSum(i, j);
                        if (sum != cell.getColSumHint()) {
                            return false;
                        }
                    }
                }
            }
        }
        
        return true;
    }

    private int calculateHorizontalSum(int clueRow, int clueCol) {
        int size = grid.getSize();
        int sum = 0;
        for (int col = clueCol + 1; col < size && !grid.getCell(clueRow, col).isReadOnly(); col++) {
            sum += grid.getCellValue(clueRow, col);
        }
        return sum;
    }

    private int calculateVerticalSum(int clueRow, int clueCol) {
        int size = grid.getSize();
        int sum = 0;
        for (int row = clueRow + 1; row < size && !grid.getCell(row, clueCol).isReadOnly(); row++) {
            sum += grid.getCellValue(row, clueCol);
        }
        return sum;
    }

    // ==================== HINT SYSTEM ====================

    private void provideHint() {
        if (session.useHint()) {
            gameUI.setHintsRemaining(session.getHintsRemaining());
            
            int size = grid.getSize();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    Cell cell = grid.getCell(i, j);
                    if (cell != null && !cell.isReadOnly() && cell.getValue() == 0) {
                        int correctVal = grid.getSolutionValue(i, j);
                        
                        if (difficulty.requiresMathChallenge()) {
                            showMathChallenge(i, j, correctVal);
                        } else {
                            grid.setCellValue(i, j, correctVal);
                            textFields[i][j].setText(String.valueOf(correctVal));
                        }
                        validateRealTime();
                        checkVictory();
                        return;
                    }
                }
            }
            gameUI.showMessage("No empty cells found!");
        } else {
            gameUI.showMessage("No hints remaining!");
        }
    }

    //FIX THIS
    private void showMathChallenge(int row, int col, int correctValue) {
        int num1 = (int)(Math.random() * correctValue);
        if (num1 == 0) num1 = 1;
        if (num1 >= correctValue) num1 = correctValue - 1;
        
        final int expectedAnswer = correctValue - num1;
        String question = String.format("%d + ? = %d", num1, correctValue);
        
        JPanel panel = new JPanel();
        panel.add(new JLabel(question));
        JTextField answerField = new JTextField(5);
        panel.add(answerField);
        panel.add(new JLabel("You have 10 seconds!"));
        
        JDialog dialog = new JDialog((JFrame) gameUI, "Math Challenge", true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        
        Timer countdown = new Timer(1000, null);
        final int[] timeLeft = {10};
        
        countdown.addActionListener(e -> {
            timeLeft[0]--;
            dialog.setTitle("Math Challenge - " + timeLeft[0] + " sec");
            if (timeLeft[0] <= 0) {
                countdown.stop();
                dialog.dispose();
                secondsElapsed += difficulty.getMathPenaltySeconds();
                session.addTime(difficulty.getMathPenaltySeconds());
                gameUI.showMessage("Time's up! +" + difficulty.getMathPenaltySeconds() + " seconds penalty!");
            }
        });
        
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            countdown.stop();
            try {
                int answer = Integer.parseInt(answerField.getText());
                if (answer == expectedAnswer) {
                    grid.setCellValue(row, col, correctValue);
                    gameUI.updateCell(row, col, correctValue);
                    textFields[row][col].setText(String.valueOf(correctValue));
                    gameUI.showMessage("Correct! Hint applied.");
                    validateRealTime();
                    checkVictory();
                } else {
                    gameUI.showMessage("Wrong answer! No hint given.");
                }
            } catch (NumberFormatException ex) {
                gameUI.showMessage("Invalid answer!");
            }
            dialog.dispose();
        });
        
        panel.add(submitButton);
        countdown.start();
        dialog.setVisible(true);
    }

    // ==================== UNDO/REDO ====================

    private void handleUndo() {
        MoveRecord move = session.undo();
        if (move != null) {
            grid.setCellValue(move.getRow(), move.getColumn(), move.getValue());
            gameUI.updateCell(move.getRow(), move.getColumn(), move.getValue());
            if (textFields[move.getRow()][move.getColumn()] != null) {
                textFields[move.getRow()][move.getColumn()].setText(move.getValue() > 0 ? String.valueOf(move.getValue()) : "");
            }
            validateRealTime();
        } else {
            gameUI.showMessage("Nothing to undo!");
        }
    }

    private void handleRedo() {
        MoveRecord move = session.redo();
        if (move != null) {
            grid.setCellValue(move.getRow(), move.getColumn(), move.getValue());
            gameUI.updateCell(move.getRow(), move.getColumn(), move.getValue());
            if (textFields[move.getRow()][move.getColumn()] != null) {
                textFields[move.getRow()][move.getColumn()].setText(move.getValue() > 0 ? String.valueOf(move.getValue()) : "");
            }
            validateRealTime();
        } else {
            gameUI.showMessage("Nothing to redo!");
        }
    }

    // ==================== NOTE MODE ====================

    private void toggleNoteMode() {
        noteMode = !noteMode;
        session.toggleNoteMode();
        gameUI.showMessage(noteMode ? "Note Mode ON - Numbers will be added as notes" : "Note Mode OFF");
        gameUI.setNoteModeActive(noteMode);
    }

    // =================== ROAST MESSAGE ======================

    private void popUpRoastingMessage() {
        String message = "ahem...";
        
        if (secondsElapsed > 5) {
            String[] roasts = {
                "Are you okay? 🫠",
                "Did you take a nap? 💤",
                "My grandma solves faster! 👵",
                "Is this your first time? 🤔",
                "You know the numbers are 1-9, right? 📝"
            };
            String roast = roasts[(int)(Math.random() * roasts.length)];
            message += "\n\n" + roast;
        }

        gameUI.showMessage(message);
    }

    // ==================== VICTORY CHECK ====================

    private void checkVictory() {
        if (victoryShown) return;

        int size = grid.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Cell cell = grid.getCell(i, j);
                if (cell != null && !cell.isReadOnly()) {
                    if (cell.getValue() != grid.getSolutionValue(i, j)) {
                        return;
                    }
                }
            }
        }
        showVictory();
    }

    private void showVictory() {
        if (victoryShown) return;

        victoryShown = true;
        timer.stop();
        session.complete();
        
        GameDAO.saveGameResult(
            player,
            secondsElapsed,
            difficulty.getName(),
            session.getHintsUsed()
        );
        
        SavedGameDAO.deleteAllSavedGamesForPlayer(player.getId());
        
        String message = String.format(
            "🎉 CONGRATULATIONS! 🎉\n\n" +
            "You solved the puzzle in %d seconds!\n" +
            "Difficulty: %s\n" +
            "Hints used: %d\n\n" +
            "Time: %s",
            secondsElapsed, 
            difficulty.getName(), 
            session.getHintsUsed(),
            timeFormat.format(new Date(secondsElapsed * 1000))
        );
        
        {
            String[] praises = {
                "Amazing solve! 🌟",
                "You're on fire! 🔥",
                "Well done there! 🎯",
                "Kakuro BOSS! 👑"
            };
            String praise = praises[(int)(Math.random() * praises.length)];
            message += "\n\n" + praise;
        }
        
        Object[] options = {"Play Again", "View Dashboard", "Exit"};
        int choice = JOptionPane.showOptionDialog(
            (JFrame) gameUI,
            message,
            "Victory!",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null, options, options[0]
        );
        
        if (choice == 0) {
            restartGame();
        } else if (choice == 1) {
            openDashboard();
        } else {
            gameUI.close();
            returnToLoginView();
        }
    }

    // ==================== SAVE & EXIT ====================

    private void saveAndExit() {
        timer.stop();
        session.save();
        SavedGameDAO.saveGameSession(session);
        gameUI.showMessage("Game saved! Time: " + secondsElapsed + "s");
        gameUI.close();
        returnToLoginView();
    }

    // ==================== NAVIGATION ====================

    private void restartGame() {
        gameUI.close();
        Grid newGrid = GridGenerator.generateRandomGrid(difficulty);
        GameView newView = new GameView(newGrid);
        new GameController(newView, newGrid, player, difficulty);
    }

    private void openSettings() {
        timer.stop(); //pause timer while in settings
        SettingsView settingsView = new SettingsView();
        new SettingsController(settingsView, player);
    
        //add window listener to resume timer when settings closed
        settingsView.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                timer.start();
            }
        });
    }

    private void openDashboard() {
        timer.stop();

        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Open Dashboard? Current progress will be lost. \nSave current game to store it.",
            "Dashboard",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            timer.start();
            return;
        }

        gameUI.close();
        DashboardView dashboardView = new DashboardView();
        new DashboardController(dashboardView, player);
    }
    
    private void returnToLoginView() {
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            new AuthController(loginView, new database.FilePlayerRepository());
        });
    }
}
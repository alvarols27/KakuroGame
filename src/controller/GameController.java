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

import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.Timer;

public class GameController {
    private GameUI gameUI; //controller doesnt know how the interface works cause GameUI takes care of it
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
            if (secondsElapsed % 180 == 0) {
                popUpRoastingMessage();
            }
        });
        timer.start();
    }

    private void initListeners() {
        gameUI.setSaveGameAction(e -> saveGame());
        gameUI.setHintAction(e -> requestHint());
        gameUI.setUndoAction(e -> handleUndo());
        gameUI.setRedoAction(e -> handleRedo());
        gameUI.setNoteModeAction(e -> toggleNoteMode());
        gameUI.setSettingsAction(e -> openSettings());
        gameUI.setDashboardAction(e -> openDashboard());
        
        //for real-time validation on every cell change
        gameUI.enterValue((row, col) -> {
            if (noteMode) return; {
                String text = textFields[row][col].getText();
                int newValue = text.isEmpty() ? 0 : Integer.parseInt(text);
                int oldValue = grid.getCellValue(row, col);

                if (oldValue != newValue) {
                    session.recordMove(row, col, oldValue, newValue); //this goes to GameSession, which has recordMove
                    grid.setCellValue(row, col, newValue);
                    
                    validateRowAndColum();
                    checkForRedCellsSubmitFinalGrid();
                }
            }
        });
    }

    // ==================== REAL-TIME VALIDATION ====================
private void validateRowAndColum() {
    int size = grid.getSize();
    
    // Reset clue cell colors
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            if (grid.getCell(i, j).isReadOnly()) {
                gameUI.resetClueCellColors(i, j);
            }
        }
    }
    
    // Track duplicates
    boolean[][] hasDuplicate = new boolean[size][size];
    
    // Check for duplicates in rows
    for (int row = 0; row < size; row++) {
        java.util.Map<Integer, java.util.List<Integer>> valuePositions = new java.util.HashMap<>();
        for (int col = 0; col < size; col++) {
            if (!grid.getCell(row, col).isReadOnly()) {
                int value = grid.getCellValue(row, col);
                if (value > 0) {
                    valuePositions.computeIfAbsent(value, k -> new java.util.ArrayList<>()).add(col);
                }
            }
        }
        for (java.util.Map.Entry<Integer, java.util.List<Integer>> entry : valuePositions.entrySet()) {
            if (entry.getValue().size() > 1) {
                for (int col : entry.getValue()) {
                    hasDuplicate[row][col] = true;
                }
            }
        }
    }
    
    // Check for duplicates in columns
    for (int col = 0; col < size; col++) {
        java.util.Map<Integer, java.util.List<Integer>> valuePositions = new java.util.HashMap<>();
        for (int row = 0; row < size; row++) {
            if (!grid.getCell(row, col).isReadOnly()) {
                int value = grid.getCellValue(row, col);
                if (value > 0) {
                    valuePositions.computeIfAbsent(value, k -> new java.util.ArrayList<>()).add(row);
                }
            }
        }
        for (java.util.Map.Entry<Integer, java.util.List<Integer>> entry : valuePositions.entrySet()) {
            if (entry.getValue().size() > 1) {
                for (int row : entry.getValue()) {
                    hasDuplicate[row][col] = true;
                }
            }
        }
    }
    
    // Validate clue cells
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
    checkForRedCellsSubmitFinalGrid();
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

    private void requestHint() {
        if (session.useHint()) {
            gameUI.setHintsRemaining(session.getHintsRemaining());
            
            int size = grid.getSize();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    Cell cell = grid.getCell(i, j);
                    if (cell != null && !cell.isReadOnly() && cell.getValue() == 0) {
                        final int row = i;
                        final int col = j;
                        final int correctVal = grid.getSolutionValue(i, j);
                        
                        if (difficulty.requiresMathChallenge()) {
                            generateMathChallenge(row, col, correctVal);
                        } else {
                            //only for easy and medium
                            grid.setCellValue(row, col, correctVal);
                            textFields[row][col].setText(String.valueOf(correctVal));
                            validateRowAndColum();
                            checkForRedCellsSubmitFinalGrid();
                        }
                        return;
                    }
                }
            }
            gameUI.showMessage("No empty cells found!");
        } else {
            gameUI.showMessage("No hints remaining!");
        }
    }
    
    private void generateMathChallenge(int row, int col, int correctValue) {
        String question;
        int expectedAnswer;
        
        int problemType = (int)(Math.random() * 4);
        
    switch (problemType) {
        case 0: 
            int multiplier = (int)(Math.random() * 5) + 2;
            int product = correctValue * multiplier;
            question = String.format("%d ÷ %d = ?", product, multiplier);
            expectedAnswer = correctValue;
            break;
            
        case 1: 
            int a = (int)(Math.random() * 5) + 2;
            int b = (int)(Math.random() * 10) + 1; 
            int c = (a * correctValue) - b;
            question = String.format("(%d × ?) - %d = %d", a, b, c);
            expectedAnswer = correctValue;
            break;
            
        case 2: 
            int percent = (int)(Math.random() * 4) + 2;
            int part = (correctValue * percent) / 10;
            question = String.format("%d%% of ? = %d", percent * 10, part);
            expectedAnswer = correctValue;
            break;
            
        default:
            int num1 = (int)(Math.random() * (correctValue / 2)) + 1;
            int num2 = (int)(Math.random() * (correctValue - num1)) + 1;
            int num3 = correctValue - num1 - num2;
            question = String.format("%d + %d + ? = %d", num1, num2, correctValue);
            expectedAnswer = num3;
            break;
        }
        
        JPanel panel = new JPanel();
        JLabel questionLabel = new JLabel(question);
        questionLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(questionLabel);
        
        JTextField answerField = new JTextField(5);
        answerField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        answerField.setHorizontalAlignment(JTextField.CENTER);
        panel.add(answerField);
        
        JLabel timerLabel = new JLabel("You have 10 seconds!");
        timerLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        panel.add(timerLabel);
        
        JDialog dialog = new JDialog((JFrame) gameUI, "Math Challenge", true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setSize(400, 100);
        dialog.setLocationRelativeTo(null);
        
        Timer countdown = new Timer(1000, null);
        final int[] timeLeft = {10};
        final boolean[] answerSubmitted = {false};
        final boolean[] timeExpired = {false};
        
        countdown.addActionListener(e -> {
            timeLeft[0]--;
            dialog.setTitle("Math Challenge - " + timeLeft[0] + " sec");
            if (timeLeft[0] <= 0 && !answerSubmitted[0]) {
                countdown.stop();
                timeExpired[0] = true;
                dialog.dispose();
                secondsElapsed += difficulty.getMathPenaltySeconds();
                session.addTime(difficulty.getMathPenaltySeconds());
                gameUI.showMessage("Time's up! +" + difficulty.getMathPenaltySeconds() + " seconds penalty!");
            }
        });
        
        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        submitButton.addActionListener(e -> {
            if (answerSubmitted[0]) return;
            
            countdown.stop();
            answerSubmitted[0] = true;
            
            try {
                int answer = Integer.parseInt(answerField.getText());
                if (answer == expectedAnswer && !timeExpired[0]) {
                    grid.setCellValue(row, col, correctValue);
                    textFields[row][col].setText(String.valueOf(correctValue));
                    gameUI.showMessage("Correct! Hint applied.");
                    validateRowAndColum();
                    checkForRedCellsSubmitFinalGrid();
                } else if (timeExpired[0]) {
                    gameUI.showMessage("Time expired! No hint given.");
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
        MoveRecord move = session.requestUndo();
        if (move != null) {
            grid.setCellValue(move.getRow(), move.getColumn(), move.getOldValue());
            
            if (textFields[move.getRow()][move.getColumn()] != null) {
                String displayValue = move.getOldValue() > 0 ? String.valueOf(move.getOldValue()) : "";
                textFields[move.getRow()][move.getColumn()].setText(displayValue);
            }
            
            gameUI.updateCell(move.getRow(), move.getColumn(), move.getOldValue());
            
            validateRowAndColum();
        } else {
            gameUI.showMessage("Nothing to undo!");
        }
    }

    private void handleRedo() {
        MoveRecord move = session.requestRedo();
        if (move != null) {
            grid.setCellValue(move.getRow(), move.getColumn(), move.getNewValue());
            
            if (textFields[move.getRow()][move.getColumn()] != null) {
                String displayValue = move.getNewValue() > 0 ? String.valueOf(move.getNewValue()) : "";
                textFields[move.getRow()][move.getColumn()].setText(displayValue);
            }
            
            // Update the GameUI
            gameUI.updateCell(move.getRow(), move.getColumn(), move.getNewValue());
            
            validateRowAndColum();
            
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
        
            String[] roasts = {
                "Are you okay?",
                "Did you take a nap? zZzZ",
                "My grandma solves faster!",
                "Is this your first time?",
                "You know the numbers are 1-9, right?"
            };
            String roast = roasts[(int)(Math.random() * roasts.length)];
            message += "\n\n" + roast;

        gameUI.showMessage(message);
    }

    // ==================== VICTORY CHECK ====================

    private void checkForRedCellsSubmitFinalGrid() {
        if (victoryShown) return;

        int size = grid.getSize();
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!grid.getCell(i, j).isReadOnly()) {
                    if (grid.getCellValue(i, j) == 0) {
                        return; 
                    }
                }
            }
        }
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Cell cell = grid.getCell(i, j);
                if (cell.isReadOnly()) {
                    if (cell.getRowSumHint() > 0) {
                        int sum = calculateHorizontalSum(i, j);
                        if (sum != cell.getRowSumHint()) {
                            return; 
                        }
                    }
                    if (cell.getColSumHint() > 0) {
                        int sum = calculateVerticalSum(i, j);
                        if (sum != cell.getColSumHint()) {
                            return; 
                        }
                    }
                }
            }
        }
        
        for (int row = 0; row < size; row++) {
            Set<Integer> seen = new HashSet<>();
            for (int col = 0; col < size; col++) {
                if (!grid.getCell(row, col).isReadOnly()) {
                    int value = grid.getCellValue(row, col);
                    if (seen.contains(value)) {
                        return;
                    }
                    seen.add(value);
                }
            }
        }
        
        for (int col = 0; col < size; col++) {
            Set<Integer> seen = new HashSet<>();
            for (int row = 0; row < size; row++) {
                if (!grid.getCell(row, col).isReadOnly()) {
                    int value = grid.getCellValue(row, col);
                    if (seen.contains(value)) {
                        return;
                    }
                    seen.add(value);
                }
            }
        }
        showVictoryAndRecordFinalStats();
    }

    private void showVictoryAndRecordFinalStats() {
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
                "Amazing solve!",
                "You're on fire!",
                "Well done there!",
                "Kakuro BOSS!"
            };
            String praise = praises[(int)(Math.random() * praises.length)];
            message += "\n\n" + praise;
        }
        
        Object[] options = {"View Dashboard", "Exit"};
        int choice = JOptionPane.showOptionDialog(
            (JFrame) gameUI,
            message,
            "Victory!",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null, options, options[0]
        );
        
        if (choice == 0) {
            openDashboard();
        } else {
            gameUI.close();
            returnToLoginView();
        }
    }

    // ==================== SAVE & EXIT ====================

    private void saveGame() {
        timer.stop();
        session.save();
        SavedGameDAO.saveGameSession(session);
        gameUI.showMessage("Game saved! Time: " + secondsElapsed + "s");
        gameUI.close();
        goToDashboardView();
    }

    // ==================== NAVIGATION ====================

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
            "Open Dashboard? Current progress will be lost. \nSave current game to store it. \nIf you won, you're safe to go!",
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
            new AuthController(loginView);
        });
    }

    private void goToDashboardView() {
        SwingUtilities.invokeLater(() -> {
            DashboardView dashboardView = new DashboardView();
            new DashboardController(dashboardView, player);
        });
    }
}
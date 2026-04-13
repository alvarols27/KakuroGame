package controller;

import interfaces.DashboardUI;
import interfaces.Difficulty;
import model.*;
import model.difficulty.*;
import view.GameView;
import view.LoginView;
import view.SettingsView;
import database.SavedGameDAO;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class DashboardController {
    private DashboardUI dashboardUI;
    private Dashboard dashboard;
    private Player player;
    private SimpleDateFormat dateFormat;
    
    public DashboardController(DashboardUI dashboardUI, Player player) {
        this.dashboardUI = dashboardUI;
        this.player = player;
        this.dashboard = new Dashboard(player);
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        
        initListeners();
        refreshDisplay();
    }

    private void initListeners() {
        dashboardUI.setBackAction(e -> logout());
        dashboardUI.setNewGameAction(e -> startNewGame());
        dashboardUI.setLoadSavedAction(e -> loadSavedGame());
        dashboardUI.setDeleteSavedAction(e -> deleteSavedGame());
        dashboardUI.setSettingsAction(e -> openSettings());
    }

    private void refreshDisplay(){
        dashboard.refresh();
        //update stats
        StatsSummary stats = dashboard.getStatsSummary();
        dashboardUI.updateStatsDisplay(
            String.valueOf(stats.getTotalGamesPlayed()),
            String.format("%d sec", (int) stats.getAverageTime()),
            String.valueOf(stats.getTotalHintsUsed()),
            stats.getFavoriteDifficulty(),
            stats.getLastPlayedDate() != null ? dateFormat.format(stats.getLastPlayedDate()) : "Never"
        );

        //update best scores
        List<BestScore> bestScores = dashboard.getBestScores();
        String easyBest = "--", easyDate = "--";
        String mediumBest = "--", mediumDate = "--";
        String hardBest = "--", hardDate = "--";

        for (BestScore bs : bestScores) {
            switch (bs.getDifficulty()) {
                case "Easy":
                    easyBest = bs.getBestTime() + " sec";
                    easyDate = dateFormat.format(bs.getDateAchieved());
                    break;
                case "Medium":
                    mediumBest = bs.getBestTime() + " sec";
                    mediumDate = dateFormat.format(bs.getDateAchieved());
                    break;
                case "Hard":
                    hardBest = bs.getBestTime() + " sec";
                    hardDate = dateFormat.format(bs.getDateAchieved());
                    break;
            }
        }

        dashboardUI.updateBestScoresDisplay(easyBest, easyDate, mediumBest, mediumDate, hardBest, hardDate);

        //update game history
        List<GameResult> allGames = dashboard.requestPersonalStats();
        String[][] historyData = new String[allGames.size()][4]; //rows(number of games) || columns(4 infos: date, difficulty, timeplayed, n of hints used)
        for (int i = 0; i < allGames.size(); i++) {
            GameResult gr = allGames.get(i);
            historyData[i][0] = dateFormat.format(gr.getDate());
            historyData[i][1] = gr.getDifficulty();
            historyData[i][2] = String.valueOf(gr.getTimePlayed());
            historyData[i][3] = String.valueOf(gr.getHintsUsed());
        }
        dashboardUI.updateGameHistoryDisplay(historyData);

        //update saved games
        List<SavedGameInfo> savedGames = SavedGameDAO.getSavedGames(player.getId());

        Object[][] savedData = new Object[savedGames.size()][5];
        for (int i = 0; i < savedGames.size(); i++) {
            SavedGameInfo sg = savedGames.get(i);
            savedData[i][0] = sg.getSessionId();
            savedData[i][1] = sg.getLastSavedDate();
            savedData[i][2] = sg.getDifficulty();
            savedData[i][3] = sg.getTimer();
        }
        dashboardUI.updateSavedGamesDisplay(savedData);
    }

    private void startNewGame() {
        // get default diff from SettingsController
        String savedDifficulty = SettingsController.getSavedDifficulty(player.getUsername());
        
        String[] difficulties = { "Easy", "Medium", "Hard"};
        
        JLabel messageLabel = new JLabel("Select Difficulty:");
        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        JComboBox<String> comboBox = new JComboBox<>(difficulties);
        comboBox.setFont(new Font("SansSerif", Font.BOLD, 14));
        comboBox.setSelectedItem(savedDifficulty); //this appears when selecting a new game difficulty

        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.add(messageLabel, BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(
            (java.awt.Component) dashboardUI,
            panel,
            "New Game",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        String selected = null;
        if (result == JOptionPane.OK_OPTION) {
            selected = (String) comboBox.getSelectedItem();
        }

        if (selected != null){
            dashboardUI.close();

            Difficulty difficulty;
            switch (selected) {
                case "Medium":
                    difficulty = new MediumDifficulty();
                    break;
                case "Hard":
                    difficulty = new HardDifficulty();
                    break;
                default:
                    difficulty = new EasyDifficulty();
            }

            Grid grid = GridGenerator.generateRandomGrid(difficulty);
            GameView gameView = new GameView(grid);
            new GameController(gameView, grid, player, difficulty);
        }
    }

    private void loadSavedGame() {
        int sessionId = dashboardUI.getSelectedSavedGameId();
        if (sessionId <= 0) {
            dashboardUI.showError("Please select a saved game to load");
            return;
        }

        GameSession loadedSession = SavedGameDAO.loadGameSession(sessionId);
        if (loadedSession != null) {
            dashboardUI.close();
            GameView gameView = new GameView(loadedSession.getGrid());
            new GameController(gameView, loadedSession.getGrid(), player, loadedSession.getDifficulty(), loadedSession);
        } else {
            dashboardUI.showError("Failed to load the saved game");
        }
    }

    private void deleteSavedGame() {
        int sessionId = dashboardUI.getSelectedSavedGameId();
        if (sessionId <= 0) {
            dashboardUI.showError("Please select a saved game to delete.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            (java.awt.Component) dashboardUI,
            "Are you sure you want to delete this saved game?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            SavedGameDAO.deleteSavedGame(sessionId);
            refreshDisplay();
            dashboardUI.showError("Saved game deleted.");
        }
    }

    private void logout() {
        dashboardUI.close();
        javax.swing.SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            new AuthController(loginView);
        });
    }

    private void openSettings() {
        SettingsView settingsView = new SettingsView();
        new SettingsController(settingsView, player);
    }
}

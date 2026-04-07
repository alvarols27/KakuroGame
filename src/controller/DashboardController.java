package controller;

import interfaces.DashboardUI;
import interfaces.Difficulty;
import interfaces.SavedGameInfo;
import model.*;
import model.difficulty.*;
import view.GameView;
import view.LoginView;
import view.SettingsView;
// import database.GameDAO;
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
        dashboardUI.setBackAction(e -> goBack());
        dashboardUI.setRefreshAction(e -> refreshDisplay());
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
        List<database.MockDatabase.GameResult> allGames = dashboard.requestPersonalStats();
        String[][] historyData = new String[allGames.size()][4]; //rows(number of games) || columns(4 infos: date, difficulty, timeplayed, n of hints used)
        for (int i = 0; i < allGames.size(); i++) {
            database.MockDatabase.GameResult gr = allGames.get(i);
            historyData[i][0] = dateFormat.format(gr.getDate());
            historyData[i][1] = gr.getDifficulty();
            historyData[i][2] = String.valueOf(gr.getTimePlayed());
            historyData[i][3] = String.valueOf(gr.getHintsUsed());
        }
        dashboardUI.updateGameHistoryDisplay(historyData);

        //update saved games
        List<SavedGameInfo> savedGames = SavedGameDAO.getSavedGames(player.getId());
        String[][] savedData = new String[savedGames.size()][5];
        for (int i = 0; i < savedGames.size(); i++) {
            SavedGameInfo sg = savedGames.get(i);
            savedData[i][0] = String.valueOf(sg.getSessionId());
            savedData[i][1] = dateFormat.format(sg.getLastSavedDate());
            savedData[i][2] = sg.getDifficulty();
            savedData[i][3] = String.valueOf(sg.getTimer());
        }
        dashboardUI.updateSavedGamesDisplay(savedData);
    }

    private void startNewGame() {
        String[] difficulties = { "Easy", "Medium", "Hard"};

        JLabel messageLabel = new JLabel("Select Difficulty:");
        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        JComboBox<String> comboBox = new JComboBox<>(difficulties);
        comboBox.setFont(new Font("SansSerif", Font.BOLD, 14));

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

    //NOT SURE ABOUT THIS ONE
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

    private void goBack() {
        dashboardUI.close();
        javax.swing.SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            new AuthController(loginView, new database.FilePlayerRepository());
        });
    }

    private void openSettings() {
        SettingsView settingsView = new SettingsView();
        new SettingsController(settingsView, player);
    }
}

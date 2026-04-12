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
        dashboardUI.setBackAction(e -> goBack());
        dashboardUI.setRefreshAction(e -> refreshDisplay());
        dashboardUI.setNewGameAction(e -> startNewGame());
        dashboardUI.setLoadSavedAction(e -> loadSavedGame());
        dashboardUI.setDeleteSavedAction(e -> deleteSavedGame());
        dashboardUI.setSettingsAction(e -> openSettings());
    }

    private void refreshDisplay(){
        dashboard.refresh();
        // findAllDatabaseFiles();
        // searchForDbFiles(null, 0);

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
        String easyBestTime = "--", easyBestDate = "--";
        String mediumBestTime = "--", mediumBestDate = "--";
        String hardBestTime = "--", hardBestDate = "--";

        for (BestScore bs : bestScores) {
            String diff = bs.getDifficulty();
            String time = bs.getBestTime() + "s";
            String date = dateFormat.format(bs.getDateAchieved());

            if ("Easy".equals(diff)) {
                easyBestTime = time;
                easyBestDate = date;
            } else if ("Medium".equals(diff)) {
                mediumBestTime = time;
                mediumBestDate = date;
            } else if ("Hard".equals(diff)) {
                hardBestTime = time;
                hardBestDate = date;
            }
        }

        dashboardUI.updateBestScoresDisplay(easyBestTime, easyBestDate, mediumBestTime, mediumBestDate, hardBestTime, hardBestDate);

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
        String selected = SettingsController.getSavedDifficulty(player.getUsername());
        dashboardUI.close();

        Difficulty difficulty;
        switch (selected) {
            case "Hard":
                difficulty = new HardDifficulty();
                break;
            case "Medium":
                difficulty = new MediumDifficulty();
                break;
            default:
                difficulty = new EasyDifficulty();
        }

        Grid grid = GridGenerator.generateRandomGrid(difficulty);
        GameView gameView = new GameView(grid);
        new GameController(gameView, grid, player, difficulty);
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
            new AuthController(loginView);
        });
    }

    private void openSettings() {
        SettingsView settingsView = new SettingsView();
        new SettingsController(settingsView, player);
    }
}

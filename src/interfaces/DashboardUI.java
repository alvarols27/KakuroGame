package interfaces;

import java.awt.event.ActionListener;

public interface DashboardUI {
    void close();
    void showError(String message);
    void updateStatsDisplay(String totalGames, String avgTime, String totalHints, 
                            String favoriteDiff, String lastPlayed);
    void updateBestScoresDisplay(String easyBest, String easyDate,
                                  String mediumBest, String mediumDate,
                                  String hardBest, String hardDate);
    void updateGameHistoryDisplay(String[][] historyData);
    void updateSavedGamesDisplay(String[][] savedGamesData);
    void setBackAction(ActionListener listener);
    void setRefreshAction(ActionListener listener);
    void setNewGameAction(ActionListener listener);
    void setLoadSavedAction(ActionListener listener);
    void setDeleteSavedAction(ActionListener listener);
    int getSelectedSavedGameId();
    void setSettingsAction(ActionListener listener);
}
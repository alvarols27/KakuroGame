package interfaces;

import java.awt.event.ActionListener;
import java.util.function.BiConsumer;

public interface GameUI {
    void close();
    void updateTimer(String time);
    void showVictoryMessage(String message, int seconds, int hintsUsed);
    void showErrorMessage(String message);
    void showMessage(String message);
    void updateCell(int row, int col, int value);
    void highlightError(int row, int col);
    void clearHighlight(int row, int col);
    void setSubmitEnabled(boolean enabled);
    void setNoteModeActive(boolean active);
    void setHintsRemaining(int remaining);
    void openSettings();
    void openDashboard();
    
    //for colouring in clueCell (half - half)
    void setClueCellCorrect(int row, int col, boolean isCorrect);
    void setClueCellError(int row, int col, boolean isError);
    void setClueCellRowCorrect(int row, int col, boolean isCorrect);
    void setClueCellRowError(int row, int col, boolean isError);
    void setClueCellColCorrect(int row, int col, boolean isCorrect);
    void setClueCellColError(int row, int col, boolean isError);
    void resetClueCellColors(int row, int col);
    
    //Button action setters
    void setSaveAction(ActionListener listener);
    void setHintAction(ActionListener listener);
    void setUndoAction(ActionListener listener);
    void setRedoAction(ActionListener listener);
    void setSettingsAction(ActionListener listener);
    void setDashboardAction(ActionListener listener);
    void setNoteModeAction(ActionListener listener);
    void setCellInputListener(BiConsumer<Integer, Integer> listener);
}
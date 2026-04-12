package interfaces;

import java.awt.event.ActionListener;

public interface SettingsUI {
    void close();
    void showError(String message);
    void showSuccess(String message);
    String getSelectedDifficulty();
    void setDifficultyOptions(String[] difficulties);
    void setBackAction(ActionListener listener);
    void setSaveAction(ActionListener listener);
}
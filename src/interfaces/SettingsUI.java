package interfaces;

import java.awt.event.ActionListener;

public interface SettingsUI {
    void close();
    void showError(String message);
    void showSuccess(String message);
    String getSelectedTheme();
    String getSelectedDifficulty();
    void setThemeOptions(String[] themes);
    void setDifficultyOptions(String[] difficulties);
    void setBackAction(ActionListener listener);
    void setSaveAction(ActionListener listener);
}
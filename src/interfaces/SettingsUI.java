package interfaces;

import java.awt.event.ActionListener;

public interface SettingsUI {
    void close();
    void showError(String message);
    void showSuccess(String message);
    String getSelectedTheme();
    String getSelectedDifficulty();
    void setSelectedTheme(String theme);
    void setSelectedDifficulty(String difficulty);
    void setBackAction(ActionListener listener);
    void setSaveAction(ActionListener listener);
}
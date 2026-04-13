package interfaces;

import java.awt.event.ActionListener;

public interface SettingsUI {
    void applySettings();
    void showError(String message);
    void showSuccess(String message);
    String getDifficulty();
    void setDifficulty(String difficulty);
    void setBackAction(ActionListener listener);
    void setSaveAction(ActionListener listener);
}
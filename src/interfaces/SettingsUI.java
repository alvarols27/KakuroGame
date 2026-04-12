package interfaces;

import java.awt.event.ActionListener;

public interface SettingsUI {
    void close();
    void showError(String message);
    void showSuccess(String message);
    String getSelectedDifficulty();
    void setSelectedDifficulty(String difficulty);
    void setBackAction(ActionListener listener);
    void setSaveAction(ActionListener listener);
}
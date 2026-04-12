package controller;

import interfaces.SettingsUI;
import model.Player;
import java.util.prefs.Preferences;

public class SettingsController {
    private SettingsUI settingsUI;
    private Player player;
    private static Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);
    
    public SettingsController(SettingsUI settingsUI, Player player) {
        this.settingsUI = settingsUI;
        this.player = player;
        
        initListeners();
        loadCurrentSettings();
    }
    
    private void initListeners() {
        settingsUI.setSaveAction(e -> saveSettings());
        settingsUI.setBackAction(e -> closeSettings());
    }
    
    public static String getSavedDifficulty(String username) {
        return prefs.get("difficulty_" + username, "Easy");
    }
    
    private void loadCurrentSettings() {
        String currentDiff = getSavedDifficulty(player.getUsername());
        settingsUI.setSelectedDifficulty(currentDiff);
    }
    
    private void saveSettings() {
        String newDifficulty = settingsUI.getSelectedDifficulty();
        prefs.put("difficulty_" + player.getUsername(), newDifficulty);
        settingsUI.showSuccess("Settings saved!\nDefault difficulty: " + newDifficulty);
    }
    
    private void closeSettings() {
        settingsUI.close();
    }
}
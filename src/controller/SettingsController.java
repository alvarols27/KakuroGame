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
        settingsUI.setBackAction(e -> applySettings());
    }
    
    public static String getSavedDifficulty(String username) {
        return prefs.get("difficulty_" + username, "Easy");
    }
    
    private void loadCurrentSettings() {
        String currentDiff = getSavedDifficulty(player.getUsername());
        settingsUI.setDifficulty(currentDiff);
    }
    
    public String getProfileName() { return player.getUsername(); }
    public void setProfileName(String name) { player.setUsername(name); }
    public String getProfileAvatar() { return "default"; }
    public void setProfileAvatar(String avatar) { }

    public String getDifficulty() { return settingsUI.getDifficulty(); }
    public void setDifficulty(String diff) { settingsUI.setDifficulty(diff); }
    public String getTheme() { return "Light"; }
    public void setTheme(String theme) { }

    public void saveSettings() {
        String newDifficulty = settingsUI.getDifficulty();
        prefs.put("difficulty_" + player.getUsername(), newDifficulty);
        settingsUI.showSuccess("Settings saved!\nDefault difficulty: " + newDifficulty);
    }
    
    public void applySettings() {
        settingsUI.applySettings();
    }
}
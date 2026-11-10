package controller;

import interfaces.SettingsUI;
import model.Player;
import java.util.prefs.Preferences;

public class SettingsController {
    private SettingsUI settingsUI;
    private Player player;
    private static final Preferences prefs =
            Preferences.userNodeForPackage(SettingsController.class);

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
    
    public static void setSavedDifficulty(String username, String difficulty) {
        prefs.put("difficulty_" + username, difficulty);
    }

    private void loadCurrentSettings() {
        String currentDiff = getSavedDifficulty(player.getUsername());
        String currentTheme = ThemeManager.isDarkMode() ? "Dark" : "Light";

        settingsUI.setSelectedDifficulty(currentDiff);
        settingsUI.setSelectedTheme(currentTheme);
    }

    private void saveSettings() {
        String newDifficulty = settingsUI.getSelectedDifficulty();
        String newTheme = settingsUI.getSelectedTheme();

        // Save difficulty for this specific user
        setSavedDifficulty(player.getUsername(), newDifficulty);

        boolean currentlyDark = ThemeManager.isDarkMode();

        if (newTheme.equals("Dark") && !currentlyDark) {
            ThemeManager.toggleTheme();
        } else if (newTheme.equals("Light") && currentlyDark) {
            ThemeManager.toggleTheme();
        }

        settingsUI.showSuccess(
                "Settings saved!\nTheme: " + newTheme +
                "\nDefault difficulty: " + newDifficulty
        );
    }

    private void closeSettings() {
        settingsUI.close();
    }
}
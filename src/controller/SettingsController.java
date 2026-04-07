package controller;

import interfaces.SettingsUI;
import model.Player;
import java.awt.Color;
import javax.swing.*;

public class SettingsController {
    private SettingsUI settingsUI;
    private Player player;
    private String currentTheme;
    private String currentDifficulty;
    
    public SettingsController(SettingsUI settingsUI, Player player) {
        this.settingsUI = settingsUI;
        this.player = player;
        
        //SIMPLIFIED BUT STILL GOTTA FINISH
        this.currentTheme = "Light";
        this.currentDifficulty = "Easy";
        
        initListeners();
        loadCurrentSettings();
    }
    
    private void initListeners() {
        settingsUI.setSaveAction(e -> saveSettings());
        settingsUI.setBackAction(e -> closeSettings());
    }
    
    private void loadCurrentSettings() {
        String[] themes = {currentTheme};
        String[] difficulties = {currentDifficulty};
        settingsUI.setThemeOptions(themes);
        settingsUI.setDifficultyOptions(difficulties);
    }
    
    private void saveSettings() {
        String newTheme = settingsUI.getSelectedTheme();
        String newDifficulty = settingsUI.getSelectedDifficulty();
        
        this.currentTheme = newTheme;
        this.currentDifficulty = newDifficulty;
        
        //PPLY THEME SIMPLIFIED STILL GOTTA FINISH
        if (newTheme.equals("Dark")) {
            UIManager.put("Panel.background", new Color(50, 50, 50));
            UIManager.put("Label.foreground", Color.WHITE);
        } else {
            UIManager.put("Panel.background", new Color(236, 240, 241));
            UIManager.put("Label.foreground", Color.BLACK);
        }
        
        settingsUI.showSuccess("Settings saved!\nDefault difficulty: " + newDifficulty);
    }
    
    private void closeSettings() {
        settingsUI.close();
    }
    
    public String getDefaultDifficulty() {
        return currentDifficulty;
    }
}
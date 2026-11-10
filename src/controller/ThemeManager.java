package controller;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class ThemeManager {
    private static final String THEME_KEY = "kakuro_theme";
    private static Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);

    public static boolean isDarkMode() {
        return "Dark".equals(prefs.get(THEME_KEY, "Light"));
    }

    public static void toggleTheme() {
        String newTheme = isDarkMode() ? "Light" : "Dark";
        prefs.put(THEME_KEY, newTheme);
        applyTheme();
    }

    public static void applyTheme() {
        boolean dark = isDarkMode();
        if (dark) {
            UIManager.put("Panel.background", new Color(43, 43, 43));
            UIManager.put("Label.foreground", new Color(200, 200, 200));
            UIManager.put("Button.background", new Color(60, 63, 65));
            UIManager.put("Button.foreground", Color.BLACK); 
            UIManager.put("ComboBox.background", new Color(60, 63, 65));
            UIManager.put("ComboBox.foreground", new Color(200, 200, 200));
            UIManager.put("TextField.background", new Color(45, 45, 45)); 
            UIManager.put("TextField.foreground", new Color(180, 180, 180)); 
            UIManager.put("PasswordField.background", new Color(45, 45, 45));
            UIManager.put("PasswordField.foreground", new Color(180, 180, 180));
            UIManager.put("OptionPane.background", new Color(43, 43, 43));
            UIManager.put("OptionPane.messageForeground", new Color(200, 200, 200));
            UIManager.put("TabbedPane.background", new Color(43, 43, 43));
            UIManager.put("TabbedPane.foreground", new Color(200, 200, 200));
            UIManager.put("Table.background", new Color(43, 43, 43));
            UIManager.put("Table.foreground", new Color(200, 200, 200));
            UIManager.put("TableHeader.background", new Color(60, 63, 65));
            UIManager.put("TableHeader.foreground", Color.BLACK);
        } else {
            UIManager.put("Panel.background", new Color(236, 240, 241));
            UIManager.put("Label.foreground", new Color(52, 73, 94));
            UIManager.put("Button.background", null);
            UIManager.put("Button.foreground", Color.BLACK);
            UIManager.put("ComboBox.background", Color.WHITE);
            UIManager.put("ComboBox.foreground", Color.BLACK);
            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("TextField.foreground", Color.BLACK);
            UIManager.put("PasswordField.background", Color.WHITE);
            UIManager.put("PasswordField.foreground", Color.BLACK);
            UIManager.put("OptionPane.background", Color.WHITE);
            UIManager.put("OptionPane.messageForeground", Color.BLACK);
            UIManager.put("TabbedPane.background", null);
            UIManager.put("TabbedPane.foreground", null);
            UIManager.put("Table.background", Color.WHITE);
            UIManager.put("Table.foreground", Color.BLACK);
            UIManager.put("TableHeader.background", null);
            UIManager.put("TableHeader.foreground", Color.BLACK);
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        for (java.awt.Window window : java.awt.Window.getWindows()) {
            if (window instanceof JFrame && !(window.getClass().getSimpleName().equals("GameView"))) {
            }
            SwingUtilities.updateComponentTreeUI(window);
            window.repaint();
        }
    }
    
    public static Color getBackgroundColor() {
        return isDarkMode() ? new Color(43, 43, 43) : new Color(236, 240, 241);
    }
    
    public static Color getHeaderColor() {
        return isDarkMode() ? new Color(30, 30, 30) : new Color(41, 128, 185);
    }
    
    public static Color getWhiteCellColor() {
        return isDarkMode() ? new Color(69, 73, 74) : new Color(255, 255, 255);
    }
    
    public static Color getCellTextColor() {
        return isDarkMode() ? new Color(200, 200, 200) : new Color(44, 62, 80);
    }

    public static Color getTextColor() {
        return isDarkMode() ? new Color(220, 220, 220) : new Color(52, 73, 94);
    }
    
    public static Color getGridBackground() {
        return isDarkMode() ? new Color(60, 60, 60) : new Color(240, 240, 240);
    }
    
    public static Color getErrorCellColor() {
        return isDarkMode() ? new Color(130, 0, 0) : new Color(249, 234, 172);
    }
    
    public static Color getSelectedCellColor() {
        return isDarkMode() ? new Color(50, 100, 150) : new Color(255, 255, 200);
    }
}
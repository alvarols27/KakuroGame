import interfaces.LoginUI;
import view.LoginView;
import controller.AuthController;
import controller.ThemeManager;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ThemeManager.applyTheme();

        SwingUtilities.invokeLater(() -> {
            LoginUI loginUI = new LoginView();
            new AuthController(loginUI);
        });
    }
}
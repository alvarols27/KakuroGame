import interfaces.LoginUI;
import interfaces.PlayerRepository;
import view.LoginView;
import database.FilePlayerRepository;
import controller.AuthController;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //we start with loginView
        LoginUI loginUI = new LoginView();
        PlayerRepository playerRepo = new FilePlayerRepository();
        new AuthController(loginUI, playerRepo);
    }
}
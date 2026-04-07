import interfaces.LoginUI;
import view.LoginView;
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
        new AuthController(loginUI);
    }
}
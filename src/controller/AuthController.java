package controller;

import interfaces.LoginUI;
import interfaces.Difficulty;
import model.Player;
import model.difficulty.EasyDifficulty;
import model.difficulty.MediumDifficulty;
import model.difficulty.HardDifficulty;
import view.GameView;
import view.RegisterView;
import java.util.Optional;
import database.PlayerDAO;

public class AuthController {
    private LoginUI loginUI;

    public AuthController(LoginUI loginUI) {
        this.loginUI = loginUI;
        initListeners();
    }

    private void initListeners() {
        loginUI.setLoginAction(e -> handleLogin());
        loginUI.setGuestAction(e -> playAsGuest());
        loginUI.setRegisterAction(e -> showRegisterScreen());
    }

    private void showRegisterScreen() {
        loginUI.close();
        RegisterView registerView = new RegisterView();
        registerView.setRegisterAction(e -> handleCreateAccount(registerView));
        registerView.setBackAction(e -> {
            registerView.dispose();
            loginUI.setVisible(true);
        });
    }

    private void handleCreateAccount(RegisterView rv) {
        String user = rv.getUsername();
        String pass = rv.getPassword();
        String confirmPass = rv.getConfirmPassword();
        
        if (user.isEmpty() || pass.isEmpty()) {
            rv.showError("Please enter both username and password.");
            return;
        }
        
        if (!pass.equals(confirmPass)) {
            rv.showError("Passwords do not match!");
            return;
        }
        
        if (PlayerDAO.usernameExists(user)) {
            rv.showError("Username already exists!");
            return;
        }
        
        PlayerDAO.register(user, pass);
        rv.showSuccess("Account created successfully for " + user + "!");
        rv.dispose();
        loginUI.setVisible(true);
    }

    private void handleLogin() {
        String user = loginUI.getUsername();
        String pass = loginUI.getPassword();
        
        if (user.isEmpty() || pass.isEmpty()) {
            loginUI.showError("Please enter both username and password.");
            return;
        }
        
        Optional<Player> player = PlayerDAO.login(user, pass);
        if (player.isPresent()) {
            startGame(player.get());
        } else {
            loginUI.showError("Invalid credentials or user not found.");
        }
    }

    private void playAsGuest() {
        int confirm = javax.swing.JOptionPane.showConfirmDialog(
            (java.awt.Component) loginUI,
            "Guest mode: Your progress won't be saved.\nContinue as guest?",
            "Guest Mode",
            javax.swing.JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            startGame(new Player(0, "Guest", ""));
        }
    }

    private void startGame(Player player) {    
        String savedDifficulty = SettingsController.getSavedDifficulty(player.getUsername());
        
        Difficulty difficulty;
        switch (savedDifficulty) {
            case "Medium":
                difficulty = new MediumDifficulty();
                break;
            case "Hard":
                difficulty = new HardDifficulty();
                break;
            default:
                difficulty = new EasyDifficulty();
        }
        
        model.Grid grid = GridGenerator.generateRandomGrid(difficulty);
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            GameView gameView = new GameView(grid);
            new GameController(gameView, grid, player, difficulty);
            loginUI.close();
        });
    }
}
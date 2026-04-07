package controller;

import interfaces.LoginUI;
import interfaces.PlayerRepository;
import interfaces.Difficulty;
import model.Player;
import model.difficulty.EasyDifficulty;
import view.GameView;
import view.RegisterView;
import java.util.Optional;

public class AuthController {
    private LoginUI loginUI;
    private PlayerRepository playerRepo;

    public AuthController(LoginUI loginUI, PlayerRepository playerRepo) {
        this.loginUI = loginUI;
        this.playerRepo = playerRepo;
        initListeners();
    }

    private void initListeners() {
        loginUI.setLoginAction(e -> handleLogin());
        loginUI.setGuestAction(e -> handleGuest());
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
        
        if (user.isEmpty() || pass.isEmpty()) {
            rv.showError("Please enter both username and password.");
            return;
        }
        
        if (!rv.passwordsMatch()) {
            rv.showError("Passwords do not match!");
            return;
        }
        
        if (playerRepo.usernameExists(user)) {
            rv.showError("Username already exists!");
            return;
        }
        
        playerRepo.register(user, pass);
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
        
        Optional<Player> player = playerRepo.login(user, pass);
        if (player.isPresent()) {
            startGame(player.get());
        } else {
            loginUI.showError("Invalid credentials or user not found.");
        }
    }

    private void handleGuest() {
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
    Difficulty difficulty = new EasyDifficulty();
    model.Grid grid = GridGenerator.generateRandomGrid(difficulty);
    
    javax.swing.SwingUtilities.invokeLater(() -> {
        GameView gameView = new GameView(grid);
        new GameController(gameView, grid, player, difficulty);
        loginUI.close();
    });
}
}
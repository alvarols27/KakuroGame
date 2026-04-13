package model;

public class Player implements java.io.Serializable {
    private int id;
    private String username;
    private String password;

    public Player(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    private boolean guest = false;

    public void register() { /* Handled by AuthController */ }
    public void login() { /* Handled by AuthController */ }
    public void logout() { /* Handled by Controllers */ }
    public void openDashboard() { /* Handled by AuthController */ }
    public void openSettings() { /* Handled by DashboardController */ }

    public boolean isGuest() { return guest || id == 0; }
    public void setGuest(boolean guest) { this.guest = guest; }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
}

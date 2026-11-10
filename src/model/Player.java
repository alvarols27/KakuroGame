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

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

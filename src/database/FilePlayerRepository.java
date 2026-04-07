package database;

import interfaces.PlayerRepository;
import model.Player;
import java.util.Optional;

public class FilePlayerRepository implements PlayerRepository {
    
    @Override
    public Optional<Player> login(String username, String password) {
        return MockDatabase.getPlayers().stream()
                .filter(p -> p.getUsername().equals(username) && p.getPassword().equals(password))
                .findFirst();
    }
    
    @Override
    public void register(String username, String password) {
        int nextId = MockDatabase.getPlayers().size() + 1;
        MockDatabase.addPlayer(new Player(nextId, username, password));
    }
    
    @Override
    public boolean usernameExists(String username) {
        return MockDatabase.getPlayers().stream()
                .anyMatch(p -> p.getUsername().equals(username));
    }
}
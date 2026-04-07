package interfaces;

import model.Player;
import java.util.Optional;

public interface PlayerRepository {
    Optional<Player> login(String username, String password);
    void register(String username, String password);
    boolean usernameExists(String username);
}
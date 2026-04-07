package interfaces;

import java.util.List;
import model.GameSession;

public interface SavedGameRepository {
    void saveGameSession(GameSession session);
    List<SavedGameInfo> getSavedGames(int playerId);
    GameSession loadGameSession(int sessionId);
    void deleteSavedGame(int sessionId);
}


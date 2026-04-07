package database;

import model.Player;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import model.StatsSummary;

import database.MockDatabase.GameResult;

public class GameDAO {
    public static void saveGameResult(Player player, int timePlayed, String difficulty, int numHintsUsed) {
        //We do not save for guest users
        if (player.getId() != 0) {
            GameResult result = new GameResult(
                player.getId(),
                player.getUsername(),
                timePlayed,
                difficulty,
                numHintsUsed
            );
            MockDatabase.addGameResult(result);
        }
    }

    //stream() to apply multiple conditions for filtering
    //collect(Collectors.groupingBy()) => stores key (username) and value (timePlayed)
    public static List<GameResult> getHighScores() {
        return MockDatabase.getGameResults().stream()
            .collect(Collectors.groupingBy(
                GameResult::getUsername,
                Collectors.minBy(Comparator.comparingInt(GameResult::getTimePlayed))
            ))
            .values().stream() //takes the collect and creates a new stream
            // .filter(Optional::isPresent)
            .map(Optional::get) //extracts the GameResult
            .sorted(Comparator.comparingInt(GameResult::getTimePlayed)) //fastest go first
            .limit(10)
            .collect(Collectors.toList()); //convert to List
    }

    public static List<GameResult> getBestScoresByDifficulty(String difficulty) {
        return MockDatabase.getGameResults().stream()
            .filter(result -> result.getDifficulty().equals(difficulty))
            .collect(Collectors.groupingBy(
                GameResult::getUsername,
                Collectors.minBy(Comparator.comparingInt(GameResult::getTimePlayed))
        ))
        .values().stream()
        .map(Optional::get)
        .sorted(Comparator.comparingInt(GameResult::getTimePlayed))
        .collect(Collectors.toList());
    }

    public static List<GameResult> getPlayerGameResults(int playerId) {
        return MockDatabase.getGameResults().stream()
            .filter(result -> result.getPlayerId() == (playerId)) //since we cannot use equals with int
            .sorted(Comparator.comparing(GameResult::getDate).reversed())
            .collect(Collectors.toList());
    }

    public static StatsSummary getPlayerStats(int playerId) {
        List<GameResult> playResults = getPlayerGameResults(playerId);
        return new StatsSummary(playResults);
    }
}

package model;

import java.util.*;
import java.util.stream.Collectors;

public class StatsSummary {
    private List<GameResult> gameResults;

    public StatsSummary() {
        this.gameResults = new ArrayList<>();
    }

    public StatsSummary(List<GameResult> gameResult){
        this.gameResults = gameResult;
    }

    //STILL GOTTA CHECK
    public int getTotalGamesPlayed() {
        return gameResults.size();
    }
    
    public double getAverageTime() {
        if (gameResults.isEmpty()) return 0;
        return gameResults.stream()
            .mapToInt(GameResult::getTimePlayed)
            .average()
            .orElse(0);
    }
    
    public int getTotalHintsUsed() {
        return gameResults.stream()
            .mapToInt(GameResult::getHintsUsed)
            .sum();
    }
    
    public String getFavoriteDifficulty() {
        if (gameResults.isEmpty()) return "None";
        
        return gameResults.stream()
            .collect(Collectors.groupingBy(GameResult::getDifficulty, Collectors.counting()))
            .entrySet().stream() 
                .max(Map.Entry.comparingByValue())//find the highest value
                .map(Map.Entry::getKey) //get difficulty
                .orElse("None");
    }
    
    public Date getLastPlayedDate() {
        if (gameResults.isEmpty()) return null;
        
        return gameResults.stream()
                .map(GameResult::getDate)
                .max(Date::compareTo)
                .orElse(null);
    }
    
    public List<GameResult> getGameResults() {
        return gameResults;
    }
    
    public List<GameResult> requestPersonalStats() {
        return gameResults;
    }

    public void calculate() {
        // Trigger recalculation logic if cached
    }
}

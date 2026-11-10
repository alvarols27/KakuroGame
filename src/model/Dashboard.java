package model;

import interfaces.Difficulty;
import database.GameDAO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import controller.GridGenerator;

public class Dashboard {
    private Player player;
    private StatsSummary statsSummary;
    private List<BestScore> bestScores;
    private java.util.Date lastRefreshed;
    
    public Dashboard(Player player) {
        this.player = player;
        this.lastRefreshed = new java.util.Date();
        refresh();
    }
    
    public void refresh() {
        this.lastRefreshed = new java.util.Date();
        //to load all GameResults
        List<GameResult> allCompletedGames = GameDAO.getPlayerGameResults(player.getId());
        this.statsSummary = new StatsSummary(allCompletedGames);

        //load best scores
        this.bestScores = calculateBestScores(allCompletedGames);
    }

    private List<BestScore> calculateBestScores(List<GameResult> games) {
        List<BestScore> bestScores = new ArrayList<>();

        // best score for easy
        GameResult bestEasy = games.stream()
            .filter(g -> g.getDifficulty().equals("Easy"))
            .min(Comparator.comparingInt(GameResult::getTimePlayed))
            .orElse(null);
        if (bestEasy != null) {
            bestScores.add(new BestScore("Easy", bestEasy.getTimePlayed(), bestEasy.getDate(), player.getId()));
        }

        // for medium
        GameResult bestMedium = games.stream()
            .filter(g -> g.getDifficulty().equals("Medium"))
            .min(Comparator.comparingInt(GameResult::getTimePlayed))
            .orElse(null);
        if (bestMedium != null) {
            bestScores.add(new BestScore("Medium", bestMedium.getTimePlayed(), bestMedium.getDate(), player.getId()));
        }

        // for hard
        GameResult besthard = games.stream()
            .filter(g -> g.getDifficulty().equals("Hard"))
            .min(Comparator.comparingInt(GameResult::getTimePlayed))
            .orElse(null);
        if (besthard != null) {
            bestScores.add(new BestScore("Hard", besthard.getTimePlayed(), besthard.getDate(), player.getId()));
        }

        return bestScores;
    }
    
    public java.util.Date getLastRefreshed() {
        return lastRefreshed;
    }
    
    public List<BestScore> getBestScores() {
        return bestScores;
    }
    
    public StatsSummary getStatsSummary() {
        return statsSummary;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    // Request operations    
    public BestScore requestBestScore(String difficulty) {
        refresh();
        for (BestScore bs : bestScores) {
            if (bs.getDifficulty().equals(difficulty)) {
                return bs;
            }
        }
        return null;
    }
    
    public List<GameResult> requestPersonalStats() {
        refresh();
        return statsSummary.getGameResults();
    }
    
    public GameSession requestNewPuzzle(String difficulty) {
        // Create difficulty object
        Difficulty diff;
        switch (difficulty) {
            case "Medium":
                diff = new model.difficulty.MediumDifficulty();
                break;
            case "Hard":
                diff = new model.difficulty.HardDifficulty();
                break;
            default:
                diff = new model.difficulty.EasyDifficulty();
        }
        
        GameSession newSession = new GameSession(player, diff);
        
        GridGenerator.generateRandomGrid(diff);
        
        return newSession;
    }
}
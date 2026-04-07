package interfaces;

public interface Difficulty {
    int getGridSize();
    int getMaxHints();
    boolean requiresMathChallenge();
    int getMathPenaltySeconds();
    String getName();
}

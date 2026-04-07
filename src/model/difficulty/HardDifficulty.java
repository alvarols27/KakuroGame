package model.difficulty;

import interfaces.Difficulty;

public class HardDifficulty implements Difficulty {
    @Override
    public int getGridSize() { return 11; }

    @Override
    public int getMaxHints() { return 3; }

    @Override
    public boolean requiresMathChallenge() { return true; }

    @Override
    public int getMathPenaltySeconds() { return 10; }

    @Override
    public String getName() { return "Hard"; }
}

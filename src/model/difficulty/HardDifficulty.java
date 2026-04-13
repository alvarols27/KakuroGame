package model.difficulty;

import interfaces.Difficulty;

public class HardDifficulty implements Difficulty {
    @Override
    public int getGridSize() { return 9; }

    @Override
    public int getMaxHints() { return 10; }

    @Override
    public boolean requiresMathChallenge() { return true; }

    @Override
    public int getMathPenaltySeconds() { return 10; }

    @Override
    public String getName() { return "Hard"; }
}

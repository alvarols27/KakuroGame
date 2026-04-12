package model.difficulty;

import interfaces.Difficulty;

public class MediumDifficulty implements Difficulty {
    @Override
    public int getGridSize() { return 8; }

    @Override
    public int getMaxHints() { return 10; }

    @Override
    public boolean requiresMathChallenge() { return false; }

    @Override
    public int getMathPenaltySeconds() { return 0; }

    @Override
    public String getName() { return "Medium"; }
}

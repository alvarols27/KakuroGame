package model.difficulty;

import interfaces.Difficulty;

//(@Override) Does the parent/interface have a ...() method?

public class EasyDifficulty implements Difficulty {
    @Override
    public int getGridSize() { return 5; }

    @Override
    public int getMaxHints() { return 999; }

    @Override
    public boolean requiresMathChallenge() { return false; }

    @Override
    public int getMathPenaltySeconds() { return 0; }

    @Override
    public String getName() { return "Easy"; }
}
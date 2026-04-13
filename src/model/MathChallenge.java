package model;

import java.util.Random;

public class MathChallenge {
    private String equation;
    private int answer;
    private boolean solved;
    private int countdown;

    public void generateChallenge() {
        Random rand = new Random();
        int a = rand.nextInt(10) + 1;
        int b = rand.nextInt(10) + 1;
        equation = a + " + " + b;
        answer = a + b;
        solved = false;
        countdown = 30;
    }

    public String getEquation() {
        return equation;
    }

    public boolean checkAnswer(int result) {
        if (result == answer) {
            solved = true;
            return true;
        }
        return false;
    }

    public void startCountdown() {
        // Countdown logic overseen by controller
    }

    public boolean isSolved() {
        return solved;
    }
}

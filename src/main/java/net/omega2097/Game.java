package net.omega2097;


public class Game {
    private int lives;
    private int score;
    private int level;

    public Game() {
        this.lives = 3;
        this.score = 0;
        this.level = 0;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}

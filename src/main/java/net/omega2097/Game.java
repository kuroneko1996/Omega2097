package net.omega2097;


public class Game {
    private int lives;
    private int score;
    private int level;
    private GameState state = GameState.INIT;

    public enum GameState {
        INIT,
        RESTART_LEVEL,
        PLAY,
        OVER,
        MENU,
        EXIT,
    }

    public Game() {
        this.lives = 3;
        this.score = 0;
        this.level = 0;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
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

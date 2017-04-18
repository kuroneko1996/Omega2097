package net.omega2097;

public class Stat {
    private int current;
    private int max;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public boolean isMax() {
        return current == max;
    }

    public Stat(int value) {
        current = value;
        max = value;
    }
}

package net.omega2097;

public class Stat<T> {
    private T current;
    private T max;

    public T getCurrent() {
        return current;
    }

    public void setCurrent(T current) {
        this.current = current;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public boolean isMax() {
        return current == max;
    }

    public Stat(T value) {
        current = value;
        max = value;
    }
}

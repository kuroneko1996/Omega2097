package net.omega2097;

public class Stat<T> {
    private T current;
    private T max;

    public T getCurrent() {
        return current;
    }

    public Stat<T> setCurrent(T current) {
        this.current = current;
        return this;
    }

    public T getMax() {
        return max;
    }

    public Stat<T> setMax(T max) {
        this.max = max;
        return this;
    }

    public boolean isMax() {
        return current.equals(max);
    }

    public Stat(T value) {
        current = value;
        max = value;
    }
}

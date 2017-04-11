package net.omega2097.util;

public class Random implements IRandom {
    private java.util.Random _rand;
    public Random(long seed) {
        _rand = new java.util.Random(seed);
    }

    @Override
    public int next(int maxValue) {
        return _rand.nextInt(maxValue + 1);
    }

    @Override
    public int next(int minValue, int maxValue) {
        return _rand.nextInt(maxValue + 1 - minValue) + minValue;
    }
}

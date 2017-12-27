package com.kawiory.civsim2.simulator;

import com.google.common.base.Objects;

import java.util.stream.Stream;

/**
 * @author Kacper
 */

public class Coordinates {

    private final int x;
    private final int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinates)) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y);
    }

    public Stream<Coordinates> getNeighbours(){
        return Stream.of(
                new Coordinates(x + 1, y),
                new Coordinates(x, y + 1),
                new Coordinates(x - 1, y),
                new Coordinates(x, y - 1)
        );
    }
}

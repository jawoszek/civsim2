package com.kawiory.civsim2.simulator;

import com.google.common.base.MoreObjects;
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

    public Stream<Coordinates> getNeighbours(int maxX, int maxY){
        return Stream.of(
                new Coordinates(x + 1, y),
                new Coordinates(x, y + 1),
                new Coordinates(x - 1, y),
                new Coordinates(x, y - 1)
        ).filter(
                coordinates -> isInsideMap(coordinates, maxX, maxY)
        );
    }

    public Stream<Coordinates> getNeighbours(int maxX, int maxY, int r){
        if (r < 1 || r > 2) {
            throw new IllegalArgumentException();
        }

        if (r == 1) {
            return getNeighbours(maxX, maxY);
        }

        return Stream.of(
                new Coordinates(x + 1, y),
                new Coordinates(x, y + 1),
                new Coordinates(x - 1, y),
                new Coordinates(x, y - 1),

                new Coordinates(x + 2, y),
                new Coordinates(x, y + 2),
                new Coordinates(x - 2, y),
                new Coordinates(x, y - 2),

                new Coordinates(x + 1, y + 1),
                new Coordinates(x - 1, y + 1),
                new Coordinates(x - 1, y - 1),
                new Coordinates(x + 1, y - 1)
        ).filter(
                coordinates -> isInsideMap(coordinates, maxX, maxY)
        );
    }

    private boolean isInsideMap(Coordinates coordinates, int maxX, int maxY) {
        return coordinates.getX() < maxX && coordinates.getX() >= 0 && coordinates.getY() < maxY && coordinates.getY() >= 0;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("x", x)
                .add("y", y)
                .toString();
    }
}

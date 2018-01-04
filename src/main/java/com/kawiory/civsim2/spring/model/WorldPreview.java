package com.kawiory.civsim2.spring.model;

/**
 * @author Kacper
 */

public class WorldPreview {

    private final String name;
    private final int id;
    private final int usingSimsCount;
    private final int X;
    private final int Y;

    public WorldPreview(int id, String name, int usingSimsCount, int X, int Y) {
        this.name = name;
        this.id = id;
        this.usingSimsCount = usingSimsCount;
        this.X = X;
        this.Y = Y;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public int getUsingSimsCount() {
        return usingSimsCount;
    }

}

package com.kawiory.civsim2.spring.model;

/**
 * @author Kacper
 */

public class WorldPreview {

    private final String name;
    private final int id;
    private final int simulationsCount;
    private final int sizeX;
    private final int sizeY;

    public WorldPreview(int id, String name, int simulationsCount, int sizeX, int sizeY) {
        this.name = name;
        this.id = id;
        this.simulationsCount = simulationsCount;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getSimulationsCount() {
        return simulationsCount;
    }

}

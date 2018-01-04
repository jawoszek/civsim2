package com.kawiory.civsim2.spring.model;

import java.util.List;

/**
 * @author Kacper
 */

public class WorldDetails extends WorldPreview {

    private final int[][] worldMap;
    private final List<SimulationPreview> usingSimulations;


    public WorldDetails(int id, String name, int usingSimsCount, int X, int Y,
                        int[][] worldMap, List<SimulationPreview> usingSimulations) {
        super(id, name, usingSimsCount, X, Y);
        this.worldMap = worldMap;
        this.usingSimulations = usingSimulations;
    }

    public int[][] getWorldMap() {
        return worldMap;
    }

    public List<SimulationPreview> getUsingSimulations() {
        return usingSimulations;
    }
}

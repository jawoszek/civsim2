package com.kawiory.civsim2.spring.model;

import java.util.List;

/**
 * @author Kacper
 */

public class WorldDetails extends WorldPreview {

    private final int[][] worldMap;
    private final List<SimulationPreview> simulations;


    public WorldDetails(int id, String name, int usingSimsCount, int X, int Y,
                        int[][] worldMap, List<SimulationPreview> simulations) {
        super(id, name, usingSimsCount, X, Y);
        this.worldMap = worldMap;
        this.simulations = simulations;
    }

    public int[][] getWorldMap() {
        return worldMap;
    }

    public List<SimulationPreview> getSimulations() {
        return simulations;
    }
}

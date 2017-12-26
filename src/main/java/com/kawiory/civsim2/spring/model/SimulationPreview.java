package com.kawiory.civsim2.spring.model;

import com.kawiory.civsim2.simulator.Simulation;

/**
 * @author Kacper
 */

public class SimulationPreview {

    private final int id;
    private final String name;
    private final int mapID;
    private final String mapName;
    private final boolean isCompleted;
    private int currentFrame;
    private final int maxFrame;

    public SimulationPreview(int id, String name, int mapID, String mapName, boolean isCompleted, int currentFrame, int frameCount) {
        this.id = id;
        this.name = name;
        this.mapID = mapID;
        this.mapName = mapName;
        this.isCompleted = isCompleted;
        this.currentFrame = currentFrame;
        this.maxFrame = frameCount;
    }

    public SimulationPreview(Simulation simulation) {
        this(
                simulation.getId(),
                simulation.getName(),
                simulation.getMapID(),
                simulation.getMapName(),
                simulation.isCompleted(),
                simulation.getCurrentFrame(),
                simulation.getMaxFrame()
        );
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMapID() {
        return mapID;
    }

    public String getMapName() {
        return mapName;
    }

    public boolean isCompleted() {
        return isCompleted || currentFrame == maxFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public int getFrameCount() {
        return maxFrame;
    }
}

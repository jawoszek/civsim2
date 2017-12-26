package com.kawiory.civsim2.spring.model;

import com.google.common.base.MoreObjects;

/**
 * @author Kacper
 */

public class SimulationPrototype {

    private int mapID;
    private int maxFrame;
    private int civCount;
    private String name;
    private boolean historical;

    public SimulationPrototype() {
        this.name = null;
        this.mapID = 1;
        this.maxFrame = 200;
        this.civCount = 5;
        historical = false;
    }

    public SimulationPrototype(int mapID, int maxFrame, int civCount, String name, boolean historical) {
        this.mapID = mapID;
        this.maxFrame = maxFrame;
        this.civCount = civCount;
        this.name = name;
        this.historical = historical;
    }

    public int getMapID() {
        return mapID;
    }

    public void setMapID(int mapID) {
        this.mapID = mapID;
    }

    public int getMaxFrame() {
        return maxFrame;
    }

    public void setMaxFrame(int maxFrame) {
        this.maxFrame = maxFrame;
    }

    public int getCivCount() {
        return civCount;
    }

    public void setCivCount(int civCount) {
        this.civCount = civCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHistorical() {
        return historical;
    }

    public void setHistorical(boolean historical) {
        this.historical = historical;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("mapID", mapID)
                .add("maxFrame", maxFrame)
                .add("civCount", civCount)
                .add("name", name)
                .add("historical", historical)
                .toString();
    }
}

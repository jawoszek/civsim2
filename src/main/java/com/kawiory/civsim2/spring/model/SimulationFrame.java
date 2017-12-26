package com.kawiory.civsim2.spring.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kacper
 */

public class SimulationFrame {

    private int simID;
    private int frameID;
    private int frameNumber;
    private int maxFrame;
    private List<CivilizationPreview> civilizationsPreviews;
    private List<int[]> provinces;
    private String time;

    public int getSimID() {
        return simID;
    }

    public int getFrameID() {
        return frameID;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public int getMaxFrame() {
        return maxFrame;
    }

    public List<CivilizationPreview> getCivilizationsPreviews() {
        return civilizationsPreviews;
    }

    public List<int[]> getProvinces() {
        return provinces;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public SimulationFrame(int simID, int frameID, int frameNumber, int maxFrame) {
        this.simID = simID;
        this.frameID = frameID;
        this.frameNumber = frameNumber;
        this.maxFrame = maxFrame;
        this.civilizationsPreviews = new ArrayList<>();
        this.provinces = new ArrayList<>();
        this.time = ""; //TODO parse time
    }

    public void addCivilization(CivilizationPreview civ) {
        civilizationsPreviews.add(civ);
    }

    public void addProvince(int[] prov) {
        provinces.add(prov);
    }
}

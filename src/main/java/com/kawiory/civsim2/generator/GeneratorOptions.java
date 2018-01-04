package com.kawiory.civsim2.generator;

public class GeneratorOptions {

    private int startSizeX = 12;
    private int startSizeY = 6;
    private double growthFactor = 2.2;
    private int iterations = 4;
    private double water = 0.6;
    private double[] terrainFactor = {120, 128, 126.5, 40, 30, 20, 20, 20, 20};
    private String name = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartSizeX() {
        return startSizeX;
    }

    public double getGrowthFactor() {
        return growthFactor;
    }

    public int getIterations() {
        return iterations;
    }

    public int getStartSizeY() {
        return startSizeY;
    }

    public double getWater() {
        return water;
    }

    public double[] getTerrainFactor() {
        return terrainFactor;
    }

    public void setGrowthFactor(double growthFactor) {
        this.growthFactor = growthFactor;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public void setStartSizeX(int startSizeX) {
        this.startSizeX = startSizeX;
    }

    public void setStartSizeY(int startSizeY) {
        this.startSizeY = startSizeY;
    }

    public void setWater(double water) {
        this.water = water;
    }

    public void setTerrainFactors(double[] terrainFactor) {
        this.terrainFactor = terrainFactor;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("GeneratorOptions{");
        sb.append("startSizeX=").append(startSizeX);
        sb.append(", startSizeY=").append(startSizeY);
        sb.append(", growthFactor=").append(growthFactor);
        sb.append(", iterations=").append(iterations);
        sb.append(", water=").append(water);
        sb.append(", terrainFactor=");
        if (terrainFactor == null) sb.append("null");
        else {
            sb.append('[');
            for (int i = 0; i < terrainFactor.length; ++i)
                sb.append(i == 0 ? "" : ", ").append(terrainFactor[i]);
            sb.append(']');
        }
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

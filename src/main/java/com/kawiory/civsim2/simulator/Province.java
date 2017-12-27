package com.kawiory.civsim2.simulator;

/**
 * @author Kacper
 */
public class Province {

    private final int terrain;

    private Civilization civilization;
    private int population;

    public Province(int terrain, Civilization civilization, int population) {
        this.terrain = terrain;
        this.civilization = civilization;
        this.population = population;
    }

    public Civilization getCivilization() {
        return civilization;
    }

    public void setCivilization(Civilization civilization) {
        this.civilization = civilization;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public int getTerrain() {
        return terrain;
    }
}

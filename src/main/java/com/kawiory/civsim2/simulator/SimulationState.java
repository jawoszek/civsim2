package com.kawiory.civsim2.simulator;

import com.kawiory.civsim2.persistance.DataProvider;
import com.kawiory.civsim2.simulator.naming.NameGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Kacper
 */

public class SimulationState {

    private final int simulationID;

    private final DataProvider dataProvider;
    private final NameGenerator nameGenerator;
    private final Terrains terrains;

    private int currentFrame;
    private int[][] map;

    private final Set<Civilization> civilizations;
    private final Map<Coordinates, Province> provinces;
    private final Map<Coordinates, Basin> basins;

    public SimulationState(int simulationID, DataProvider dataProvider, NameGenerator nameGenerator, Terrains terrains) {
        this.simulationID = simulationID;
        this.nameGenerator = nameGenerator;
        this.dataProvider = dataProvider;
        this.terrains = terrains;
        this.currentFrame = 0;
        this.civilizations = new HashSet<>();
        this.provinces = new HashMap<>();
        this.basins = new HashMap<>();
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public int[][] getMap() {
        return map;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }

    public Set<Civilization> getCivilizations() {
        return civilizations;
    }

    public Map<Coordinates, Province> getProvinces() {
        return provinces;
    }

    public Map<Coordinates, Basin> getBasins() {
        return basins;
    }

    public Terrains getTerrains() {
        return terrains;
    }

    public int getSizeX() {
        return map.length;
    }

    public int getSizeY() {
        if (getSizeX() < 1) {
            throw new IllegalStateException("Wrong map size");
        }
        return map[0].length;
    }

    public int getTerrain(int x, int y) {
        return map[x][y];
    }

    public boolean isProvinceTaken(int x, int y) {
        return provinces.containsKey(new Coordinates(x, y));
    }

    public Province getProvince(int x, int y) {
        return provinces.get(new Coordinates(x, y));
    }

    public Civilization createCivilization() {
        return createCivilization(nameGenerator.generateName());
    }

    public Civilization createCivilization(String name) {
        int newCivilizationID = dataProvider.insertCiv(simulationID, name, dataProvider.getRandomColor());
        Civilization newCivilization = new Civilization(newCivilizationID, name, 0, 0);
        civilizations.add(newCivilization);
        return newCivilization;
    }

    public int getTerrainFactor(Province province) {
        return terrains.getTerrainFactor(province.getTerrain(), province.getCivilization().getTechnologyLevel());
    }

    public int getMaxPopulation(Province province) {
        return terrains.getMaxPopulation(province.getTerrain(), province.getCivilization().getTechnologyLevel());
    }
}

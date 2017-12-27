package com.kawiory.civsim2.simulator;

import com.kawiory.civsim2.persistance.DataProvider;
import com.kawiory.civsim2.simulator.naming.NameGenerator;

import java.util.Random;

/**
 * @author Kacper
 */

public class Simulation implements Runnable {

    private final DataProvider dataProvider;
    private final Terrains terrains;
    private final int maxFrame;
    private final int mapID;

    private int id;
    private boolean stopped;
    private String name;
    private boolean nameChosen;
    private int initialCivilizationsCount;

    private final SimulationState state;

    public Simulation(int mapID, int maxFrame, String name, int initialCivilizationsCount, DataProvider dataProvider, Terrains terrains, NameGenerator nameGenerator) {
        if (name == null) {
            this.nameChosen = false;
            this.name = "Not yet generated";
        } else {
            this.name = name;
        }
        this.id = 0;
        this.mapID = mapID;
        this.maxFrame = maxFrame;
        this.initialCivilizationsCount = initialCivilizationsCount;
        this.dataProvider = dataProvider;
        this.terrains = terrains;
        this.state = new SimulationState(id, dataProvider, nameGenerator, terrains);
    }

    @Override
    public void run() {
        id = dataProvider.insertSimulation(this);
        state.setMap(dataProvider.getWorldMap(mapID));
        initialCivilizationsCount = Math.min(numberOfHabitable(0), initialCivilizationsCount);
        Random r = new Random();
        for (int i = 1; i <= initialCivilizationsCount; i++) {
            int x;
            int y;
            do {
                x = r.nextInt(state.getSizeX());
                y = r.nextInt(state.getSizeY());
            } while (!terrains.isHabitable(state.getTerrain(x, y), 0) || state.isProvinceTaken(x, y));

            Civilization civilization = state.createCivilization();
            Coordinates coordinates = new Coordinates(x, y);
            Province province =
                    new Province(
                            state.getTerrain(x, y),
                            civilization,
                            Math.min(
                                    500,
                                    terrains.getMaxPopulation(state.getTerrain(x, y), 0)
                            )
                    );

            state.getProvinces().put(coordinates, province);
            civilization.getOwnedProvinces().add(coordinates);
        }
        dataProvider.insertFrame(1, id, state.getProvinces());
        state.setCurrentFrame(2);
        while (state.getCurrentFrame() < maxFrame && !stopped) {
            nextRound();
            dataProvider.insertFrame(state.getCurrentFrame(), id, state.getProvinces());
            state.setCurrentFrame(state.getCurrentFrame() + 1);
        }
        if (stopped) dataProvider.deleteSimulation(id);
    }

    private void nextRound() {

    }

    private int numberOfHabitable(int technologyLevel) {
        if (state.getMap() == null) {
            throw new IllegalStateException("No map");
        }
        int i = 0;
        for (int[] x : state.getMap()) {
            for (int y : x) {
                if (terrains.isHabitable(y, technologyLevel)) {
                    i++;
                }
            }
        }
        return i;
    }

    public int getId() {
        return id;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void stop() {
        this.stopped = true;
    }

    public int getMaxFrame() {
        return maxFrame;
    }

    public int getMapID() {
        return mapID;
    }

    public String getName() {
        return name;
    }

    public boolean isNameChosen() {
        return nameChosen;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMapName() {
        return dataProvider.getMapName(mapID);
    }

    public boolean isCompleted() {
        return getCurrentFrame() >= maxFrame;
    }

    public int getCurrentFrame() {
        return state.getCurrentFrame();
    }
}

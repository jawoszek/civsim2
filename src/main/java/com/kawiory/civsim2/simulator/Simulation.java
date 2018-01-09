package com.kawiory.civsim2.simulator;

import com.google.common.collect.ImmutableList;
import com.kawiory.civsim2.persistance.DataProvider;
import com.kawiory.civsim2.simulator.initializators.HistoricalInitializer;
import com.kawiory.civsim2.simulator.initializators.Initializer;
import com.kawiory.civsim2.simulator.initializators.RandomInitializer;
import com.kawiory.civsim2.simulator.naming.NameGenerator;
import com.kawiory.civsim2.simulator.rules.*;

import java.util.List;

/**
 * @author Kacper
 */

public class Simulation implements Runnable {

    private final List<Rule> simulationRules = ImmutableList.of(
            new TechnologicalProgress(),
            new Relations(),
            new InternalAffairs(),
            new PopulationGrowth(),
            new Expansion(),
            new Colonization(),
            new War(),
            new Separatism()
    );

    private final DataProvider dataProvider;
    private final Terrains terrains;
    private final int maxFrame;
    private final int mapID;
    private final boolean historical;

    private int id;
    private boolean stopped;
    private String name;
    private boolean nameChosen;
    private int initialCivilizationsCount;

    private final SimulationState state;

    public Simulation(int mapID, int maxFrame, String name, int initialCivilizationsCount, DataProvider dataProvider, Terrains terrains, NameGenerator nameGenerator, boolean historical) {
        if (name == null) {
            this.nameChosen = false;
            this.name = "Not yet generated";
        } else {
            this.nameChosen = true;
            this.name = name;
        }
        this.id = 0;
        this.mapID = mapID;
        this.maxFrame = maxFrame;
        this.initialCivilizationsCount = initialCivilizationsCount;
        this.dataProvider = dataProvider;
        this.terrains = terrains;
        this.state = new SimulationState(id, dataProvider, nameGenerator, terrains, simulationRules);
        this.historical = historical;
    }

    @Override
    public void run() {
        this.id = dataProvider.insertSimulation(this);
        state.setSimulationID(id);
        System.out.println(id);
        state.setMap(dataProvider.getWorldMap(mapID));

        initialCivilizationsCount = Math.min(numberOfHabitable(0), initialCivilizationsCount);

        Initializer initializer = getInitializer();
        initializer.initializeCivilizations(initialCivilizationsCount, state);

        dataProvider.insertFrame(1, id, state.getProvinces());
        state.setCurrentFrame(2);
        while (state.getCurrentFrame() <= maxFrame && !stopped) {
            nextRound();
            dataProvider.insertFrame(state.getCurrentFrame(), id, state.getProvinces());
            state.setCurrentFrame(state.getCurrentFrame() + 1);
        }
        if (stopped) dataProvider.deleteSimulation(id);
    }

    private void nextRound() {
        simulationRules.forEach(rule -> rule.changeState(state));
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

    private Initializer getInitializer() {
        if (historical) {
            return new HistoricalInitializer(dataProvider.getMapName(mapID));
        }
        return new RandomInitializer();
    }
}

package com.kawiory.civsim2.simulator.initializators;

import com.kawiory.civsim2.simulator.Civilization;
import com.kawiory.civsim2.simulator.Coordinates;
import com.kawiory.civsim2.simulator.SimulationState;

import java.util.Random;

/**
 * @author Kacper
 */
public class RandomInitializer implements Initializer {

    private final Random random = new Random();

    @Override
    public void initializeCivilizations(int initialCivilizationsCount, SimulationState simulationState) {
        for (int i = 1; i <= initialCivilizationsCount; i++) {
            int government = random.nextInt(3) - 1;

            int x;
            int y;
            do {
                x = random.nextInt(simulationState.getSizeX());
                y = random.nextInt(simulationState.getSizeY());
            } while (!simulationState.getTerrains().isHabitable(simulationState.getTerrain(x, y), 0) || simulationState.isProvinceTaken(x, y));

            Civilization civilization = simulationState.createCivilization(government, 0);
            Coordinates coordinates = new Coordinates(x, y);

            simulationState.assignProvince(coordinates, civilization, 5000);
        }
    }
}

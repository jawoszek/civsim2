package com.kawiory.civsim2.simulator.initializators;

import com.kawiory.civsim2.simulator.Civilization;
import com.kawiory.civsim2.simulator.Coordinates;
import com.kawiory.civsim2.simulator.HistoricalMaps;
import com.kawiory.civsim2.simulator.SimulationState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Kacper
 */
public class HistoricalInitializer implements Initializer {

    private final String mapName;

    private final Random random = new Random();

    public HistoricalInitializer(String mapName) {
        this.mapName = mapName;
    }

    @Override
    public void initializeCivilizations(int initialCivilizationsCount, SimulationState simulationState) {
        HistoricalMaps historicalMap = getHistoricalMaps(mapName);
        List<String> historicalNames = new ArrayList<>(historicalMap.getStarts().keySet());

        for (int i = 1; i <= initialCivilizationsCount; i++) {
            String currentName = null;
            Coordinates currentCoordinates = null;
            int government = random.nextInt(3) - 1;

            if (!historicalNames.isEmpty()) {
                int lastIndex = historicalNames.size() - 1;
                currentName = historicalNames.get(lastIndex);
                historicalNames.remove(lastIndex);
                currentCoordinates = historicalMap.getStarts().get(currentName)._1;
                government = historicalMap.getStarts().get(currentName)._2;
            }

            while (currentCoordinates == null || !simulationState.getTerrains().isHabitable(simulationState.getTerrain(currentCoordinates), 0) || simulationState.isProvinceTaken(currentCoordinates)) {
                currentCoordinates = new Coordinates(random.nextInt(simulationState.getSizeX()), random.nextInt(simulationState.getSizeY()));
            }

            Civilization civilization;
            if (currentName == null) {
                civilization = simulationState.createCivilization(government, 0);
            } else {
                civilization = simulationState.createCivilization(currentName, government, 0);
            }

            simulationState.assignProvince(currentCoordinates, civilization, 5000);
        }
    }

    private HistoricalMaps getHistoricalMaps(String mapName) {
        for (HistoricalMaps maps : HistoricalMaps.values()) {
            if (maps.getMapName().equals(mapName)) {
                return maps;
            }
        }
        throw new IllegalArgumentException();
    }
}

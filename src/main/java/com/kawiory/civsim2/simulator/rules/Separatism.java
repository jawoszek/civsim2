package com.kawiory.civsim2.simulator.rules;

import com.kawiory.civsim2.simulator.Civilization;
import com.kawiory.civsim2.simulator.Coordinates;
import com.kawiory.civsim2.simulator.SimulationState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Kacper
 */
public class Separatism implements Rule {

    private final Random random = new Random();

    @Override
    public void changeState(SimulationState simulationState) {
        new HashSet<>(simulationState.getCivilizations())
                .stream()
                .filter(civilization -> !simulationState.isCivilizationAtWar(civilization))
                .forEach(civilization -> processCivilization(simulationState, civilization));
    }

    @Override
    public void deleteCivilization(Civilization civilization) {

    }

    private void processCivilization(SimulationState simulationState, Civilization civilization) {
        int size = civilization.getProvincesCount();
        int maxSize = civilization.getMaxProvincesCount();
        int technologyLevel = civilization.getTechnologyLevel();
        int sizeRatio = 100 * size / maxSize;

        if (getSeparatismSizeThreshold(technologyLevel) >= sizeRatio) {
            return;
        }

        int chance = getBasicChance(civilization) * 100 * size / maxSize;

        if (size > maxSize) {
            chance = chance * size / maxSize;
        }

        int result = random.nextInt(10000);

        if (result < chance) {
            separateSingleCivilization(simulationState, civilization);
        }
    }

    private int getSeparatismSizeThreshold(int technologyLevel) {
        return 20;
    }

    private int getBasicChance(Civilization civilization) {
        return 5 - civilization.getEfficiencies().getAdministrativeEfficiency();
    }

    private void separateSingleCivilization(SimulationState simulationState, Civilization civilization) {
        if (civilization.getProvincesCount() == 0) {
            return;
        }
        int numberOfProvincesToSeparate = civilization.getProvincesCount() * (random.nextInt(40) + 10) / 100;
        Coordinates currentCoordinatesToSeparate = getRandomProvince(civilization);

        Civilization newCivilization = simulationState.createCivilization(civilization.getGovernmentID(), civilization.getTechnologyLevel());
        simulationState.assignProvince(currentCoordinatesToSeparate, newCivilization);

        List<Coordinates> possibleCoordinates = new ArrayList<>();
        for (int i = 1; i < numberOfProvincesToSeparate; i++) {
            List<Coordinates> newCoordinates =
                    currentCoordinatesToSeparate
                            .getNeighbours(simulationState.getSizeX(), simulationState.getSizeY())
                            .filter(simulationState::isProvinceTaken)
                            .filter(coordinates -> simulationState.getProvince(coordinates).getCivilization() == civilization)
                            .collect(Collectors.toList());
            possibleCoordinates.addAll(newCoordinates);

            if (possibleCoordinates.isEmpty()) {
                break;
            }
            currentCoordinatesToSeparate = possibleCoordinates.get(random.nextInt(possibleCoordinates.size()));
            simulationState.assignProvince(currentCoordinatesToSeparate, newCivilization);
        }
    }

    private Coordinates getRandomProvince(Civilization civilization) {
        return new ArrayList<>(civilization.getOwnedProvinces()).get(random.nextInt(civilization.getProvincesCount()));
    }
}

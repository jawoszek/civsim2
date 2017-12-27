package com.kawiory.civsim2.simulator.rules;

import com.kawiory.civsim2.simulator.Civilization;
import com.kawiory.civsim2.simulator.Coordinates;
import com.kawiory.civsim2.simulator.Province;
import com.kawiory.civsim2.simulator.SimulationState;

import java.util.*;

/**
 * @author Kacper
 */
public class Expansion implements Rule {

    private final Random r = new Random();

    @Override
    public void changeState(SimulationState simulationState) {
        getSuccessfulExpansions(simulationState)
                .forEach(
                        (coordinates, provinces) ->
                                settleExpansion(simulationState, coordinates, provinces)
                );
    }

    private void settleExpansion(SimulationState simulationState, Coordinates targetCoordinates,
                                 Set<Province> sourceProvinces) {
        Map<Civilization, Integer> possibleCivilizations = new HashMap<>();
        sourceProvinces.forEach(
                province -> possibleCivilizations.merge(
                        province.getCivilization(),
                        100 * province.getPopulation() / simulationState.getMaxPopulation(province),
                        Integer::sum
                )
        );

        int sum = possibleCivilizations.values().stream().mapToInt(Integer::intValue).sum();

        int result = r.nextInt(sum);

        int current = 0;
        for (Map.Entry<Civilization, Integer> entry : possibleCivilizations.entrySet()) {
            current += entry.getValue();
            if (result < current) {
                claimProvince(simulationState, targetCoordinates, entry.getKey());
                return;
            }
        }
    }

    private void claimProvince(SimulationState simulationState, Coordinates targetCoordinates, Civilization civilization) {
        int terrain = simulationState.getTerrain(targetCoordinates.getX(), targetCoordinates.getY());
        int population = Math.min(simulationState.getTerrains().getMaxPopulation(terrain, civilization.getTechnologyLevel()), 1000);

        Province newProvince = new Province(terrain, civilization, population);

        simulationState.getProvinces().put(targetCoordinates, newProvince);
        civilization.getOwnedProvinces().add(targetCoordinates);
    }


    private Map<Coordinates, Set<Province>> getSuccessfulExpansions(SimulationState simulationState) {
        Map<Coordinates, Set<Province>> possibilities = new HashMap<>();

        iterateOverExistingProvinces(simulationState, possibilities);

        return possibilities;
    }

    private void iterateOverExistingProvinces(SimulationState simulationState,
                                              Map<Coordinates, Set<Province>> possibilities) {
        simulationState
                .getProvinces()
                .forEach(
                        (coordinates, province) ->
                                iterateOverPossibleProvinces(simulationState, coordinates, province, possibilities)
                );
    }

    private void iterateOverPossibleProvinces(SimulationState simulationState, Coordinates sourceCoordinates,
                                              Province sourceProvince, Map<Coordinates, Set<Province>> possibilities) {
        sourceCoordinates
                .getNeighbours()
                .forEach(
                        targetCoordinates -> tryToExpand(simulationState, sourceCoordinates, sourceProvince, targetCoordinates, possibilities)
                );
    }

    private void tryToExpand(SimulationState simulationState, Coordinates sourceCoordinates, Province sourceProvince,
                             Coordinates targetCoordinates, Map<Coordinates, Set<Province>> possibilities) {
        if (simulationState.getProvinces().containsKey(targetCoordinates)) {
            return;
        }

        int targetTerrain = simulationState.getTerrain(targetCoordinates.getX(), targetCoordinates.getY());

        if (!simulationState.getTerrains().isHabitable(targetTerrain, sourceProvince.getCivilization().getTechnologyLevel())) {
            return;
        }

        if (!isSuccessful(targetTerrain, sourceProvince, simulationState)) {
            return;
        }

        Set<Province> sourceProvinces = possibilities.getOrDefault(targetCoordinates, new HashSet<>());
        sourceProvinces.add(sourceProvince);
    }

    private boolean isSuccessful(int targetTerrain, Province sourceProvince, SimulationState simulationState) {
        return r.nextInt(100) < getChance(targetTerrain, sourceProvince, simulationState);
    }

    private int getChance(int targetTerrain, Province sourceProvince, SimulationState simulationState) {
        Civilization civilization = sourceProvince.getCivilization();

        int sourcePopulationPart = 100 * sourceProvince.getPopulation() / simulationState.getMaxPopulation(sourceProvince);

        int civilizationProvincesPart = 100 - (100 * civilization.getOwnedProvinces().size() / civilization.getMaxProvincesCount());

        int terrainFactor = simulationState.getTerrains().getTerrainFactor(targetTerrain, civilization.getTechnologyLevel());

        return (sourcePopulationPart + civilizationProvincesPart) * terrainFactor / 100;
    }
}

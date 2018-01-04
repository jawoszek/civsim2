package com.kawiory.civsim2.simulator.rules;

import com.kawiory.civsim2.simulator.*;
import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Kacper
 */
public class Colonization implements Rule {

    private final Random random = new Random();

    @Override
    public void changeState(SimulationState simulationState) {
        increasePortsStrength(simulationState);
        propagateColonialStrength(simulationState);
        colonize(simulationState);
    }

    @Override
    public void deleteCivilization(Civilization civilization) {

    }

    private void increasePortsStrength(SimulationState simulationState) {
        Map<Coordinates, Set<Civilization>> playersPort = new HashMap<>();

        simulationState.getProvinces().forEach(
                (coordinates, province) ->
                        coordinates
                                .getNeighbours(simulationState.getSizeX(), simulationState.getSizeY())
                                .filter(potentialCoordinates -> simulationState.isWater(coordinates))
                                .forEach(portCoordinates -> {
                                            Set<Civilization> civilizations = playersPort.getOrDefault(portCoordinates, new HashSet<>());
                                            civilizations.add(province.getCivilization());
                                            playersPort.put(portCoordinates, civilizations);
                                        }
                                )
        );

        Map<Coordinates, Basin> basins = simulationState.getBasins();

        playersPort.forEach(
                (coordinates, civilizations) -> {
                    Basin basin = basins.getOrDefault(coordinates, new Basin());
                    Map<Civilization, Integer> colonialStrength = basin.getColonialStrength();
                    civilizations.forEach(
                            civilization ->
                                    colonialStrength.merge(
                                            civilization,
                                            Math.min(getColonialStrengthIncrease(civilization), getMaxColonialStrength(civilization)),
                                            (previousValue, newValue) -> Math.min(previousValue + newValue, getMaxColonialStrength(civilization))
                                    )
                    );
                    basins.put(coordinates, basin);
                }
        );
    }

    private int getColonialStrengthIncrease(Civilization civilization) {
        return 1;
    }

    private int getMaxColonialStrength(Civilization civilization) {
        return 10;
    }

    private void propagateColonialStrength(SimulationState simulationState) {
        Map<Coordinates, Basin> basins = simulationState.getBasins();
        Map<Coordinates, Basin> newBasins = new HashMap<>();
        Map<Civilization, List<Tuple2>> civilizationsStrength = new HashMap<>();

        basins.forEach(
                (coordinates, basin) -> {
                    basin.getColonialStrength().forEach(
                            (civilization, strength) -> {
                                List<Tuple2> basinsStrengths = civilizationsStrength.getOrDefault(civilization, new ArrayList<>());
                                basinsStrengths.add(Tuple.of(coordinates, strength));
                                civilizationsStrength.put(civilization, basinsStrengths);
                            }
                    );
                }
        );

        civilizationsStrength.forEach(
                (civilization, basinsStrengths) -> {
                    Set<Coordinates> alreadyPropagatedFrom = new HashSet<>();
                    while (!basinsStrengths.isEmpty()) {
                        basinsStrengths.sort(Comparator.comparingInt(tuple2 -> (Integer) tuple2._2));
                        Tuple2 strongestBasin = basinsStrengths.remove(basinsStrengths.size() - 1);

                        Coordinates coordinates = (Coordinates) strongestBasin._1;
                        int strength = (int) strongestBasin._2;
                        alreadyPropagatedFrom.add(coordinates);

                        Basin basin = newBasins.getOrDefault(coordinates, new Basin());
                        basin.getColonialStrength().merge(civilization, strength, Math::max);
                        newBasins.put(coordinates, basin);

                        if (strength > 1) {
                            coordinates
                                    .getNeighbours(simulationState.getSizeX(), simulationState.getSizeY())
                                    .filter(simulationState::isWater)
                                    .filter(
                                            targetCoordinates ->
                                                    !alreadyPropagatedFrom.contains(targetCoordinates)
                                    )
                                    .forEach(
                                            targetCoordinates ->
                                                    basinsStrengths.add(Tuple.of(targetCoordinates, strength - 1))
                                    );
                        }
                    }

                }
        );

        basins.clear();
        basins.putAll(newBasins);
    }

    private void colonize(SimulationState simulationState) {
        Map<Civilization, Map<Coordinates, Integer>> colonies = new HashMap<>();
        Map<Civilization, Integer> colonized = new HashMap<>();

        getPossibleColonies(simulationState).forEach(
                (coordinates, civilizations) -> {
                    int sum = civilizations.values().stream().mapToInt(Integer::intValue).sum();
                    int result = random.nextInt(sum);
                    int current = 0;

                    for (Map.Entry<Civilization, Integer> civilizationStrength : civilizations.entrySet()) {
                        current += civilizationStrength.getValue();
                        if (result < current) {
                            Map<Coordinates, Integer> coloniesForCivilization = colonies.getOrDefault(civilizationStrength.getKey(), new HashMap<>());
                            coloniesForCivilization.put(coordinates, civilizationStrength.getValue());
                            colonies.put(civilizationStrength.getKey(), coloniesForCivilization);
                            return;
                        }
                    }
                }
        );

        colonies.forEach(
                (civilization, targets) -> {
                    List<Tuple2> targetsOrdered =
                            targets.entrySet()
                                    .stream()
                                    .map(entry -> Tuple.of(entry.getKey(), entry.getValue()))
                                    .collect(Collectors.toList());

                    targetsOrdered.sort((t1, t2) -> ((Integer) t2._2).compareTo((Integer) t1._2));

                    targetsOrdered.forEach(
                            target -> {
                                if (colonized.getOrDefault(civilization, 0) >= getMaxColoniesPerFrame(civilization)) {
                                    return;
                                }

                                Coordinates coordinates = (Coordinates) target._1;
                                int strength = (int) target._2;
                                boolean successful = colonizeProvince(simulationState, civilization, coordinates, strength);

                                if (successful) {
                                    colonized.merge(civilization, 1, Integer::sum);
                                }
                            }
                    );
                }
        );
    }

    private boolean colonizeProvince(SimulationState simulationState, Civilization civilization, Coordinates coordinates, int colonialStrength) {
        int terrainFactor = simulationState.getTerrains().getTerrainFactor(simulationState.getTerrain(coordinates), civilization.getTechnologyLevel());

        int chance = (terrainFactor + (colonialStrength / 10)) * (5 + 2 * civilization.getEfficiencies().getAdministrativeEfficiency());
        int result = random.nextInt(100000);

        if (result >= chance) {
            return false;
        }

        simulationState.assignProvince(coordinates, civilization, 500);
        return true;
    }

    private int getMaxColoniesPerFrame(Civilization civilization) {
        return 1;
    }

    private Map<Coordinates, Map<Civilization, Integer>> getPossibleColonies(SimulationState simulationState) {
        Map<Coordinates, Map<Civilization, Integer>> possibleColonies = new HashMap<>();

        simulationState.getBasins().forEach(
                (coordinates, basin) ->
                        coordinates
                                .getNeighbours(simulationState.getSizeX(), simulationState.getSizeY())
                                .filter(simulationState::isLand)
                                .filter(simulationState::isProvinceUnoccupied)
                                .forEach(
                                        targetCoordinates -> {
                                            Map<Civilization, Integer> civilizationStrength = possibleColonies.getOrDefault(targetCoordinates, new HashMap<>());
                                            basin.getColonialStrength().forEach(
                                                    (civilization, strength) -> civilizationStrength.merge(civilization, strength, Integer::sum)
                                            );
                                            possibleColonies.put(targetCoordinates, civilizationStrength);
                                        }
                                )
        );

        return possibleColonies;
    }
}

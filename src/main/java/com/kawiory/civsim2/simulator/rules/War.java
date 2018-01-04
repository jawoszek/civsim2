package com.kawiory.civsim2.simulator.rules;

import com.kawiory.civsim2.simulator.*;
import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Kacper
 */
public class War implements Rule {

    private final Random random = new Random();

    @Override
    public void changeState(SimulationState simulationState) {
        checkForTruces(simulationState);
        checkForWarsBreakingOut(simulationState);
        processWars(simulationState);
    }

    @Override
    public void deleteCivilization(Civilization civilization) {

    }

    private void checkForTruces(SimulationState simulationState) {
        Set<Set<Civilization>> truces =
                simulationState
                        .getCivilizationsAtWar()
                        .entrySet()
                        .stream()
                        .filter(war -> rollForTruce(war.getValue()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet());

        truces.forEach(pair -> truce(simulationState, pair));
    }

    private boolean rollForTruce(Integer relationsBeforeWar) {
        return random.nextInt(10000) < 10 * relationsBeforeWar / 100;
    }

    private void truce(SimulationState simulationState, Set<Civilization> pair) {
        Set<Coordinates> coordinatesToSplit =
                pair
                        .stream()
                        .flatMap(civilization -> civilization.getOwnedProvinces().stream())
                        .collect(Collectors.toSet());

        int leadNeededToWin = 5;
        List<Tuple2<Coordinates, Map<Civilization, Long>>> coordinatesInOrder = orderByDifferences(simulationState, coordinatesToSplit, pair);
        boolean alreadyRefreshedOrder = false;

        while (!coordinatesToSplit.isEmpty()) {
            if (leadNeededToWin < 2) {
                return;
            }

            Tuple2<Coordinates, Map<Civilization, Long>> currentTuple = coordinatesInOrder.get(coordinatesInOrder.size() - 1);
            Coordinates currentCoordinates = currentTuple._1;
            Province currentProvince = simulationState.getProvince(currentCoordinates);

            Civilization stronger = currentTuple._2.entrySet().stream().filter(entry -> pair.contains(entry.getKey())).sorted(Comparator.comparingLong(map -> -map.getValue())).map(Map.Entry::getKey).findFirst().orElseThrow(IllegalStateException::new);
            Civilization weaker = currentTuple._2.entrySet().stream().filter(entry -> pair.contains(entry.getKey())).sorted(Comparator.comparingLong(Map.Entry::getValue)).map(Map.Entry::getKey).findFirst().orElseThrow(IllegalStateException::new);

            long difference = currentTuple._2.get(stronger) - currentTuple._2.get(weaker);

            if (difference < leadNeededToWin) {
                if (alreadyRefreshedOrder) {
                    leadNeededToWin--;
                    alreadyRefreshedOrder = false;
                    continue;
                }
                coordinatesInOrder = orderByDifferences(simulationState, coordinatesToSplit, pair);
                alreadyRefreshedOrder = true;
                continue;
            }

            coordinatesToSplit.remove(currentCoordinates);
            coordinatesInOrder.remove(coordinatesInOrder.size() - 1);

            if (currentProvince.getCivilization().equals(stronger)) {
                continue;
            }

            simulationState.assignProvince(currentCoordinates, stronger);
        }
    }

    private List<Tuple2<Coordinates, Map<Civilization, Long>>> orderByDifferences(SimulationState simulationState, Set<Coordinates> coordinatesSet, Set<Civilization> pair) {
        return coordinatesSet
                .stream()
                .map(coordinates -> Tuple.of(coordinates, countCivilizationsInVicinity(simulationState, coordinates)))
                .sorted(
                        Comparator.comparingLong(
                                tuple -> pair.stream().mapToLong(civilization -> tuple._2.getOrDefault(civilization, 0L)).max().orElse(0) - pair.stream().mapToLong(civilization -> tuple._2.getOrDefault(civilization, 0L)).min().orElse(0)
                        ))
                .collect(Collectors.toList());
    }

    private Map<Civilization, Long> countCivilizationsInVicinity(SimulationState simulationState, Coordinates coordinates) {
        return coordinates
                .getNeighbours(simulationState.getSizeX(), simulationState.getSizeY(), 2)
                .filter(simulationState::isProvinceTaken)
                .map(simulationState::getProvince)
                .map(Province::getCivilization)
                .collect(
                        Collectors.groupingBy(
                                Function.identity(),
                                Collectors.counting()
                        )
                );
    }

    private void checkForWarsBreakingOut(SimulationState simulationState) {
        simulationState
                .getNeighboursPairs()
                .stream()
                .filter(pair -> !simulationState.getCivilizationsAtWar().containsKey(pair))
                .filter(pair -> rollForWar(simulationState, pair))
                .forEach(pair -> warBreaks(simulationState, pair));
    }

    private void warBreaks(SimulationState simulationState, Set<Civilization> pair) {
        simulationState.getCivilizationsAtWar().put(pair, getRelationsFactor(simulationState, pair));
    }

    private boolean rollForWar(SimulationState simulationState, Set<Civilization> pair) {
        int chanceForWar = 1000 * getRelationsFactor(simulationState, pair) / 1000000;

        return random.nextInt(100) < chanceForWar;
    }

    private int getRelationsFactor(SimulationState simulationState, Set<Civilization> pair) {
        return simulationState.getRelations().getOrDefault(pair, 3000) + getGovernmentFactor(pair) + getEfficiencyFactor(pair);
    }

    private int getGovernmentFactor(Set<Civilization> pair) {
        int max = pair.stream().mapToInt(Civilization::getGovernmentID).max().orElse(0);
        int min = pair.stream().mapToInt(Civilization::getGovernmentID).min().orElse(0);

        return 2000 - ((max - min) * 1000);
    }

    private int getEfficiencyFactor(Set<Civilization> pair) {
        int politicalEfficiency =
                pair.stream()
                        .map(Civilization::getEfficiencies)
                        .mapToInt(Efficiencies::getPoliticalEfficiency)
                        .max()
                        .orElse(0);

        return (politicalEfficiency + 2) * 500;
    }

    private void processWars(SimulationState simulationState) {
        simulationState
                .getCivilizationsAtWar()
                .forEach(
                        (pair, relations) -> processWar(simulationState, pair)
                );
    }

    private void processWar(SimulationState simulationState, Set<Civilization> pair) {
        Set<Coordinates> conqueredCoordinates = new HashSet<>();

        pair.stream()
                .map(Civilization::getOwnedProvinces)
                .flatMap(Collection::stream)
                .filter(coordinates -> hasEnemyNeighbours(simulationState, coordinates, pair))
                .forEach(
                        currentCoordinates -> {
                            int defenderPopulation = getDefenderPopulation(simulationState, currentCoordinates);
                            int aggressorPopulation = getAggressorPopulation(simulationState, currentCoordinates, pair);

                            int basicChance = getOccupationChance(simulationState, defenderPopulation, aggressorPopulation, simulationState.getTerrain(currentCoordinates));
                            int chance = basicChance * getMilitaryEfficiencyFactor(simulationState, currentCoordinates, pair);

                            if (random.nextInt(400) > chance) {
                                return;
                            }

                            conqueredCoordinates.add(currentCoordinates);
                        }
                );

        conqueredCoordinates.forEach(
                coordinates -> {
                    Province occupiedProvince = simulationState.getProvince(coordinates);
                    Civilization defender = occupiedProvince.getCivilization();
                    Civilization occupant = pair.stream().filter(civilization -> !civilization.equals(defender)).findAny().orElse(null);

                    occupiedProvince.setPopulation(Math.max(occupiedProvince.getPopulation() * 90 / 100, 100));
                    simulationState.assignProvince(coordinates, occupant);
                }
        );

        conqueredCoordinates
                .stream()
                .flatMap(coordinates -> coordinates.getNeighbours(simulationState.getSizeX(), simulationState.getSizeY()))
                .distinct()
                .filter(coordinates -> !conqueredCoordinates.contains(coordinates))
                .filter(simulationState::isProvinceTaken)
                .map(simulationState::getProvince)
                .filter(province -> pair.contains(province.getCivilization()))
                .forEach(province -> province.setPopulation(Math.max(province.getPopulation() * 95 / 100, 100)));
    }

    private boolean hasEnemyNeighbours(SimulationState simulationState, Coordinates coordinates, Set<Civilization> pair) {
        return coordinates.getNeighbours(simulationState.getSizeX(), simulationState.getSizeY())
                .filter(simulationState::isProvinceTaken)
                .map(simulationState::getProvince)
                .map(Province::getCivilization)
                .filter(pair::contains)
                .anyMatch(civilization -> !simulationState.getProvince(coordinates).getCivilization().equals(civilization));
    }

    private int getDefenderPopulation(SimulationState simulationState, Coordinates coordinates) {
        Civilization defenderCivilization = simulationState.getProvince(coordinates).getCivilization();

        return simulationState.getProvince(coordinates).getPopulation() +
                coordinates.getNeighbours(simulationState.getSizeX(), simulationState.getSizeY())
                .filter(simulationState::isProvinceTaken)
                .map(simulationState::getProvince)
                .filter(province -> province.getCivilization().equals(defenderCivilization))
                .mapToInt(Province::getPopulation)
                .sum();
    }

    private int getAggressorPopulation(SimulationState simulationState, Coordinates coordinates, Set<Civilization> pair) {
        Civilization defenderCivilization = simulationState.getProvince(coordinates).getCivilization();

        return coordinates.getNeighbours(simulationState.getSizeX(), simulationState.getSizeY())
                .filter(simulationState::isProvinceTaken)
                .map(simulationState::getProvince)
                .filter(province -> pair.contains(province.getCivilization()))
                .filter(province -> !province.getCivilization().equals(defenderCivilization))
                .mapToInt(Province::getPopulation)
                .sum();
    }

    private int getOccupationChance(SimulationState simulationState, int defenderPopulation, int aggressorPopulation, int terrain) {
        int defenderStrength = (100 + simulationState.getTerrains().getTerrainDefenseBonus(terrain)) * defenderPopulation;

        return 10000 * aggressorPopulation / defenderStrength;
    }

    private int getMilitaryEfficiencyFactor(SimulationState simulationState, Coordinates coordinates, Set<Civilization> pair) {
        Civilization defender = simulationState.getProvince(coordinates).getCivilization();
        Civilization attacker = pair.stream().filter(civilization -> !civilization.equals(defender)).findAny().orElseThrow(IllegalStateException::new);

        int militaryEfficiencyDifference = attacker.getEfficiencies().getMilitaryEfficiency() - defender.getEfficiencies().getMilitaryEfficiency();

        return militaryEfficiencyDifference + 4;
    }
}

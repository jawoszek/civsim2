package com.kawiory.civsim2.simulator;

import com.google.common.collect.ImmutableSet;
import com.kawiory.civsim2.persistance.DataProvider;
import com.kawiory.civsim2.simulator.naming.NameGenerator;
import com.kawiory.civsim2.simulator.rules.Rule;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Kacper
 */

public class SimulationState {

    private int simulationID;

    private final DataProvider dataProvider;
    private final NameGenerator nameGenerator;
    private final Terrains terrains;

    private int currentFrame;
    private int[][] map;

    private final List<Rule> rules;

    private final Set<Civilization> civilizations;
    private final Map<Civilization, Map<Civilization, Set<Coordinates>>> neighbours;

    private final Map<Coordinates, Province> provinces;
    private final Map<Coordinates, Basin> basins;

    private final Map<Set<Civilization>, Integer> relations;
    private final Map<Set<Civilization>, Integer> civilizationsAtWar;

    public SimulationState(int simulationID, DataProvider dataProvider, NameGenerator nameGenerator, Terrains terrains, List<Rule> rules) {
        this.simulationID = simulationID;
        this.nameGenerator = nameGenerator;
        this.dataProvider = dataProvider;
        this.terrains = terrains;
        this.currentFrame = 0;
        this.rules = rules;
        this.civilizations = new HashSet<>();
        this.neighbours = new HashMap<>();
        this.provinces = new HashMap<>();
        this.basins = new HashMap<>();
        this.relations = new HashMap<>();
        this.civilizationsAtWar = new HashMap<>();
    }

    public int getSimulationID() {
        return simulationID;
    }

    public void setSimulationID(int simulationID) {
        this.simulationID = simulationID;
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

    public Map<Civilization, Map<Civilization, Set<Coordinates>>> getNeighbours() {
        return neighbours;
    }

    public Map<Set<Civilization>, Integer> getRelations() {
        return relations;
    }

    public Map<Set<Civilization>, Integer> getCivilizationsAtWar() {
        return civilizationsAtWar;
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

    public int getTerrain(Coordinates coordinates) {
        return map[coordinates.getX()][coordinates.getY()];
    }

    public boolean isProvinceTaken(int x, int y) {
        return provinces.containsKey(new Coordinates(x, y));
    }

    public boolean isProvinceTaken(Coordinates coordinates) {
        return provinces.containsKey(coordinates);
    }

    public boolean isProvinceUnoccupied(Coordinates coordinates) {
        return !provinces.containsKey(coordinates);
    }

    public boolean isWater(Coordinates coordinates) {
        return getTerrain(coordinates) == 0;
    }

    public boolean isLand(Coordinates coordinates) {
        return !isWater(coordinates);
    }

    public Province getProvince(int x, int y) {
        return provinces.get(new Coordinates(x, y));
    }

    public Province getProvince(Coordinates coordinates) {
        return provinces.get(coordinates);
    }

    public Civilization createCivilization() {
        return createCivilization(nameGenerator.generateName());
    }

    public Civilization createCivilization(String name) {
        return createCivilization(name, 0, 0);
    }

    public Civilization createCivilization(int government, int technologyLevel) {
        int newCivilizationID = dataProvider.insertCiv(simulationID, nameGenerator.generateName(), dataProvider.getRandomColor());
        Civilization newCivilization = new Civilization(newCivilizationID, nameGenerator.generateName(), government, technologyLevel);
        civilizations.add(newCivilization);
        neighbours.put(newCivilization, new HashMap<>());
        return newCivilization;
    }

    public Civilization createCivilization(String name, int government, int technologyLevel) {
        int newCivilizationID = dataProvider.insertCiv(simulationID, name, dataProvider.getRandomColor());
        Civilization newCivilization = new Civilization(newCivilizationID, name, government, technologyLevel);
        civilizations.add(newCivilization);
        neighbours.put(newCivilization, new HashMap<>());
        return newCivilization;
    }

    public int getTerrainFactor(Province province) {
        return terrains.getTerrainFactor(province.getTerrain(), province.getCivilization().getTechnologyLevel());
    }

    public int getMaxPopulation(Province province) {
        return terrains.getMaxPopulation(province.getTerrain(), province.getCivilization().getTechnologyLevel());
    }

    private void removeCivilization(Civilization civilization) {
        civilizations.remove(civilization);
        rules.forEach(rule -> rule.deleteCivilization(this, civilization));
    }

    public Province removeProvince(Coordinates coordinates) {
        Province province = provinces.remove(coordinates);
        Civilization civilization = province.getCivilization();

        province.setCivilization(null);
        civilization.getOwnedProvinces().remove(coordinates);
        removeNeighbours(civilization, coordinates);

        if (civilization.getProvincesCount() == 0) {
            removeCivilization(civilization);
        }

        return province;
    }

    public Province assignProvince(Coordinates coordinates, Civilization civilization, int population) {
        Province province;
        if (provinces.containsKey(coordinates)) {
            province = removeProvince(coordinates);
            if (population >= 0) {
                province.setPopulation(population);
            }
            province.setCivilization(civilization);
        } else {
            if (population < 0) {
                population = 0;
            }
            int terrain = getTerrain(coordinates);
            province = new Province(terrain, civilization, Math.min(population, terrains.getMaxPopulation(terrain, civilization.getTechnologyLevel())));
        }
        provinces.put(coordinates, province);
        civilization.getOwnedProvinces().add(coordinates);
        addNeighbours(civilization, coordinates);
        return province;
    }

    public Province assignProvince(Coordinates coordinates, Civilization civilization) {
        return assignProvince(coordinates, civilization, -1);
    }

    private void addNeighbours(Civilization civilization, Coordinates coordinates) {
        coordinates
                .getNeighbours(getSizeX(), getSizeY())
                .forEach(
                        neighbourCoordinates -> {
                            Province neighbourProvince = getProvince(neighbourCoordinates);
                            if (neighbourProvince == null) {
                                return;
                            }
                            addCoordinatesToNeighbourhood(civilization, coordinates, neighbourProvince.getCivilization());
                            addCoordinatesToNeighbourhood(neighbourProvince.getCivilization(), neighbourCoordinates, civilization);
                        }
                );
    }

    private void removeNeighbours(Civilization civilization, Coordinates coordinates) {
        coordinates
                .getNeighbours(getSizeX(), getSizeY())
                .forEach(
                        neighbourCoordinates -> {
                            Province neighbourProvince = getProvince(neighbourCoordinates);
                            if (neighbourProvince == null) {
                                return;
                            }
                            removeCoordinatesFromNeighbourhood(civilization, coordinates, neighbourProvince.getCivilization());
                            removeCoordinatesFromNeighbourhood(neighbourProvince.getCivilization(), neighbourCoordinates, civilization);
                        }
                );
    }

    private void addCoordinatesToNeighbourhood(Civilization civilization, Coordinates coordinates,
                                               Civilization neighbourCivilization) {
        if (civilization.getId() == neighbourCivilization.getId()) {
            return;
        }
        Map<Civilization, Set<Coordinates>> centralCivilization = getNeighbours().getOrDefault(civilization, new HashMap<>());
        Set<Coordinates> centralCoordinates = centralCivilization.getOrDefault(neighbourCivilization, new HashSet<>());
        centralCoordinates.add(coordinates);
        centralCivilization.put(neighbourCivilization, centralCoordinates);
        getNeighbours().put(civilization, centralCivilization);
    }

    private void removeCoordinatesFromNeighbourhood(Civilization civilization, Coordinates coordinates,
                                                    Civilization neighbourCivilization) {
        if (civilization.getId() == neighbourCivilization.getId()) {
            return;
        }
        Map<Civilization, Set<Coordinates>> centralCivilization = getNeighbours().get(civilization);
        Set<Coordinates> centralCoordinates = centralCivilization.getOrDefault(neighbourCivilization, new HashSet<>());
        if (!coordinatesHasNeighbourWithCivilization(coordinates, neighbourCivilization)) {
            centralCoordinates.remove(coordinates);
        }
        if (centralCoordinates.isEmpty()) {
            centralCivilization.remove(neighbourCivilization);
        }
    }

    private boolean coordinatesHasNeighbourWithCivilization(Coordinates coordinates, Civilization civilization) {
        return coordinates
                .getNeighbours(getSizeX(), getSizeY())
                .filter(this::isProvinceTaken)
                .map(this::getProvince)
                .anyMatch(province -> province.getCivilization().getId() == civilization.getId());
    }

    public Set<Set<Civilization>> getNeighboursPairs() {
        return neighbours
                .entrySet()
                .stream()
                .flatMap(
                        neighbourEntry ->
                                neighbourEntry
                                        .getValue()
                                        .entrySet()
                                        .stream()
                                        .filter(neighbourCoordinatesEntry -> !neighbourCoordinatesEntry.getValue().isEmpty())
                                        .map(Map.Entry::getKey)
                                        .map(neighbour -> ImmutableSet.of(neighbourEntry.getKey(), neighbour))
                ).collect(Collectors.toSet());
    }

    public Set<Set<Civilization>> getNeighboursPairs(Civilization civilization) {
        return neighbours
                .getOrDefault(civilization, new HashMap<>())
                .entrySet()
                .stream()
                .filter(neighbourCoordinatesEntry -> !neighbourCoordinatesEntry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .map(neighbour -> ImmutableSet.of(civilization, neighbour))
                .collect(Collectors.toSet());
    }

    public boolean isCivilizationAtWar(Civilization civilization) {
        return civilizationsAtWar
                .keySet()
                .stream()
                .anyMatch(pair -> pair.contains(civilization));
    }
}

package com.kawiory.civsim2.persistance;

import com.kawiory.civsim2.simulator.Coordinates;
import com.kawiory.civsim2.simulator.Province;
import com.kawiory.civsim2.simulator.Simulation;
import com.kawiory.civsim2.spring.model.*;

import java.util.List;
import java.util.Map;

/**
 * @author Kacper
 */

public interface DataProvider {

    // server side

    List<WorldPreview> getWorldPreviews();

    int getWorldsCount();

    WorldDetails getWorldDetails(int id);

    Map<Integer, String> getColors();

    SimulationsList getSimulationsList();

    SimulationDetails getSimulationDetails(int id);

    SimulationFrame getSimulationFrame(int simID, int frameNumber);

    List<SimulationFrame> getSimulationFrames(int simID);

    ProvinceDetails getProvinceDetails(int frameID, int x, int y);

    Map<Integer, Map<String, String>> terrainProperty();

    Map<Integer, Map<String, String>> property(String name);

    Map<String, List<String>> properties(int id);

    String getMapName(int id);

    Map<List<Integer>, String> getPropertyForFrame(int id, String name);

    // simulation side

    int[][] getWorldMap(int id);

    int insertSimulation(Simulation simulation);

    int insertCiv(int simid, String name, int colorID);

    int insertFrame(int number, int simid, Map<Coordinates, Province> map);

    int insertMap(String name, int[][] map);

    void deleteSimulation(int simID);

    int getRandomColor();

}

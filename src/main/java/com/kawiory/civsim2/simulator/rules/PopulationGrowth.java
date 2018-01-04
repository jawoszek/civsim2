package com.kawiory.civsim2.simulator.rules;

import com.kawiory.civsim2.simulator.Civilization;
import com.kawiory.civsim2.simulator.Coordinates;
import com.kawiory.civsim2.simulator.Province;
import com.kawiory.civsim2.simulator.SimulationState;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kacper
 */
public class PopulationGrowth implements Rule {

    @Override
    public void changeState(SimulationState simulationState) {
        Set<Coordinates> overpopulatedProvinces = new HashSet<>();

        simulationState
                .getProvinces()
                .forEach(
                        (coordinates, province) ->
                        {
                            int currentPopulation = province.getPopulation();
                            int growthRatio = getGrowthRatio(province, simulationState);

                            int additionalPopulation = growthRatio * currentPopulation / 1000000;
                            int newPopulation = additionalPopulation + currentPopulation;
                            province.setPopulation(newPopulation);
                            if (isOverpopulated(province, simulationState)){
                                overpopulatedProvinces.add(coordinates);
                            }
                        }
                );

        // TODO relocate population from overpopulated provinces
    }

    @Override
    public void deleteCivilization(Civilization civilization) {

    }

    private int getGrowthRatio(Province province, SimulationState simulationState) {
        int basicGrowth = getBasicGrowthRatio(province);

        int terrainFactor = simulationState.getTerrainFactor(province);

        int populationDensityFactor = 100 - ((80 * province.getPopulation()) / simulationState.getMaxPopulation(province));

        return basicGrowth * terrainFactor * populationDensityFactor;
    }

    private int getBasicGrowthRatio(Province province) {
        return 10 + province.getCivilization().getEfficiencies().getAdministrativeEfficiency() * 2;
    }

    private boolean isOverpopulated(Province province, SimulationState simulationState) {
        int maxPopulation = simulationState.getMaxPopulation(province);

        return province.getPopulation() > maxPopulation;
    }
}

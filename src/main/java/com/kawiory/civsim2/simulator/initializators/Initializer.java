package com.kawiory.civsim2.simulator.initializators;

import com.kawiory.civsim2.simulator.SimulationState;

/**
 * @author Kacper
 */
public interface Initializer {

    void initializeCivilizations(int initialCivilizationsCount, SimulationState simulationState);
}

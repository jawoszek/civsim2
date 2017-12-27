package com.kawiory.civsim2.simulator.rules;

import com.kawiory.civsim2.simulator.SimulationState;

/**
 * @author Kacper
 */
public interface Rule {

    void changeState(SimulationState simulationState);
}

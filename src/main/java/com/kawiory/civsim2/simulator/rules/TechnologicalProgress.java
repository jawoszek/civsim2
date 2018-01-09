package com.kawiory.civsim2.simulator.rules;

import com.kawiory.civsim2.simulator.Civilization;
import com.kawiory.civsim2.simulator.SimulationState;

import java.util.Random;

/**
 * @author Kacper
 */
public class TechnologicalProgress implements Rule {

    private final Random random = new Random();

    @Override
    public void changeState(SimulationState simulationState) {
        simulationState
                .getCivilizations()
                .forEach(civilization -> processCivilization(simulationState, civilization));
    }

    @Override
    public void deleteCivilization(SimulationState simulationState, Civilization civilization) {

    }

    private void processCivilization(SimulationState simulationState, Civilization civilization) {
        int basicTechnologyIncome = 2 + civilization.getEfficiencies().getScienceEfficiency();
        int randomTechnologicalIncome = random.nextInt(3);
        int warTechnologicalIncome = simulationState.isCivilizationAtWar(civilization) ? 3 : 0;

        int addedTechnologicalLevel = basicTechnologyIncome + randomTechnologicalIncome + warTechnologicalIncome;
        int resultedTechnologicalLevel = civilization.getTechnologyLevel() + addedTechnologicalLevel;
        civilization.setTechnologyLevel(resultedTechnologicalLevel);
    }
}

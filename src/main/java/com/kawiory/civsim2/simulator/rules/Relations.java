package com.kawiory.civsim2.simulator.rules;

import com.kawiory.civsim2.simulator.Civilization;
import com.kawiory.civsim2.simulator.SimulationState;

/**
 * @author Kacper
 */
public class Relations implements Rule {
    @Override
    public void changeState(SimulationState simulationState) {
        simulationState
                .getNeighboursPairs()
                .forEach(
                        pair -> {
                            int currentRelation = simulationState.getRelations().getOrDefault(pair, 3000);
                            if (simulationState.getCivilizationsAtWar().containsKey(pair)) {
                                return;
                            }
                            int valueToAdd = pair.stream().mapToInt(civilization -> getBasicRelationGrowth(civilization) - civilization.getProvincesRatio()).sum();
                            currentRelation += Math.max(0,Math.min(6000, currentRelation + valueToAdd));
                            simulationState.getRelations().put(pair, currentRelation);
                        }
                );
    }

    @Override
    public void deleteCivilization(Civilization civilization) {

    }

    private int getBasicRelationGrowth(Civilization civilization) {
        int technologyLevel = civilization.getTechnologyLevel();

        return 100;
    }
}

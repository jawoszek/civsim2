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
                            valueToAdd = normalizeValueToAdd(valueToAdd);
                            currentRelation = Math.max(0,Math.min(6000, currentRelation + valueToAdd));
                            simulationState.getRelations().put(pair, currentRelation);
                        }
                );
    }

    @Override
    public void deleteCivilization(SimulationState simulationState, Civilization civilization) {

    }

    private int normalizeValueToAdd(int valueToAdd) {
        if (valueToAdd > 100) {
            return 5;
        }

        if (valueToAdd > 0) {
            return 2;
        }

        if (valueToAdd < -100) {
            return -5;
        }

        if (valueToAdd < 0) {
            return -2;
        }

        return 0;
    }

    private int getBasicRelationGrowth(Civilization civilization) {
        int technologyLevel = civilization.getTechnologyLevel();
        int technologyBonus = 0;

        if (technologyLevel > 600) {
            technologyBonus += 50;
        }

        if (technologyLevel > 1400) {
            technologyBonus += 50;
        }

        if (technologyLevel > 1800) {
            technologyBonus += 50;
        }

        return 50 + technologyBonus;
    }
}

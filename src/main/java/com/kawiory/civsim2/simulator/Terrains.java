package com.kawiory.civsim2.simulator;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * @author Kacper
 */

public class Terrains {

    private final static Set<Integer> unhabitable = ImmutableSet.of(0, 6, 7, 11);

    public boolean isHabitable(int terrain, int technologyLevel) {
        return getMaxPopulation(terrain, technologyLevel) > 0;
    }

    public boolean isWasteland(int terrain, int technologyLevel) {
        return (terrain == 6 || terrain == 7 || terrain == 11);
    }

    public int getMaxPopulation(int terrain, int technologyLevel) {
        return 100;
    }


}

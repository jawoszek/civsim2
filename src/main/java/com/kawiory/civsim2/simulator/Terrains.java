package com.kawiory.civsim2.simulator;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * @author Kacper
 */

public class Terrains {

    private final static int[] basicFactor = {0, 80, 5, 10, 20, 40, 0, 50, 15, 10, 20, 0};
    private final static Set<Integer> unhabitable = ImmutableSet.of(0, 6, 7, 11);

    public boolean isHabitable(int terrain, int technologyLevel) {
        return getMaxPopulation(terrain, technologyLevel) > 0;
    }

    public boolean isWasteland(int terrain, int technologyLevel) {
        return !isHabitable(terrain, technologyLevel);
    }

    public int getMaxPopulation(int terrain, int technologyLevel) {
        return 1000 * getTerrainFactor(terrain, technologyLevel);
    }

    public int getTerrainFactor(int terrain, int technologyLevel) {
        return getBasicFactor(terrain);
    }

    private int getBasicFactor(int terrain) {
        return basicFactor[terrain];
    }
}

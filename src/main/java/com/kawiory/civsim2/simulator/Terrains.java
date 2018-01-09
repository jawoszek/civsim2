package com.kawiory.civsim2.simulator;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * @author Kacper
 */

public class Terrains {

    private final static int[] basicFactor = {0, 60, 5, 10, 20, 30, 0, 50, 15, 10, 20, 0};
    private final static Set<Integer> unhabitable = ImmutableSet.of(0, 6, 7, 11);

    public boolean isHabitable(int terrain, int technologyLevel) {
        return getMaxPopulation(terrain, technologyLevel) > 0;
    }

    public boolean isWasteland(int terrain, int technologyLevel) {
        return !isHabitable(terrain, technologyLevel);
    }

    public int getMaxPopulation(int terrain, int technologyLevel) {
        int fromTechnology = 0;

        if (technologyLevel > 800) {
            fromTechnology += 20000;
        }

        return 1000 * getTerrainFactor(terrain, technologyLevel) + fromTechnology;
    }

    public int getTerrainFactor(int terrain, int technologyLevel) {
        int fromTechnology = 0;

        if (technologyLevel > 90) {
            fromTechnology += 10;
        }

        if (technologyLevel > 190) {
            fromTechnology += 5;
        }

        if (technologyLevel > 240 && (terrain == 2 || terrain == 5 || terrain == 7)) {
            fromTechnology += 20;
        }

        if (technologyLevel > 340) {
            fromTechnology += 5;
        }

        if (technologyLevel > 500 && (terrain == 4)) {
            fromTechnology += 30;
        }

        if (technologyLevel > 720) {
            fromTechnology += 5;
        }

        if (technologyLevel > 1200) {
            fromTechnology += 5;
        }

        if (technologyLevel > 2200) {
            fromTechnology += 30;
        }

        return getBasicFactor(terrain) + fromTechnology;
    }

    private int getBasicFactor(int terrain) {
        return basicFactor[terrain];
    }

    public int getTerrainDefenseBonus(int terrain) {
        switch (terrain) {
            case 2:
                return 20;
            case 3:
                return 80;
            case 4:
                return 30;
            case 6:
                return 100;
            case 8:
                return 50;
            case 9:
                return 60;
            default:
                return 0;
        }
    }
}

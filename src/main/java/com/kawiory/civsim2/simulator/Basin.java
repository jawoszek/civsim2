package com.kawiory.civsim2.simulator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kacper
 */
public class Basin {

    private final Map<Civilization, Integer> colonialStrength;

    public Basin() {
        this.colonialStrength = new HashMap<>();
    }
}

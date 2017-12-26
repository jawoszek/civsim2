package com.kawiory.civsim2.spring.model;

/**
 * @author Kacper
 */

public class SimulationDetails {

    private SimulationPreview simPrev;

    public SimulationDetails(SimulationPreview simPrev) {
        this.simPrev = simPrev;
    }

    public SimulationPreview getSimPrev() {
        return simPrev;
    }
}

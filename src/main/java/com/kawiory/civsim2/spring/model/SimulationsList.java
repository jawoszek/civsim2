package com.kawiory.civsim2.spring.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kacper
 */

public class SimulationsList {

    private final List<SimulationPreview> done = new ArrayList<>();
    private final List<SimulationPreview> inProgress = new ArrayList<>();
    private final List<SimulationPreview> waiting = new ArrayList<>();

    public void addDone(SimulationPreview simulation) {
        done.add(simulation);
    }

    public void addInProgress(SimulationPreview simulation) {
        inProgress.add(simulation);
    }

    public void addWaiting(SimulationPreview simulation) {
        waiting.add(simulation);
    }

    public List<SimulationPreview> getDone() {
        return done;
    }

    public List<SimulationPreview> getInProgress() {
        return inProgress;
    }

    public List<SimulationPreview> getWaiting() {
        return waiting;
    }

    public SimulationsList merge(SimulationsList other) {
        done.addAll(other.getDone());
        inProgress.addAll(other.getInProgress());
        waiting.addAll(other.getWaiting());
        return this;
    }
}

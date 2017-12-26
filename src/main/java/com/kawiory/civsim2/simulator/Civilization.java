package com.kawiory.civsim2.simulator;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kacper
 */

public class Civilization {

    private final int id;
    private final String name;

    private int governmentID;
    private int technologyLevel;

    private final Set<Coordinates> ownedProvinces;

    public Civilization(int id, String name, int governmentID, int technologyLevel) {
        this.id = id;
        this.name = name;
        this.governmentID = governmentID;
        this.technologyLevel = technologyLevel;
        this.ownedProvinces = new HashSet<>();
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getGovernmentID() {
        return governmentID;
    }

    public void setGovernmentID(int governmentID) {
        this.governmentID = governmentID;
    }

    public int getTechnologyLevel() {
        return technologyLevel;
    }

    public void setTechnologyLevel(int technologyLevel) {
        this.technologyLevel = technologyLevel;
    }

    public Set<Coordinates> getOwnedProvinces() {
        return ownedProvinces;
    }
}

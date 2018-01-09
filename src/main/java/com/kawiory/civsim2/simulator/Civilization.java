package com.kawiory.civsim2.simulator;

import com.google.common.base.Objects;

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
    private final Efficiencies efficiencies;

    public Civilization(int id, String name, int governmentID, int technologyLevel) {
        this.id = id;
        this.name = name;
        this.governmentID = governmentID;
        this.technologyLevel = technologyLevel;
        this.ownedProvinces = new HashSet<>();
        this.efficiencies = new Efficiencies();
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

    public int getMaxProvincesCount() {
        int fromTechnology = 0;
        if (technologyLevel > 190) {
            fromTechnology += 1000;
        }

        if (technologyLevel > 500) {
            fromTechnology += 2000;
        }

        if (technologyLevel > 800) {
            fromTechnology += 4000;
        }

        if (technologyLevel > 1400) {
            fromTechnology += 4000;
        }

        if (technologyLevel > 1800) {
            fromTechnology += 8000;
        }

        int fromGovernment = 0;

        if (governmentID == 1) {
            fromGovernment += 500;
        }

        return 1000 + fromTechnology + fromGovernment;
    }

    public int getProvincesCount() {
        return ownedProvinces.size();
    }

    public int getProvincesRatio() {
        return 100 * getProvincesCount() / getMaxProvincesCount();
    }

    public Efficiencies getEfficiencies() {
        return efficiencies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Civilization)) return false;
        Civilization that = (Civilization) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

package com.kawiory.civsim2.simulator;

/**
 * @author Kacper
 */
public class Efficiencies {

    private boolean hadGoldenAge;
    private boolean hadDarkAge;
    private int timeToNextChange;
    private int administrativeEfficiency;
    private int politicalEfficiency;
    private int scienceEfficiency;
    private int militaryEfficiency;

    public int getAdministrativeEfficiency() {
        return administrativeEfficiency;
    }

    public void setAdministrativeEfficiency(int administrativeEfficiency) {
        this.administrativeEfficiency = administrativeEfficiency;
    }

    public int getPoliticalEfficiency() {
        return politicalEfficiency;
    }

    public void setPoliticalEfficiency(int politicalEfficiency) {
        this.politicalEfficiency = politicalEfficiency;
    }

    public int getScienceEfficiency() {
        return scienceEfficiency;
    }

    public void setScienceEfficiency(int scienceEfficiency) {
        this.scienceEfficiency = scienceEfficiency;
    }

    public int getTimeToNextChange() {
        return timeToNextChange;
    }

    public void setTimeToNextChange(int timeToNextChange) {
        this.timeToNextChange = timeToNextChange;
    }

    public boolean isHadGoldenAge() {
        return hadGoldenAge;
    }

    public void setHadGoldenAge(boolean hadGoldenAge) {
        this.hadGoldenAge = hadGoldenAge;
    }

    public boolean isHadDarkAge() {
        return hadDarkAge;
    }

    public void setHadDarkAge(boolean hadDarkAge) {
        this.hadDarkAge = hadDarkAge;
    }

    public int getMilitaryEfficiency() {
        return militaryEfficiency;
    }

    public void setMilitaryEfficiency(int militaryEfficiency) {
        this.militaryEfficiency = militaryEfficiency;
    }
}

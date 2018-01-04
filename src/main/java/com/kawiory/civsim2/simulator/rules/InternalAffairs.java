package com.kawiory.civsim2.simulator.rules;

import com.kawiory.civsim2.simulator.Civilization;
import com.kawiory.civsim2.simulator.Efficiencies;
import com.kawiory.civsim2.simulator.SimulationState;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Kacper
 */
public class InternalAffairs implements Rule {

    private final Random random = new Random();

    @Override
    public void changeState(SimulationState simulationState) {
        simulationState.getCivilizations().forEach(this::processCivilization);
    }

    @Override
    public void deleteCivilization(Civilization civilization) {

    }

    private void processCivilization(Civilization civilization) {
        Efficiencies efficiencies = civilization.getEfficiencies();

        int timeToNextChange = efficiencies.getTimeToNextChange();
        if (timeToNextChange > 0) {
            efficiencies.setTimeToNextChange(timeToNextChange - 1);
            return;
        }

        int rollForAges = random.nextInt(100);

        if (allPositive(efficiencies) && rollForAges < getGoldenAgeChance(efficiencies)) {
            setGoldenAge(civilization);
            return;
        }

        if (allNegative(efficiencies) && rollForAges < getDarkAgeChance(efficiencies)) {
            setDarkAge(civilization);
            return;
        }

        int rollForType = random.nextInt(4);

        switch (rollForType) {
            case 0:
                changeEfficiency(
                        efficiencies::setAdministrativeEfficiency,
                        efficiencies::getAdministrativeEfficiency,
                        efficiencies);
                break;
            case 1:
                changeEfficiency(
                        efficiencies::setPoliticalEfficiency,
                        efficiencies::getPoliticalEfficiency,
                        efficiencies);
                break;
            case 2:
                changeEfficiency(
                        efficiencies::setScienceEfficiency,
                        efficiencies::getScienceEfficiency,
                        efficiencies);
                break;
            case 3:
                changeEfficiency(
                        efficiencies::setMilitaryEfficiency,
                        efficiencies::getMilitaryEfficiency,
                        efficiencies);
                break;
        }
    }

    private void setDarkAge(Civilization civilization) {
        Efficiencies efficiencies = civilization.getEfficiencies();
        efficiencies.setHadDarkAge(true);
        efficiencies.setAdministrativeEfficiency(-2);
        efficiencies.setScienceEfficiency(-2);
        efficiencies.setPoliticalEfficiency(-2);
        efficiencies.setMilitaryEfficiency(-2);
        efficiencies.setTimeToNextChange(20);
    }

    private void setGoldenAge(Civilization civilization) {
        Efficiencies efficiencies = civilization.getEfficiencies();
        efficiencies.setHadGoldenAge(true);
        efficiencies.setAdministrativeEfficiency(2);
        efficiencies.setScienceEfficiency(2);
        efficiencies.setPoliticalEfficiency(2);
        efficiencies.setMilitaryEfficiency(2);
        efficiencies.setTimeToNextChange(20);
    }

    private boolean allPositive(Efficiencies efficiencies) {
        return efficiencies.getAdministrativeEfficiency() > 0 && efficiencies.getPoliticalEfficiency() > 0 && efficiencies.getScienceEfficiency() > 0 && efficiencies.getMilitaryEfficiency() > 0;
    }

    private boolean allNegative(Efficiencies efficiencies) {
        return efficiencies.getAdministrativeEfficiency() < 0 && efficiencies.getPoliticalEfficiency() < 0 && efficiencies.getScienceEfficiency() < 0 && efficiencies.getMilitaryEfficiency() < 0;
    }

    private int getGoldenAgeChance(Efficiencies efficiencies) {
        return (efficiencies.getAdministrativeEfficiency() + efficiencies.getPoliticalEfficiency() + efficiencies.getAdministrativeEfficiency() + efficiencies.getMilitaryEfficiency()) * 5;
    }

    private int getDarkAgeChance(Efficiencies efficiencies) {
        return -(efficiencies.getAdministrativeEfficiency() + efficiencies.getPoliticalEfficiency() + efficiencies.getAdministrativeEfficiency() + efficiencies.getMilitaryEfficiency()) * 5;
    }

    private void changeEfficiency(Consumer<Integer> setter, Supplier<Integer> getter, Efficiencies efficiencies) {
        int chanceToGoUp;
        int timeBeforeNextIfWentUp;
        int timeBeforeNextIfWentDown;

        switch (getter.get()) {
            case -2:
                chanceToGoUp = 70;
                timeBeforeNextIfWentUp = 5;
                timeBeforeNextIfWentDown = 1;
                break;
            case -1:
                chanceToGoUp = 60;
                timeBeforeNextIfWentUp = 5;
                timeBeforeNextIfWentDown = 4;
                break;
            case 0:
                chanceToGoUp = 50;
                timeBeforeNextIfWentUp = 5;
                timeBeforeNextIfWentDown = 5;
                break;
            case 1:
                chanceToGoUp = 40;
                timeBeforeNextIfWentUp = 5;
                timeBeforeNextIfWentDown = 4;
                break;
            case 2:
                chanceToGoUp = 30;
                timeBeforeNextIfWentUp = 1;
                timeBeforeNextIfWentDown = 5;
                break;
            default:
                throw new IllegalStateException();
        }

        int roll = random.nextInt(100);

        if (roll < chanceToGoUp) {
            setter.accept(Math.min(2, getter.get() + 1));
            efficiencies.setTimeToNextChange(timeBeforeNextIfWentUp);
            return;
        }
        setter.accept(Math.max(-2, getter.get() - 1));
        efficiencies.setTimeToNextChange(timeBeforeNextIfWentDown);
    }
}

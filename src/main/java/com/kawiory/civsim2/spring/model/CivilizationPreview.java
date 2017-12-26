package com.kawiory.civsim2.spring.model;

/**
 * @author Kacper
 */

public class CivilizationPreview {

    private final int id;
    private final String name;
    private final int colorID;

    public CivilizationPreview(int id, String name, int colorID) {
        this.id = id;
        this.name = name;
        this.colorID = colorID;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getColorID() {
        return colorID;
    }

}

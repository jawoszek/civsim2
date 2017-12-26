package com.kawiory.civsim2.spring.model;

/**
 * @author Kacper
 */

public class ProvinceProperty {

    private final String name;
    private int value;

    public ProvinceProperty(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

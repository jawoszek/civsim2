package com.kawiory.civsim2.spring.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kacper
 */

public class ProvinceDetails {

    private final List<ProvinceProperty> properties = new ArrayList<>();

    public void addProperty(ProvinceProperty property) {
        properties.add(property);
    }

    public List<ProvinceProperty> getProperties() {
        return properties;
    }
}

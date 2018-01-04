package com.kawiory.civsim2.generator;

public class Presets {
    static GeneratorOptions standard(){
        return new GeneratorOptions();
    }

    public static GeneratorOptions islands(){
        GeneratorOptions o = new GeneratorOptions();
        return new GeneratorOptions();
    }

    public static GeneratorOptions continental(){
        GeneratorOptions o = new GeneratorOptions();
        o.setWater(0.5);
        return new GeneratorOptions();
    }
}

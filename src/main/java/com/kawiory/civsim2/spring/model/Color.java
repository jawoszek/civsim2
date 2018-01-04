package com.kawiory.civsim2.spring.model;

import java.util.Arrays;
import java.util.List;

/**
 * @author Kacper
 */

public class Color {

    public static final List<Integer> colors = Arrays.asList(983270, 65280, 16773146, 8949905, 28672, 9083245,
            0, 13417984, 4222540, 13417984, 16773836, 13430527,
            java.awt.Color.RED.getRGB() & 0xFFFFFF, java.awt.Color.BLUE.getRGB() & 0xFFFFFF,
            java.awt.Color.CYAN.getRGB() & 0xFFFFFF, java.awt.Color.BLACK.getRGB() & 0xFFFFFF,
            java.awt.Color.MAGENTA.getRGB() & 0xFFFFFF, java.awt.Color.GRAY.getRGB() & 0xFFFFFF);

    public static int getIndexOfClosestTerrain(int color) {
        int id = 1;
        int currDist = Integer.MAX_VALUE;
        int redC = (color >> 16) & 0xFF;
        int greenC = (color >> 8) & 0xFF;
        int blueC = (color) & 0xFF;
        for (int i = 1; i <= 11; i++) {
            int redI = (colors.get(i) >> 16) & 0xFF;
            int greenI = (colors.get(i) >> 8) & 0xFF;
            int blueI = (colors.get(i)) & 0xFF;
            int curr = (int) Math.round(Math.sqrt(Math.pow(redC - redI, 2) + Math.pow(greenC - greenI, 2) + Math.pow(blueC - blueI, 2)));
            if (curr < currDist) {
                currDist = curr;
                id = i;
            }
        }
        return id;
    }

    public static int getTerrainFromMode(int color) {
        int id = 0;
        int redC = (color >> 16) & 0xFF;
        int greenC = (color >> 8) & 0xFF;
        int blueC = (color) & 0xFF;
        if (redC > 240 && greenC > 240 && blueC > 240) id = 11;
        else if (redC > 140 && greenC > 145 && blueC > 240) id = 10;
        else if (redC < 20 && greenC < 150 && blueC > 120) id = 4;
        else if (redC < 20 && greenC > 170 && blueC > 70) id = 1;
        else if (redC < 20 && greenC > 240 && blueC < 50) id = 8;
        else if (redC > 220 && greenC > 245 && blueC > 100) id = 5;
        else if (redC > 245 && greenC > 200 && blueC < 20) id = 2;
        else if (redC > 245 && greenC > 140 && blueC < 35) id = 1;
        return id;
    }

    public static final List<String> terrains = Arrays.asList("Water", "Grassland", "Desert", "Mountain", "Forest",
            "Steppe", "Peak", "Arid land", "Jungle", "Swamp", "Tundra", "Arctic");

    public static String colorToString(int color) {
        return "#" + String.format("%06X", color);
    }
}

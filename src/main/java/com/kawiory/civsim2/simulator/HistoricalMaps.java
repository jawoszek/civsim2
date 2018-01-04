package com.kawiory.civsim2.simulator;

import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kacper
 */
public enum HistoricalMaps {
    WHOLE_MODE("world_mode.jpg"), SMALL_MODE("world_mode_small.jpg"), EUROPE_MODE("europe_mode.jpg"), NEAR_EAST_MODE("near_east_mode.jpg");

    String mapName;

    HistoricalMaps(String mapName) {
        this.mapName = mapName;
    }

    public String getMapName() {
        return mapName;
    }

    public Map<String, Tuple2<Coordinates, Integer>> getStarts() {
        Map<String, Tuple2<Coordinates, Integer>> map = new HashMap<>();
        switch (this) {
            case WHOLE_MODE:
                map.put("Roman Empire", Tuple.of(new Coordinates(651, 243), 0));
                map.put("Egypt", Tuple.of(new Coordinates(711, 298), -1));
                map.put("Greece", Tuple.of(new Coordinates(684, 260), 1));
                map.put("Ottoman Empire", Tuple.of(new Coordinates(701, 255), 0));
                map.put("Incas Empire", Tuple.of(new Coordinates(330, 438), -1));
                map.put("Babylon", Tuple.of(new Coordinates(768, 288), -1));
                map.put("China", Tuple.of(new Coordinates(1021, 297), -1));
                map.put("India", Tuple.of(new Coordinates(880, 355), 0));
                map.put("Mongols", Tuple.of(new Coordinates(981, 168), -1));
                map.put("Sweden", Tuple.of(new Coordinates(663, 159), 1));
                map.put("England", Tuple.of(new Coordinates(601, 196), 0));
                map.put("Huron", Tuple.of(new Coordinates(315, 221), -1));
                map.put("Aztec Empire", Tuple.of(new Coordinates(253, 337), -1));
                break;
            case SMALL_MODE:
                map.put("Roman Empire", Tuple.of(new Coordinates(161, 58), 0));
                map.put("Egypt", Tuple.of(new Coordinates(177, 76), -1));
                map.put("Greece", Tuple.of(new Coordinates(170, 63), 1));
                map.put("Ottoman Empire", Tuple.of(new Coordinates(178, 63), 0));
                map.put("Incas Empire", Tuple.of(new Coordinates(87, 110), -1));
                map.put("Babylon", Tuple.of(new Coordinates(191, 73), -1));
                map.put("China", Tuple.of(new Coordinates(255, 72), -1));
                map.put("India", Tuple.of(new Coordinates(219, 85), 0));
                map.put("Mongols", Tuple.of(new Coordinates(246, 45), -1));
                map.put("Sweden", Tuple.of(new Coordinates(163, 39), 1));
                map.put("England", Tuple.of(new Coordinates(149, 48), 0));
                map.put("Huron", Tuple.of(new Coordinates(82, 57), -1));
                map.put("Aztec Empire", Tuple.of(new Coordinates(64, 85), -1));
                break;
            case EUROPE_MODE:
                map.put("Roman Empire", Tuple.of(new Coordinates(136, 163), 0));
                map.put("Denmark", Tuple.of(new Coordinates(123, 96), 1));
                map.put("Ottoman Empire", Tuple.of(new Coordinates(197, 170), 0));
                map.put("Russia", Tuple.of(new Coordinates(220, 100), -1));
                map.put("Spain", Tuple.of(new Coordinates(74, 174), 0));
                map.put("Egypt", Tuple.of(new Coordinates(195, 218), -1));
                map.put("England", Tuple.of(new Coordinates(88, 118), 0));
                break;
            case NEAR_EAST_MODE:
                map.put("Egypt", Tuple.of(new Coordinates(42, 67), -1));
                map.put("Israel", Tuple.of(new Coordinates(62, 56), 0));
                map.put("Persia", Tuple.of(new Coordinates(142, 53), 0));
                map.put("Babylon", Tuple.of(new Coordinates(97, 60), -1));
                map.put("Greece", Tuple.of(new Coordinates(15, 28), 1));
                map.put("Macedonia", Tuple.of(new Coordinates(19, 13), 0));
                break;
        }
        return map;
    }
}

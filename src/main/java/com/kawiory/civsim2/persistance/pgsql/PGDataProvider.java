package com.kawiory.civsim2.persistance.pgsql;

import com.kawiory.civsim2.persistance.DataProvider;
import com.kawiory.civsim2.simulator.Coordinates;
import com.kawiory.civsim2.simulator.Province;
import com.kawiory.civsim2.simulator.Simulation;
import com.kawiory.civsim2.spring.model.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * @author Kacper
 */

public class PGDataProvider implements DataProvider {

    private final DataSource dataSource;

    public PGDataProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<WorldPreview> getWorldPreviews() {
        Connection connection = null;
        List<WorldPreview> list = new ArrayList<>();
        try {
            connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rS = stmt.executeQuery("SELECT worldid,world.name,sizex,sizey,COUNT(simid) FROM civsim2.world LEFT OUTER JOIN civsim2.simulation USING (worldid) GROUP BY (worldid);");
            while (rS.next()) {
                WorldPreview world = new WorldPreview(rS.getInt(1), rS.getString(2), rS.getInt(5), rS.getInt(3), rS.getInt(4));
                list.add(world);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    @Override
    public int getWorldsCount() {
        Connection connection = null;
        int count = 0;
        try {
            connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rS = stmt.executeQuery("SELECT count(*) FROM civsim2.world;");
            if (rS.next()) {
                count = rS.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return count;
    }

    @Override
    public WorldDetails getWorldDetails(int id) {
        Connection connection = null;
        WorldDetails wD = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT world.worldid,world.name,sizex,sizey,count(simid) FROM civsim2.world LEFT OUTER JOIN civsim2.simulation ON (civsim2.world.worldid = civsim2.simulation.worldid) WHERE world.worldid=? GROUP BY (world.worldid);");
            stmt.setInt(1, id);
            ResultSet rS = stmt.executeQuery();
            if (rS.next()) {
                int[][] tab = new int[rS.getInt(3)][];
                for (int i = 0; i < rS.getInt(3); i++) {
                    tab[i] = new int[rS.getInt(4)];
                }
                ResultSet rS2 = connection.createStatement().executeQuery("SELECT x,y,terrain FROM civsim2.field WHERE worldid = " + rS.getInt(1) + ";");
                while (rS2.next()) {
                    tab[rS2.getInt(1)][rS2.getInt(2)] = rS2.getInt(3) - 1;
                }
                rS2.close();
                rS2 = connection.createStatement().executeQuery("SELECT simid,simulation.name,maxframe,count(frameid) FROM civsim2.simulation JOIN civsim2.frame USING (simid) WHERE worldid=" + rS.getInt(1) + " GROUP BY (simid);");
                List<SimulationPreview> list = new ArrayList<>();
                while (rS2.next()) {
                    list.add(new SimulationPreview(rS2.getInt(1), rS2.getString(2), rS.getInt(1), rS.getString(2), rS2.getInt(3) <= rS2.getInt(4), rS2.getInt(4), rS2.getInt(3)));
                }
                rS2.close();
                wD = new WorldDetails(rS.getInt(1), rS.getString(2), rS.getInt(5), rS.getInt(3), rS.getInt(4), tab, list);
                rS.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return wD;
    }

    @Override
    public int[][] getWorldMap(int id) {
        Connection connection = null;
        int[][] tab = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT sizex,sizey FROM civsim2.world WHERE worldid=?;");
            stmt.setInt(1, id);
            ResultSet rS = stmt.executeQuery();
            if (rS.next()) {
                tab = new int[rS.getInt(1)][];
                for (int i = 0; i < rS.getInt(1); i++) {
                    tab[i] = new int[rS.getInt(2)];
                }
                ResultSet rS2 = connection.createStatement().executeQuery("SELECT x,y,terrain FROM civsim2.field WHERE worldid = " + id + ";");
                while (rS2.next()) {
                    tab[rS2.getInt(1)][rS2.getInt(2)] = rS2.getInt(3) - 1;
                }
                rS2.close();
            }
            rS.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tab;
    }

    @Override
    public Map<Integer, String> getColors() {
        Connection connection = null;
        Map<Integer, String> result = new HashMap<>();
        try {
            connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rS = stmt.executeQuery("SELECT colorid, color FROM civsim2.color;");
            while (rS.next()) {
                result.put(rS.getInt(1) - 1, rS.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public SimulationsList getSimulationsList() {
        Connection connection = null;
        SimulationsList list = new SimulationsList();
        try {
            connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rS = stmt.executeQuery("SELECT simid,civsim2.simulation.name,worldid,civsim2.world.name,maxframe,count(frameid) FROM (civsim2.simulation JOIN civsim2.world USING (worldid)) LEFT OUTER JOIN civsim2.frame USING (simid) GROUP BY (simid, civsim2.world.worldid);");
            while (rS.next()) {
                int simID = rS.getInt(1);
                String simName = rS.getString(2);
                int worldID = rS.getInt(3);
                String worldName = rS.getString(4);
                int maxFrame = rS.getInt(5);
                int count = rS.getInt(6);
                if (count >= maxFrame) {
                    list.addDone(new SimulationPreview(simID, simName, worldID, worldName, true, count, maxFrame));
                } else {
                    list.addInProgress(new SimulationPreview(simID, simName, worldID, worldName, false, count, maxFrame));
                }
            }
            rS.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    public SimulationDetails getSimulationDetails(int id) {
        SimulationDetails out = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement pS = connection.prepareStatement("SELECT civsim2.simulation.simid,civsim2.simulation.name,worldid,civsim2.world.name,maxframe,count(frameid) FROM (civsim2.simulation JOIN civsim2.world USING (worldid)) JOIN civsim2.frame ON (civsim2.simulation.simid=civsim2.frame.simid) WHERE civsim2.simulation.simid=? GROUP BY (frame.simid, civsim2.simulation.simid, civsim2.world.worldid);");
            pS.setInt(1, id);
            ResultSet rS = pS.executeQuery();
            if (rS.next()) {
                int simID = rS.getInt(1);
                String simName = rS.getString(2);
                int worldID = rS.getInt(3);
                String worldName = rS.getString(4);
                int maxFrame = rS.getInt(5);
                int count = rS.getInt(6);
                out = new SimulationDetails(new SimulationPreview(simID, simName, worldID, worldName, count >= maxFrame, count, maxFrame));
            }
            rS.close();
            pS.close();
            return out;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return out;
    }

    @Override
    public SimulationFrame getSimulationFrame(int simID, int frameNumber) {
        SimulationFrame out = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement pS = connection.prepareStatement("SELECT maxframe,frameid FROM civsim2.simulation JOIN civsim2.frame USING (simid) WHERE simulation.simid=? AND framenumber=?;");
            pS.setInt(1, simID);
            pS.setInt(2, frameNumber);
            ResultSet rS = pS.executeQuery();
            if (rS.next()) {
                int frameID = rS.getInt(2);
                out = new SimulationFrame(simID, frameID, frameNumber, rS.getInt(1));
                PreparedStatement pS2 = connection.prepareStatement("SELECT DISTINCT civid,name,colorid FROM civsim2.civ JOIN civsim2.province USING (civid) WHERE frameid =?;");
                pS2.setInt(1, frameID);
                ResultSet rS2 = pS2.executeQuery();
                while (rS2.next()) {
                    CivilizationPreview curr = new CivilizationPreview(rS2.getInt(1), rS2.getString(2), rS2.getInt(3));
                    out.addCivilization(curr);
                }
                rS2.close();
                pS2.close();
                pS2 = connection.prepareStatement("SELECT provid,x,y,civid FROM civsim2.province WHERE frameid=?;");
                pS2.setInt(1, frameID);
                rS2 = pS2.executeQuery();
                while (rS2.next()) {
                    out.addProvince(new int[]{rS2.getInt(2), rS2.getInt(3), rS2.getInt(4)});
                }
                rS2.close();
                pS2.close();
            }
            rS.close();
            pS.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return out;
    }

    @Override
    public ArrayList<SimulationFrame> getSimulationFrames(int simID) {
        ArrayList<SimulationFrame> list = new ArrayList<>();
        SimulationFrame out;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement pS = connection.prepareStatement("SELECT maxframe,frameid,framenumber FROM civsim2.simulation JOIN civsim2.frame USING (simid) WHERE simulation.simid=?;");
            PreparedStatement pS2 = connection.prepareStatement("SELECT DISTINCT civid,name,colorid FROM civsim2.civ JOIN civsim2.province USING (civid) WHERE frameid =?;");
            PreparedStatement pS3 = connection.prepareStatement("SELECT provid,x,y,civid FROM civsim2.province WHERE frameid=?;");
            pS.setInt(1, simID);
            ResultSet rS = pS.executeQuery();
            while (rS.next()) {
                int maxFrame = rS.getInt(1);
                int frameID = rS.getInt(2);
                int frameNumber = rS.getInt(3);
                out = new SimulationFrame(simID, frameID, frameNumber, maxFrame);
                pS2.setInt(1, frameID);
                ResultSet rS2 = pS2.executeQuery();
                while (rS2.next()) {
                    CivilizationPreview curr = new CivilizationPreview(rS2.getInt(1), rS2.getString(2), rS2.getInt(3));
                    out.addCivilization(curr);
                }
                rS2.close();
                pS3.setInt(1, frameID);
                rS2 = pS3.executeQuery();
                while (rS2.next()) {
                    out.addProvince(new int[]{rS2.getInt(2), rS2.getInt(3), rS2.getInt(4)});
                }
                rS2.close();
                list.add(out);
            }
            rS.close();
            pS.close();
            pS2.close();
            pS3.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    public ProvinceDetails getProvinceDetails(int frameID, int x, int y) {
        ProvinceDetails out = new ProvinceDetails();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement pS = connection.prepareStatement("SELECT parameter.name,civsim2.value.value FROM civsim2.province JOIN civsim2.value USING (provid) JOIN civsim2.parameter USING (paraid) WHERE frameid=? AND x=? AND y=?");
            pS.setInt(1, frameID);
            pS.setInt(2, x);
            pS.setInt(3, y);
            ResultSet rS = pS.executeQuery();
            while (rS.next()) {
                out.addProperty(new ProvinceProperty(rS.getString(1), rS.getInt(2)));
            }
            rS.close();
            pS.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return out;
    }

    @Override
    public synchronized int insertSimulation(Simulation simulation) {
        int id = 0;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement pS = connection.prepareStatement("INSERT INTO civsim2.simulation (worldid,maxframe,name) VALUES (?,?,?) RETURNING simid;");
            pS.setInt(1, simulation.getMapID());
            pS.setInt(2, simulation.getMaxFrame());
            pS.setString(3, simulation.getName());
            ResultSet rS = pS.executeQuery();
            if (rS.next()) {
                id = rS.getInt(1);
            }
            rS.close();
            pS.close();
            if (!simulation.isNameChosen()) {
                pS = connection.prepareStatement("UPDATE civsim2.simulation SET name = ? WHERE simid = ?;");
                pS.setString(1, "id:" + id);
                pS.setInt(2, id);
                pS.executeUpdate();
                pS.close();
                simulation.setName("id:" + id);
            }
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    @Override
    public Map<Integer, Map<String, String>> terrainProperty() {
        Map<Integer, Map<String, String>> map = new HashMap<>();
        Map<String, String> tempMap = new HashMap<>();
        tempMap.put("isScalar", "false");
        tempMap.put("color", "#0F00E6");
        tempMap.put("name", "water");
        map.put(0, tempMap);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement pS = connection.prepareStatement("SELECT terrainid,name,color FROM civsim2.terrain JOIN civsim2.color ON (terrainid = civsim2.color.colorid);");
            ResultSet rS = pS.executeQuery();
            while (rS.next()) {
                tempMap = new HashMap<>();
                tempMap.put("color", rS.getString(3));
                tempMap.put("name", rS.getString(2));
                map.put(rS.getInt(1) - 1, tempMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    @Override
    public Map<Integer, Map<String, String>> property(String name) {
        Map<Integer, Map<String, String>> map = null;
        if (name.equals("terrain")) {
            map = terrainProperty();
        } else if (name.equals("population")) {
            map = new HashMap<>();
            Map<String, String> tempMap = new HashMap<>();
            tempMap.put("isScalar", "true");
            map.put(0, tempMap);
        }
        return map;
    }


    @Override
    public Map<String, List<String>> properties(int id) {
        Map<String, List<String>> map = new HashMap<>();
        List<String> list = Arrays.asList("color", "name");
        map.put("terrain", list);
        list = Collections.singletonList("value");
        map.put("population", list);
        return map;
    }

    @Override
    public String getMapName(int id) {
        Connection connection = null;
        String name = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement pS = connection.prepareStatement("SELECT name FROM civsim2.world WHERE worldid=?");
            pS.setInt(1, id);
            ResultSet rS = pS.executeQuery();
            if (rS.next()) {
                name = rS.getString(1);
            } else {
                name = "N/A";
            }
            rS.close();
            pS.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return name;
    }

    @Override
    public synchronized int insertCiv(int simid, String name, int colorID) {
        Connection connection = null;
        int assignedID = 0;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement pS = connection.prepareStatement("INSERT INTO civsim2.civ (simid,name,colorid) VALUES (?,?,?) RETURNING civid;");
            pS.setInt(1, simid);
            pS.setString(2, name);
            pS.setInt(3, colorID);
            ResultSet rS = pS.executeQuery();
            if (rS.next()) {
                assignedID = rS.getInt(1);
            }
            rS.close();
            pS.close();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return assignedID;
    }

    public synchronized int getRandomColor() {
        int color = 0;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            Random r = new Random();
            String colorS = Color.colorToString(r.nextInt(16777216));
            PreparedStatement pS = connection.prepareStatement("SELECT colorid FROM civsim2.color WHERE color = ?;");
            pS.setString(1, colorS);
            ResultSet rS = pS.executeQuery();
            if (rS.next()) {
                color = rS.getInt(1) - 1;
            } else {
                PreparedStatement pS2 = connection.prepareStatement("INSERT INTO civsim2.color (color) VALUES (?) RETURNING colorid;");
                pS2.setString(1, colorS);
                ResultSet rS2 = pS2.executeQuery();
                if (rS2.next()) {
                    color = rS2.getInt(1) - 1;
                }
                rS2.close();
                pS2.close();
            }
            rS.close();
            pS.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return color;
    }

    public synchronized int insertFrame(int number, int simid, Map<Coordinates, Province> map) {
        int id = 0;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement pS = connection.prepareStatement("INSERT INTO civsim2.frame (simid,framenumber) VALUES (?,?) RETURNING frameid;");
            pS.setInt(1, simid);
            pS.setInt(2, number);
            ResultSet rS = pS.executeQuery();
            if (rS.next()) {
                id = rS.getInt(1);
                pS = connection.prepareStatement("INSERT INTO civsim2.province (frameid,x,y,civid) VALUES (?,?,?,?) RETURNING provid;");
                PreparedStatement pS2 = connection.prepareStatement("INSERT INTO civsim2.value (paraid,provid,value) VALUES (?,?,?);");
                pS.setInt(1, id);
                pS2.setInt(1, 1);
                for (Map.Entry<Coordinates, Province> entry : map.entrySet()) {
                    Coordinates coordinates = entry.getKey();
                    Province province = entry.getValue();
                    pS.setInt(2, coordinates.getX());
                    pS.setInt(3, coordinates.getY());
                    pS.setInt(4, province.getCivilization().getId());
                    ResultSet rS2 = pS.executeQuery();
                    if (rS2.next()) {
                        int provID = rS2.getInt(1);
                        pS2.setInt(2, provID);
                        pS2.setInt(3, province.getPopulation());
                        pS2.executeUpdate();
                    }
                    rS2.close();
                }
            }
            pS.close();
            rS.close();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    public synchronized int insertMap(String name, int[][] map) {
        int sizeX = map.length;
        int sizeY = 0;
        if (sizeX > 0) {
            sizeY = map[0].length;
        }
        int id = 0;
        if (sizeX == 0 || sizeY == 0) return id;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement pS = connection.prepareStatement("INSERT INTO civsim2.world (name,sizex,sizey) VALUES (?,?,?);");
            if (name == null) pS.setString(1, "size:" + sizeX + "x" + sizeY);
            else pS.setString(1, name);
            pS.setInt(2, sizeX);
            pS.setInt(3, sizeY);
            ResultSet rS = pS.executeQuery();
            if (rS.next()) {
                id = rS.getInt(1);
            }
            rS.close();
            pS.close();
            pS = connection.prepareStatement("INSERT INTO civsim2.field (x,y,worldid,terrain) VALUES (?,?,?,?);");
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    int type = map[i][j];
                    pS.setInt(1, i);
                    pS.setInt(2, j);
                    pS.setInt(3, id);
                    pS.setInt(4, type + 1);
                    pS.addBatch();
                }
            }
            pS.executeBatch();
            pS.close();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    @Override
    public Map<List<Integer>, String> getPropertyForFrame(int id, String name) {
        Map<List<Integer>, String> map = new HashMap<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement pS = connection.prepareStatement("SELECT x,y,value FROM civsim2.province NATURAL JOIN civsim2.value NATURAL JOIN civsim2.parameter WHERE frameid=? AND name=?;");
            pS.setInt(1, id);
            pS.setString(2, name);
            ResultSet rS = pS.executeQuery();
            while (rS.next()) {
                List<Integer> list = new ArrayList<>();
                list.add(rS.getInt(1));
                list.add(rS.getInt(2));
                map.put(list, Integer.toString(rS.getInt(3)));
            }
            rS.close();
            pS.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    @Override
    public synchronized void deleteSimulation(int simID) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement pS1 = connection.prepareStatement("DELETE FROM civsim2.value WHERE provid IN (SELECT provid FROM civsim2.province NATURAL JOIN civsim2.frame NATURAL JOIN civsim2.simulation WHERE simid = ?);");
            PreparedStatement pS2 = connection.prepareStatement("DELETE FROM civsim2.province WHERE frameid IN (SELECT frameid FROM civsim2.frame NATURAL JOIN civsim2.simulation WHERE simid = ?);");
            PreparedStatement pS3 = connection.prepareStatement("DELETE FROM civsim2.frame WHERE simid = ?;");
            PreparedStatement pS4 = connection.prepareStatement("DELETE FROM civsim2.civ WHERE simid = ?;");
            PreparedStatement pS5 = connection.prepareStatement("DELETE FROM civsim2.simulation WHERE simid =?;");
            pS1.setInt(1, simID);
            pS2.setInt(1, simID);
            pS3.setInt(1, simID);
            pS4.setInt(1, simID);
            pS5.setInt(1, simID);
            pS1.executeUpdate();
            pS2.executeUpdate();
            pS3.executeUpdate();
            pS4.executeUpdate();
            pS5.executeUpdate();
            pS1.close();
            pS2.close();
            pS3.close();
            pS4.close();
            pS5.close();
            connection.commit();
            connection.setAutoCommit(true);
            connection.createStatement().executeUpdate("VACUUM");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

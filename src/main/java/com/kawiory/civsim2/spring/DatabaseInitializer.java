package com.kawiory.civsim2.spring;

import com.kawiory.civsim2.spring.model.Color;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Kacper
 */

@Component
public class DatabaseInitializer implements InitializingBean {

    private final static String RESOURCES_PATH = "src/main/resources/data/";

    private final DataSource dataSource;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        Connection connection = dataSource.getConnection();
//        connection.setAutoCommit(false);
//        insertParameters(connection);
//        insertColorsAndTerrains(connection);
//        connection.commit();
//        insertMapsFromPreImgs(connection);
//        connection.commit();
//        connection.close();
    }

    private void insertParameters(Connection conn) throws SQLException {
        try (PreparedStatement pS = conn.prepareStatement("INSERT INTO civsim2.parameter (paraid,name) VALUES (?,?);")) {
            pS.setInt(1, 1);
            pS.setString(2, "population");
            pS.executeUpdate();
        }
    }

    private void insertColorsAndTerrains(Connection conn) throws SQLException {
        PreparedStatement pS = conn.prepareStatement("INSERT INTO civsim2.color (color) VALUES (?);");
        PreparedStatement pS2 = conn.prepareStatement("INSERT INTO civsim2.terrain (name) VALUES (?);");
        for (int i = 0; i < Color.colors.size(); i++) {
            System.out.println("COLOR");
            pS.setString(1, Color.colorToString(Color.colors.get(i)));
            pS.executeUpdate();
            if (Color.terrains.size() > i) {
                pS2.setString(1, Color.terrains.get(i));
                pS2.executeUpdate();
            }
        }
        pS.close();
        pS2.close();
    }

    private void insertMapsFromPreImgs(Connection conn) {
        File data = new File(RESOURCES_PATH);
        try {
            PreparedStatement pS = conn.prepareStatement("INSERT INTO civsim2.world (name,sizex,sizey) VALUES (?,?,?) RETURNING worldid;");
            PreparedStatement pS2 = conn.prepareStatement("INSERT INTO civsim2.field (x,y,worldid,terrain) VALUES (?,?,?,?);");
            for (File x : data.listFiles((File y) -> y.getName().endsWith(".jpg"))) {
                System.out.println("FILE");
                try {
                    BufferedImage img = ImageIO.read(x);
                    pS.setString(1,x.getName());
                    pS.setInt(2,img.getWidth());
                    pS.setInt(3,img.getHeight());
                    ResultSet rS = pS.executeQuery();
                    int id = 1;
                    if (rS.next()){
                        id = rS.getInt(1);
                    }
                    rS.close();
                    for (int i = 0; i < img.getWidth(); i+=1) {
                        for (int j = 0; j < img.getHeight(); j+=1) {
                            int pixel = img.getRGB(i, j);
                            int red = (pixel >> 16) & 0xFF;
                            int green = (pixel >>8 ) & 0xFF;
                            int blue = (pixel) & 0xFF;
                            int terrain;
                            if (!x.getName().contains("mode")) terrain = red<20?0:Color.getIndexOfClosestTerrain(pixel);
                            else {
                                terrain = Color.getTerrainFromMode(pixel);
                                if (terrain == 0){
                                    int up = 0;
                                    int down = 0;
                                    int right = 0;
                                    int left = 0;
                                    if (i>0&&j>0) down = Color.getTerrainFromMode(img.getRGB(i-1, j-1));
                                    if (i<img.getWidth()-1&&j<img.getHeight()-1) up = Color.getTerrainFromMode(img.getRGB(i+1, j+1));
                                    if (i<img.getWidth()-1&&j>0) left = Color.getTerrainFromMode(img.getRGB(i+1, j-1));
                                    if (i>0&&j<img.getHeight()-1) right = Color.getTerrainFromMode(img.getRGB(i-1, j+1));
                                    if (down!=0 && up!=0){
                                        terrain = up;
                                    }else if(right!=0 && left!=0){
                                        terrain = right;
                                    }
                                }
                            }
                            pS2.setInt(1,i);
                            pS2.setInt(2,j);
                            pS2.setInt(3,id);
                            pS2.setInt(4,terrain + 1);
                            pS2.addBatch();
                        }
                    }
                    pS2.executeBatch();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            pS.close();
            pS2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

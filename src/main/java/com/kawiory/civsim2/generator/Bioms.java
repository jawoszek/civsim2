package com.kawiory.civsim2.generator;

import java.util.Random;

public class Bioms extends Grid{

    private double[] biomsTresh    = { 0,0.25,0.3,0.4 ,0.4 ,0.6,0.6,0.8 , 1 };
    private double[] biomsStretch  = {0.15,0.15,0.15,0.2,0.1,0.3,0.3,0.15,0.1};
    private double[] biomsSize = {0.4, 0.5, 0.5, 0.4, 0.1, 0.5, 0.6, 0.6, 1};
    private int numOfBioms = biomsSize.length;

    public Bioms (int sizeX, int sizeY) {super(sizeX,sizeY);}

    public Bioms (Integer[][] grid){ super(grid); }

    public void generateBiom(int x, int y, Terrain terrain){
        Random rand = new Random();
        if(terrain.get(x,y) == 0) return;
        double latitude = Math.abs(0.5 - (double)y/terrain.sizeY())*2;
        double best_disp = Double.MAX_VALUE;
        int best_biom = 0;
        for(int k = 0 ; k < numOfBioms ; k++){
            double displacement = Math.abs(latitude - biomsTresh[k]);
            if(k==5 && Tools.neighbors(terrain.toIntegerArray(),x,y).length<4) displacement -= 1;
            displacement = displacement - rand.nextDouble()*0.2 - biomsStretch[k];
            if(displacement < best_disp){
                best_disp = displacement;
                best_biom = k;
            }
        }
        grid[x][y] =(best_biom+1)*15;
    }

    public Bioms biomsSeed(Terrain terrain){
        Bioms bioms = new Bioms(terrain.sizeX(), terrain.sizeY());
        for(int i = 0 ; i < bioms.sizeX() ; i++)
            for(int j = 0 ; j < bioms.sizeY() ; j++)
                bioms.generateBiom(i,j,terrain);
        return bioms;
    }

    public Bioms enlarge(int sizeX, int sizeY, Terrain terrain){
        Bioms result = new Bioms(Tools.nearestInterpolation(this.toIntegerArray(),sizeX,sizeY));
        Random rand = new Random();
        for(int i = 0 ; i < sizeX ; i++){
            for(int j = 0 ; j < sizeY ; j++) {
                if (terrain.get(i, j) == 0) {
                    result.set(i, j, 0);
                    continue;
                }
                if (terrain.get(i, j) == 255 && result.get(i, j) == 0){
                    result.generateBiom(i, j, terrain);
                    continue;
                }
                Integer[] neighbors = Tools.neighbors(result.toIntegerArray(),i,j);
                int currentBiom = result.get(i,j)/15 - 1;
                double score = biomsSize[currentBiom] + rand.nextDouble()*2;
                result.set(i,j,(currentBiom+1)*15);
                for(Integer k : neighbors){
                    if(k==0) continue;
                    int neighBiom = k/15 - 1;
                    double score2 = biomsSize[neighBiom] + rand.nextDouble()*2;
                    if(score2 > score) {
                        result.set(i, j, (neighBiom+1) * 15);
                        score = score2;
                    }
                }
            }
        }

        return result;
    }
}

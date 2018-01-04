package com.kawiory.civsim2.generator;

import java.util.Random;

public class Terrain extends  Grid{

    public Terrain (int sizeX, int sizeY) {super(sizeX,sizeY);}

    public Terrain (Integer[][] grid){ super(grid); }

    static public Terrain generateSeed(int sizeX,int sizeY,double water){
        Terrain result = new Terrain(sizeX,sizeY);
        result.fillWithZeros();
        Double chance;
        int mini,minj,maxi,maxj;
        for( int i=1 ; i< sizeX-1 ; i++){
            for( int j=1 ; j< sizeY-1 ; j++){
                Random rand = new Random();
                chance = rand.nextDouble();
                if(chance>water) {
                    result.set(i, j, 255);
                    mini = (i==1) ? 1 : i-1;
                    minj = (j==1) ? 1 : j-1;
                    maxi = (i+1==sizeX-1)? sizeX-2 : i+1;
                    maxj = (j+1==sizeY-1)? sizeY-2 : j+1;
                    result.set(mini,j   , rand.nextDouble() < 0.9 ? 255:0); // N
                    result.set(mini,maxj, rand.nextDouble() < 0.4 ? 255:0); // NE
                    result.set(i   ,maxj, rand.nextDouble() < 0.2 ? 255:0); // E
                    result.set(maxi,maxj, rand.nextDouble() < 0.4 ? 255:0); // SE
                    result.set(maxi,j   , rand.nextDouble() < 0.9 ? 255:0); // S
                    result.set(maxi,minj, rand.nextDouble() < 0.4 ? 255:0); // SW
                    result.set(i   ,minj, rand.nextDouble() < 0.2 ? 255:0); // W
                    result.set(mini,minj, rand.nextDouble() < 0.4 ? 255:0); // NW
                }
            }
        }
        return result;
    }
    public Terrain enlarge(int _sizeX, int _sizeY, double randomFactor){
        Integer[][] largerGrid = Tools.bilinearInterpolation(grid,_sizeX,_sizeY);
        Random rand = new Random();
        for(int i = 0; i<largerGrid.length; i++)
            for(int j = 0; j<largerGrid[0].length; j++) {
                if (i == 0 || j == 0 || i == largerGrid.length - 1 || j == largerGrid.length - 1)
                    largerGrid[i][j] = 0;
                else
                    largerGrid[i][j] = largerGrid[i][j] + (int) ((rand.nextDouble()*2-1) * randomFactor);
            }

        return new Terrain(Tools.binarize(128,largerGrid));
    }
}

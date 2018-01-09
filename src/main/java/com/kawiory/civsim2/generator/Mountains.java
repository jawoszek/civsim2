package com.kawiory.civsim2.generator;

public class Mountains extends Grid{

    public Mountains (int sizeX, int sizeY) {super(sizeX,sizeY);}

    public Mountains (Integer[][] grid){ super(grid); }

    public Mountains enlarge(int sizeX, int sizeY){
        Mountains result = new Mountains(sizeX,sizeY);
        return result;
    }
}

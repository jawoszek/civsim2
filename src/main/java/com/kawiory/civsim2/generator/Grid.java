package com.kawiory.civsim2.generator;

import java.util.Arrays;

public class Grid {
    protected Integer[][] grid;

    public Grid(int sizeX, int sizeY) {
        grid = new Integer[sizeX][sizeY];
        fillWithZeros();
    }

    public Grid(Integer[][] _grid){ grid = _grid; }

    public int sizeX() {
        return grid.length;
    }

    public int sizeY() {
        return grid[0].length;
    }

    public Integer get(int x, int y) {
        return grid[x][y];
    }

    public void set(int x, int y, int val) {
        grid[x][y] = val;
    }

    public Integer[] getNeighbors(int x, int y) {
        Integer[] neighbors = new Integer[8];
        return neighbors;
    }

    public void fillWithZeros() {
        for(int i = 0 ; i < grid.length ; i++){
            Arrays.fill(grid[i],0);
        }
    }

    public Integer[][] toIntegerArray() {
        final Integer[][] result = new Integer[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            result[i] = Arrays.copyOf(grid[i], grid[i].length);
        }
        return result;
    }

    public static void main(String args[]){
        Grid grid = new Grid(10,10);
        for(int i = 0; i<grid.sizeX() ; i++){
            for(int j = 0; j<grid.sizeY() ; j++){
                System.out.print(grid.get(i,j));
            }
            System.out.println();
        }
    }
}
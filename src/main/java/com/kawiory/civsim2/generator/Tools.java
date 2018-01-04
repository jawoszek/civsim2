package com.kawiory.civsim2.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tools {

    private static Map<Integer,Integer> indexTrans = new HashMap<>();

    static{
        indexTrans.put(0, 0);
        indexTrans.put(1, 2);
        indexTrans.put(2, 7);
        indexTrans.put(3, 8);
        indexTrans.put(4, 5);
        indexTrans.put(5, 9);
        indexTrans.put(6, 1);
        indexTrans.put(7, 4);
        indexTrans.put(8, 10);
        indexTrans.put(9, 11);
    }


    public static Integer[][] nearestInterpolation(Integer[][] matrix, int outWidth, int outHeight){
        Integer[][] out = new Integer[outWidth][outHeight];
        double xStep = (double)matrix.length/outWidth;
        double yStep = (double)matrix[0].length/outHeight;
        for (int i = 0; i < outWidth; i++) {
            for (int j = 0; j < outHeight; j++) {
                int x = (int)Math.floor((xStep*i));
                int y = (int)Math.floor((yStep*j));
                out[i][j] = matrix[x][y];
            }
        }
        return out;
    }

    public static Integer[][] bilinearInterpolation(Integer[][] matrix, int outWidth, int outHeight){
        int A, B, C, D, x, y, gray ;
        int inWidth = matrix.length;
        int inHeight = matrix[0].length;

        float x_ratio = ((float)inWidth/outWidth);
        float y_ratio = ((float)inHeight/outHeight);
        float x_diff, y_diff;
        Integer resultMatrix[][] = new Integer[outWidth][outHeight];
        for (int i=0;i<outWidth;i++) {
            for (int j=0;j<outHeight;j++) {
                x = (int)(x_ratio * i) ;
                y = (int)(y_ratio * j) ;
                x_diff = (x_ratio * i) - x ;
                y_diff = (y_ratio * j) - y ;
                int x1 = (x+1)>=inWidth ? x : x+1;
                int y1 = (y+1)>=inHeight ? y : y+1;
                A = matrix[x][y] & 0xff ;
                B = matrix[x1][y] & 0xff ;
                C = matrix[x][y1] & 0xff ;
                D = matrix[x1][y1] & 0xff ;
                gray = (int)(
                        A*(1-x_diff)*(1-y_diff) +  B*(x_diff)*(1-y_diff) +
                                C*(y_diff)*(1-x_diff)   +  D*(x_diff*y_diff)
                ) ;
                resultMatrix[i][j] = gray;
            }
        }
        return resultMatrix ;
    }

    public static Integer[][] binarize(int threshold, Integer[][] grid){
        Integer[][] outGrid = new Integer[grid.length][grid[0].length];
        for(int i = 0; i<grid.length; i++)
            for(int j = 0; j<grid[0].length; j++)
                outGrid[i][j] = grid[i][j] >= threshold ? 255 : 0;
        return outGrid;
    }

    public static Integer[] neighbors(Integer[][] matrix, int x, int y){
        ArrayList<Integer> neighbors = new ArrayList<>();
        if(x>=1)neighbors.add(matrix[x-1][y]);
        if(y>=1)neighbors.add(matrix[x][y-1]);
        if(x<(matrix.length-1))neighbors.add(matrix[x+1][y]);
        if(y<(matrix[0].length-1))neighbors.add(matrix[x][y+1]);

        Integer res[] = new Integer[neighbors.size()];
        return neighbors.toArray(res);
    }

    public static int[][] arrayToFields(Integer[][] array){
        int sizeX = array.length;
        int sizeY = 0;
        if (sizeX>0) sizeY = array[0].length;
        int[][] map = null;
        if (sizeX>0 && sizeY>0){
            map = new int[sizeX][];
            for(int i=0;i<sizeX;i++){
                map[i] = new int[sizeY];
            }
            for(int i=0;i<sizeX;i++){
                for(int j=0;j<sizeY;j++){
                    if (array[i][j]>0) map[i][j] = indexTrans.get(array[i][j]/15);
                    else map[i][j] = 0;
                }
            }
        }
        return map;
    }
}
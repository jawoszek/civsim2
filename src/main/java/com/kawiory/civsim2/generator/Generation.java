package com.kawiory.civsim2.generator;


import com.kawiory.civsim2.persistance.DataProvider;

public class Generation implements Runnable {

    private GeneratorOptions options;
    private DataProvider dataProvider;


    public Generation(GeneratorOptions options, DataProvider dataProvider) {
        this.options = options;
        if (this.options.getStartSizeX()<6) this.options.setStartSizeX(6);
        if (this.options.getStartSizeY()<6) this.options.setStartSizeY(6);
        if (this.options.getGrowthFactor()<1.3d) this.options.setGrowthFactor(1.3d);
        if (this.options.getIterations()<3) this.options.setIterations(3);
        if (this.options.getWater()>0.9d) this.options.setWater(0.9d);
        if (this.options.getWater()<0d) this.options.setWater(0.0d);
        double[] temp = this.options.getTerrainFactor();
        for (int i=0;i<temp.length;i++){
            if (temp[i]<0.0d) temp[i] = 0.0d;
        }
        this.options.setTerrainFactors(temp);
        this.dataProvider = dataProvider;
    }

    @Override
    public void run() {
        int sizeX = options.getStartSizeX();
        int sizeY = options.getStartSizeY();
        Terrain ter = Terrain.generateSeed(sizeX,sizeY,options.getWater());
        Bioms bioms = new Bioms(sizeX,sizeY);
        for(int i = 0; i < options.getIterations(); i++) {
            sizeX = (int) (sizeX * options.getGrowthFactor());
            sizeY = (int) (sizeY * options.getGrowthFactor());
            ter = ter.enlarge(sizeX, sizeY, options.getTerrainFactor()[i]);
            if(i == 2) {
                bioms = bioms.biomsSeed(ter);
            }
            else if(i > 2){
                bioms = bioms.enlarge(sizeX,sizeY,ter);
            }
        }
        dataProvider.insertMap(options.getName(),Tools.arrayToFields(bioms.toIntegerArray()));
    }
}

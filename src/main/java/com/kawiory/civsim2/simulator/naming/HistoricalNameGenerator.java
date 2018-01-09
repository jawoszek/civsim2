package com.kawiory.civsim2.simulator.naming;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * @author Kacper
 */
public class HistoricalNameGenerator implements NameGenerator {

    private Set<String> alreadyUsed = new HashSet<>();
    private final List<String> historical = new ArrayList<>();
    private final Random random = new Random();

    private final NameGenerator randomNameGenerator = new RandomNameGenerator();

    @Override
    public String generateName() {
        try {
            if (historical.isEmpty()) {
                File file = ResourceUtils.getFile("classpath:data/historical_names.txt");
                historical.addAll(Files.readAllLines(file.toPath()));
            }
            int limit = historical.size();
            while(limit>0){
                String currName = historical.get(random.nextInt(historical.size()));
                if (!alreadyUsed.contains(currName)){
                    alreadyUsed.add(currName);
                    return currName;
                }
                limit--;
            }

        }catch(IOException e){
            e.printStackTrace();
        }
        return randomNameGenerator.generateName();
    }
}

package com.kawiory.civsim2.simulator.naming;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author Kacper
 */
public class RandomNameGenerator implements NameGenerator {

    private final String[] prenames = {"New", "Los", "Las", "West", "San", "Old"};

    private final String[] prefixes = {"Biggero", "Clor", "Es", "Sa", "Cra", "Ata", "Lu"};
    private final String[] infixes = {"fran", "vi", "sha", "or", "co", "pur", "mos"};
    private final String[] suffixes = {"co", "ok", "esh", "ov", "va", "bar", "nox", "reah", "otteh"};

    private final String[] postnames = {"Land", "Prime", "Grano", "Protectorate", "League", "Alliance", "Imperio", "Empire"};

    private int combinations = (prenames.length * prefixes.length * infixes.length * suffixes.length * postnames.length);

    private final Set<String> alreadyUsed = new HashSet<>();
    private final Random r = new Random();

    @Override
    public String generateName() {
        String name;
        if (alreadyUsed.size() >= combinations) {
            alreadyUsed.clear();
        }
        do {
            StringBuilder sb = new StringBuilder();
            if (r.nextInt(10000) % 5 == 0)
                sb.append(prenames[r.nextInt(10000) % prenames.length]).append(" ");

            sb.append(prefixes[r.nextInt(10000) % prefixes.length])
                    .append(infixes[r.nextInt(10000) % infixes.length])
                    .append(suffixes[r.nextInt(10000) % suffixes.length]);

            if (r.nextInt(10000) % 10 == 0)
                sb.append(" ").append(postnames[r.nextInt(10000) % postnames.length]);

            name = sb.toString();

        } while (alreadyUsed.contains(name));

        alreadyUsed.add(name);
        return name;
    }
}

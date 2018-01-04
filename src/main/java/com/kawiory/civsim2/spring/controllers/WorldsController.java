package com.kawiory.civsim2.spring.controllers;

import com.kawiory.civsim2.generator.Generation;
import com.kawiory.civsim2.generator.GeneratorOptions;
import com.kawiory.civsim2.generator.GeneratorsExecutor;
import com.kawiory.civsim2.persistance.DataProvider;
import com.kawiory.civsim2.spring.model.WorldDetails;
import com.kawiory.civsim2.spring.model.WorldPreview;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

/**
 * @author Kacper
 */

@Controller
public class WorldsController {

    private final DataProvider dataProvider;
    private final GeneratorsExecutor generatorsExecutor;

    public WorldsController(DataProvider dataProvider, GeneratorsExecutor generatorsExecutor) {
        this.dataProvider = dataProvider;
        this.generatorsExecutor = generatorsExecutor;
    }

    @RequestMapping(value = "/getWorldsCount", method = RequestMethod.GET)
    @ResponseBody
    public int worldsCount() {
        return dataProvider.getWorldsCount();
    }

    @RequestMapping(value = "/getWorlds", method = RequestMethod.GET)
    @ResponseBody
    public List<WorldPreview> worldsList() {
        return dataProvider.getWorldPreviews();
    }

    @RequestMapping(value = "/getWorldThumbnail", method = RequestMethod.GET)
    @ResponseBody
    public String base64Thumbnail(@RequestParam(value = "id") int id) {
        return "akjcka13"; // TODO
    }

    @RequestMapping(value = "/viewWorld", method = RequestMethod.GET)
    @ResponseBody
    public WorldDetails worldDetails(@RequestParam(value = "id") int id) {
        return dataProvider.getWorldDetails(id);
    }

    @RequestMapping(value = "/colors", method = RequestMethod.GET)
    @ResponseBody
    public Map<Integer, String> getColors() {
        return dataProvider.getColors();
    }

    @RequestMapping(value = "/createWorld", method = RequestMethod.POST)
    @ResponseBody
    public boolean createWorld(@RequestBody GeneratorOptions options) throws RejectedExecutionException {
        System.out.println("Generation options: " + options);
        Generation generation = new Generation(options, dataProvider);
        return generatorsExecutor.addWorld(generation);
    }
}

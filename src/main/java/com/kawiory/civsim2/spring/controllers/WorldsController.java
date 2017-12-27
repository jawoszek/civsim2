package com.kawiory.civsim2.spring.controllers;

import com.kawiory.civsim2.persistance.DataProvider;
import com.kawiory.civsim2.spring.model.WorldDetails;
import com.kawiory.civsim2.spring.model.WorldPreview;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author Kacper
 */

@Controller
public class WorldsController {

    private final DataProvider dataProvider;

    public WorldsController(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
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

}

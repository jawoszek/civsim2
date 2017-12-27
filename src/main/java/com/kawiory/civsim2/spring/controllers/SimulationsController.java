package com.kawiory.civsim2.spring.controllers;

import com.kawiory.civsim2.persistance.DataProvider;
import com.kawiory.civsim2.simulator.Simulation;
import com.kawiory.civsim2.simulator.SimulationsExecutor;
import com.kawiory.civsim2.simulator.Terrains;
import com.kawiory.civsim2.simulator.naming.RandomNameGenerator;
import com.kawiory.civsim2.spring.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

/**
 * @author Kacper
 */

@Controller
public class SimulationsController {

    private final DataProvider dataProvider;
    private final SimulationsExecutor simulationsExecutor;

    public SimulationsController(DataProvider dataProvider, SimulationsExecutor simulationsExecutor) {
        this.dataProvider = dataProvider;
        this.simulationsExecutor = simulationsExecutor;
    }

    @RequestMapping(value = "/getSimulations", method = RequestMethod.GET)
    @ResponseBody
    public SimulationsList simulationList() {
        return dataProvider.getSimulationsList().merge(simulationsExecutor.getWaitingSimulations());
    }

    @RequestMapping(value = "/getSimulationDetails", method = RequestMethod.GET)
    @ResponseBody
    public SimulationDetails simulationDetails(@RequestParam(value = "id") int id) {
        return dataProvider.getSimulationDetails(id);
    }

    @RequestMapping(value = "/getSimulationFrame", method = RequestMethod.GET)
    @ResponseBody
    public SimulationFrame simulationFrame(@RequestParam(value = "id") int id, @RequestParam(value = "frameNumber") int frameNumber) {
        return dataProvider.getSimulationFrame(id,frameNumber);
    }

    @RequestMapping(value = "/getSimulationFrames", method = RequestMethod.GET)
    @ResponseBody
    public List<SimulationFrame> simulationFrames(@RequestParam(value = "id") int id) {
        return dataProvider.getSimulationFrames(id);
    }

    @RequestMapping(value = "/getPropertyValues", method = RequestMethod.GET)
    @ResponseBody
    public Map<Integer,Map<String,String>> propertyValues(@RequestParam(value = "name") String name) {
        return dataProvider.property(name);
    }

    @RequestMapping(value = "/getPropertiesFromSimulation", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<String>> propertiesFromSimulation(@RequestParam(value = "simId") int simId) {
        return dataProvider.properties(simId);
    }

    @RequestMapping(value = "/provinceDetails", method = RequestMethod.GET)
    @ResponseBody
    public ProvinceDetails provinceDetails(@RequestParam(value = "id") int id, @RequestParam(value = "x") int x, @RequestParam(value = "y") int y) {
        return dataProvider.getProvinceDetails(id,x,y);
    }

    @RequestMapping(value = "/getPropertyForFrame", method = RequestMethod.GET)
    @ResponseBody
    public Map<List<Integer>,String> propertyForFrame(@RequestParam(value = "id") int id, @RequestParam(value = "propertyName") String name) {
        return dataProvider.getPropertyForFrame(id,name);
    }

    @RequestMapping(value = "/createSimulation", method = RequestMethod.POST)
    @ResponseBody
    public boolean createSimulation(@RequestBody SimulationPrototype simulation) throws RejectedExecutionException {
        System.out.println(simulation.toString());
        Simulation transformed = simulation.transform(dataProvider, new Terrains(), new RandomNameGenerator());
        return simulationsExecutor.addSimulation(transformed);
    }

    @RequestMapping(value = "/removeSimulation", method = RequestMethod.POST)
    @ResponseBody
    public void removeSimulation(@RequestBody Integer id) {
        System.out.println("Deleting sim id:"+id);
        if (!simulationsExecutor.deleteSimulation(id)) dataProvider.deleteSimulation(id);
    }

    @RequestMapping(value = "/clearQueue", method = RequestMethod.POST)
    @ResponseBody
    public void removeSimulation() {
        simulationsExecutor.clearQueue();
    }

}

package com.kawiory.civsim2.spring.controllers;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Kacper
 */

@Controller
public class ApplicationController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView rootResponse() {
        return new ModelAndView("main.html");
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    @ResponseBody
    public Object status() {
        return ImmutableMap.of("STATUS", "RUNNING");
    }
}

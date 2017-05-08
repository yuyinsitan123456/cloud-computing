package com.aca.controller;

import javax.servlet.http.HttpServletResponse;

import com.aca.cache.FoodCache;
import com.aca.cache.GreenspaceCache;
import com.aca.cache.PostcodeCache;
import com.aca.dao.CouchConnector;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    @Autowired
    private CouchConnector couchConnector;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletResponse response) {
        return "sentiment";
    }

    @RequestMapping(value = "/map/{type}/{state}/{id}", method = RequestMethod.GET)
    public ModelAndView getMap(@PathVariable(value="type") final String type,
                               @PathVariable(value="state") final String state,
                         @PathVariable(value="id") final String id,
                         HttpServletResponse response) {
        ModelAndView mav = null;
        JsonObject o = null;
        JsonArray coordinates = null;
        try {
            switch (type) {
                case "food":
                    mav = new ModelAndView("map");
                    o = FoodCache.melAreas.get(Integer.valueOf(id));
                    coordinates = o.getAsJsonObject("geometry").getAsJsonArray("coordinates");
                    mav.addObject("jsonArray", "[" + coordinates.toString() + "]");
                    return mav;
                case "greenspace":
                    if (state.equals("mel")) {
                        mav = new ModelAndView("map");
                        o = GreenspaceCache.vicAreas.get(id);
                        coordinates = o.getAsJsonObject("geometry").getAsJsonArray("coordinates");
                        mav.addObject("jsonArray", coordinates.toString());
                        return mav;
                    }
                    if (state.equals("syd")) {
                        mav = new ModelAndView("map");
                        o = GreenspaceCache.nswAreas.get(id);
                        coordinates = o.getAsJsonObject("geometry").getAsJsonArray("coordinates");
                        mav.addObject("jsonArray", coordinates.toString());
                        return mav;
                    }
                case "postcode":
                    if (state.equals("mel")) {
                        mav = new ModelAndView("map");
                        o = PostcodeCache.vicAreas.get(id);
                        coordinates = o.getAsJsonObject("geometry").getAsJsonArray("coordinates");
                        mav.addObject("jsonArray", coordinates.toString());
                        return mav;
                    }
                    if (state.equals("syd")) {
                        mav = new ModelAndView("map");
                        o = PostcodeCache.nswAreas.get(id);
                        coordinates = o.getAsJsonObject("geometry").getAsJsonArray("coordinates");
                        mav.addObject("jsonArray", coordinates.toString());
                        return mav;
                    }
                default:
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

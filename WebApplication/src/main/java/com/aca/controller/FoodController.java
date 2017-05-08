package com.aca.controller;

import com.aca.cache.FoodCache;
import com.aca.dao.CouchConnector;
import com.aca.po.FoodRow;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class FoodController {
    @Autowired
    private CouchConnector couchConnector;

    @RequestMapping(value = "/food", method = RequestMethod.GET)
    public ModelAndView getFood(HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("food");

        HashMap<Integer, FoodRow> foodRows = new HashMap<Integer, FoodRow>();
        List<FoodRow> results = new ArrayList<>();

        List<JsonObject> foodTweets = couchConnector.getView("mel", "view/food");
        for (JsonObject o : foodTweets) {
            FoodRow foodRow = new FoodRow();
            foodRows.put(o.get("key").getAsInt(), foodRow);
            foodRows.get(o.get("key").getAsInt()).setOgcFid(o.get("key").getAsInt());
            foodRows.get(o.get("key").getAsInt()).setTotalTweets(o.get("value").getAsInt());
            results.add(foodRow);
        }

        List<JsonObject> foodRelevantTweets = couchConnector.getView("mel", "view/foodRelevant");
        for (JsonObject o : foodRelevantTweets) {
            foodRows.get(o.getAsJsonArray("key").get(0).getAsInt()).setRelevantTweets(o.get("value").getAsInt());
        }

        List<JsonObject> foodSentimentTweets = couchConnector.getView("mel", "view/foodSentiment");
        for (JsonObject o : foodSentimentTweets) {
            if (o.getAsJsonArray("key").get(1).getAsString().equals("positive")) {
                foodRows.get(o.getAsJsonArray("key").get(0).getAsInt()).setPositiveTweets(o.get("value").getAsInt());
            }
        }

        for (FoodRow foodRow : results) {
            JsonObject o = FoodCache.melAreas.getOrDefault(foodRow.getOgcFid(), null);
            if (o != null) {
                foodRow.setEmployees(o.getAsJsonObject("properties").get("food_and_beverage_services").getAsInt());
            }
        }

        mav.addObject("results", results);
        return mav;
    }

    @RequestMapping(value = "/food/relevant/sentiment", method = RequestMethod.GET)
    public String getFoodSentimentRelevant(HttpServletResponse response) {
        return "food/foodRelevantSentiment";
    }
}

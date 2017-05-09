package com.aca.controller;

import com.aca.dao.CouchConnector;
import com.aca.po.GreenspaceRow;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class GreenspaceController {
    @Autowired
    private CouchConnector couchConnector;

    @RequestMapping(value = "/greenspace", method = RequestMethod.GET)
    public ModelAndView getGreenspace(HttpServletResponse response) {
        return getStateGreenspace("mel", response);
    }

    @RequestMapping(value = "/greenspace/{state}", method = RequestMethod.GET)
    public ModelAndView getStateGreenspace(@PathVariable(value="state") final String state,
                                         HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("greenspace");

        try {
            HashMap<String, GreenspaceRow> greenspaceRows = new HashMap<String, GreenspaceRow>();
            List<GreenspaceRow> results = new ArrayList<>();

            List<JsonObject> greenspaceTweets = couchConnector.getView(state, "view/greenspace");
            for (JsonObject o : greenspaceTweets) {
                GreenspaceRow greenspaceRow = new GreenspaceRow();
                greenspaceRows.put(o.get("key").getAsString(), greenspaceRow);
                greenspaceRows.get(o.get("key").getAsString()).setGreenspaceId(o.get("key").getAsString());
                greenspaceRows.get(o.get("key").getAsString()).setTotalTweets(o.get("value").getAsInt());
                results.add(greenspaceRow);
            }

            List<JsonObject> greenspaceSentimentTweets = couchConnector.getView(state, "view/greenspaceSentiment");
            for (JsonObject o : greenspaceSentimentTweets) {
                if (o.getAsJsonArray("key").get(1).getAsString().equals("positive")) {
                    greenspaceRows.get(o.getAsJsonArray("key").get(0).getAsString()).setPositiveTweets(o.get("value").getAsInt());
                }
            }

            mav.addObject("state", state);
            mav.addObject("results", results);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mav;
    }
}

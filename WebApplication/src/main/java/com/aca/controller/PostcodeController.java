package com.aca.controller;

import com.aca.dao.CouchConnector;
import com.aca.po.PostcodeCount;
import com.aca.po.PostcodeRow;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Controller
public class PostcodeController {
    @Autowired
    private CouchConnector couchConnector;

    @RequestMapping(value = "/postcode", method = RequestMethod.GET)
    public ModelAndView getPostcode(HttpServletResponse response) {
        return getStatePostcode("mel", response);
    }

    @RequestMapping(value = "/postcode/{state}", method = RequestMethod.GET)
    public ModelAndView getStatePostcode(@PathVariable(value="state") final String state,
                                       HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("postcode");

        try {
            HashMap<String, PostcodeRow> postcodeRows = new HashMap<String, PostcodeRow>();
            List<PostcodeRow> results = new ArrayList<>();

            List<JsonObject> postcodeTweets = couchConnector.getView(state, "view/postcode");
            for (JsonObject o : postcodeTweets) {
                PostcodeRow postcodeRow = new PostcodeRow();
                postcodeRows.put(o.get("key").getAsString(), postcodeRow);
                postcodeRows.get(o.get("key").getAsString()).setPostcode(o.get("key").getAsString());
                postcodeRows.get(o.get("key").getAsString()).setTotalTweets(o.get("value").getAsInt());
                results.add(postcodeRow);
            }

            List<JsonObject> postcodeSentimentTweets = couchConnector.getView(state, "view/postcodeSentiment");
            for (JsonObject o : postcodeSentimentTweets) {
                if (o.getAsJsonArray("key").get(1).getAsString().equals("positive")) {
                    postcodeRows.get(o.getAsJsonArray("key").get(0).getAsString()).setPositiveTweets(o.get("value").getAsInt());
                }
            }

            mav.addObject("state", state);
            mav.addObject("results", results);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mav;
    }

    @ResponseBody
    @RequestMapping(value = "/postcode/mel/json", method = RequestMethod.GET, produces = "application/json")
    public List<PostcodeCount> getPostcodeMelbourneJson(HttpServletResponse response) {
        return getTop10PostcodeCount("mel");
    }

    @ResponseBody
    @RequestMapping(value = "/postcode/syd/json", method = RequestMethod.GET, produces = "application/json")
    public List<PostcodeCount> getPostcodeSydneyJson(HttpServletResponse response) {
        return getTop10PostcodeCount("syd");
    }

    /*
        Serve for:
        /postcode/mel/json
        /postcode/syd/json
     */
    public List<PostcodeCount> getTop10PostcodeCount(String dbName) {
        /* Get view/postcode */
        List<JsonObject> postcodeCountJsons = couchConnector.getView(dbName, "view/postcode");

        /* Get all [postcode, tweet counts] pairs and sort them in descending order */
        List<PostcodeCount> postcodeCounts = new ArrayList<>();
        for (JsonObject o : postcodeCountJsons) {
            PostcodeCount postcodeCount = new PostcodeCount();
            postcodeCount.setPostcodeLabel(o.get("key").getAsString());
            postcodeCount.setCount(o.get("value").getAsInt());
            postcodeCounts.add(postcodeCount);
        }
        Collections.sort(postcodeCounts, Collections.reverseOrder());

        /* Get top 10 pairs in tweet counts */
        List<PostcodeCount> top10PostcodeCount = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i < postcodeCounts.size()) {
                top10PostcodeCount.add(postcodeCounts.get(i));
            }
        }

        /* Get view/postcodeSentiment */
        List<JsonObject> postcodeSentimentCountJsons = couchConnector.getView(dbName, "view/postcodeSentiment");
        HashMap<String, HashMap<String, Integer>> postcodeSentimentCounts = new HashMap<String, HashMap<String, Integer>>();
        for (JsonObject o : postcodeSentimentCountJsons) {
            String postcode = o.getAsJsonArray("key").get(0).getAsString();
            if (postcodeSentimentCounts.get(postcode) == null)
                postcodeSentimentCounts.put(postcode, new HashMap<String, Integer>());

            String sentiment = o.getAsJsonArray("key").get(1).getAsString();
            int count = o.get("value").getAsInt();
            postcodeSentimentCounts.get(postcode).put(sentiment, count);
        }

        /* Add positive counts into top10 */
        for (PostcodeCount top10 : top10PostcodeCount) {
            top10.setPositiveCount(postcodeSentimentCounts.get(top10.getPostcodeLabel()).getOrDefault("positive", 0));
        }

        return top10PostcodeCount;
    }
}

package com.aca.controller;

import com.aca.dao.CouchConnector;
import com.aca.po.Bullet;
import com.aca.po.PostcodeCount;
import com.aca.po.SentimentMeter;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
public class JsonController {
    @Autowired
    private CouchConnector couchConnector;

    @ResponseBody
    @RequestMapping(value = "/sentiment/meter/json", method = RequestMethod.GET, produces = "application/json")
    public SentimentMeter getSentimentMeterJson(HttpServletResponse response) {
        SentimentMeter sm = new SentimentMeter();

        try {
            List<JsonObject> objs = couchConnector.getView("mel", "view/sentiment");
            double negative = objs.get(0).get("value").getAsInt();
            double positive = objs.get(1).get("value").getAsInt();
            double total = negative + positive;
            if (total == 0) sm.setMelbourneRatio(0.0);
            else sm.setMelbourneRatio(positive / total * 100.0);
            sm.setMelbourneTotal((int) total);
            sm.setMelbournePositive((int) positive);

            objs = couchConnector.getView("syd", "view/sentiment");
            negative = objs.get(0).get("value").getAsInt();
            positive = objs.get(1).get("value").getAsInt();
            total = negative + positive;
            if (total == 0) sm.setSydneyRatio(0.0);
            else sm.setSydneyRatio(positive / total * 100.0);
            sm.setSydneyTotal((int) total);
            sm.setSydneyPositive((int) positive);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sm;
    }

    @ResponseBody
    @RequestMapping(value = "/sentiment/bar/json", method = RequestMethod.GET, produces = "application/json")
    public List<Bullet> getSentimentBarJson(HttpServletResponse response) {
        List<Bullet> bullets = new ArrayList<>();
        try {
            Bullet bullet = new Bullet();
            bullet.setTitle("hello");
            bullet.setSubtitle("world");
            bullet.setRanges(new int[]{10, 50, 100});
            bullet.setMarkers(new int[]{60});
            bullet.setMeasures(new int[]{70});
            bullets.add(bullet);
            bullets.add(bullet);
            bullets.add(bullet);
            bullets.add(bullet);
            bullets.add(bullet);
            bullets.add(bullet);
            bullets.add(bullet);
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return bullets;
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

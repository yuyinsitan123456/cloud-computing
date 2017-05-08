package com.aca.controller;

import com.aca.dao.CouchConnector;
import com.aca.po.Bullet;
import com.aca.po.SentimentMeter;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class SentimentController {
    @Autowired
    private CouchConnector couchConnector;

    @RequestMapping(value = "/sentiment", method = RequestMethod.GET)
    public String getSentiment(HttpServletResponse response) {
        return "sentiment";
    }

    @ResponseBody
    @RequestMapping(value = "/sentiment/meter/json", method = RequestMethod.GET, produces = "application/json")
    public SentimentMeter getSentimentMeterJson(HttpServletResponse response) {
        SentimentMeter sm = new SentimentMeter();

        try {
            List<JsonObject> twoSentiments = couchConnector.getView("mel", "view/sentiment");
            double negative = twoSentiments.get(0).get("value").getAsInt();
            double positive = twoSentiments.get(1).get("value").getAsInt();
            double total = negative + positive;
            if (total == 0) sm.setMelbourneRatio(0.0);
            else sm.setMelbourneRatio(positive / total * 100.0);
            sm.setMelbourneTotal((int) total);
            sm.setMelbournePositive((int) positive);

            twoSentiments = couchConnector.getView("syd", "view/sentiment");
            negative = twoSentiments.get(0).get("value").getAsInt();
            positive = twoSentiments.get(1).get("value").getAsInt();
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
        Bullet bullet = null;
        try {
            /* Mock Hashmap (in case a weekday has no tweets) */
            HashMap<String, Integer> mock = new HashMap<>();
            mock.put("positive", 0);
            mock.put("negative", 0);

            /* Get melbourne weekday-sentiment-count tuples */
            List<JsonObject> melWeekdaySentimentsList = couchConnector.getView("mel", "view/weekdaySentiment");
            HashMap<String, HashMap<String, Integer>> melWeekdaySentimentsMap = new HashMap<String, HashMap<String, Integer>>();
            for (JsonObject o : melWeekdaySentimentsList) {
                String weekday = o.getAsJsonArray("key").get(0).getAsString();
                if (melWeekdaySentimentsMap.get(weekday) == null)
                    melWeekdaySentimentsMap.put(weekday, new HashMap<String, Integer>());

                String sentiment = o.getAsJsonArray("key").get(1).getAsString();
                int count = o.get("value").getAsInt();
                melWeekdaySentimentsMap.get(weekday).put(sentiment, count);
            }

            List<JsonObject> sydWeekdaySentimentsList = couchConnector.getView("syd", "view/weekdaySentiment");
            HashMap<String, HashMap<String, Integer>> sydWeekdaySentimentsMap = new HashMap<String, HashMap<String, Integer>>();
            for (JsonObject o : sydWeekdaySentimentsList) {
                String weekday = o.getAsJsonArray("key").get(0).getAsString();
                if (sydWeekdaySentimentsMap.get(weekday) == null)
                    sydWeekdaySentimentsMap.put(weekday, new HashMap<String, Integer>());

                String sentiment = o.getAsJsonArray("key").get(1).getAsString();
                int count = o.get("value").getAsInt();
                sydWeekdaySentimentsMap.get(weekday).put(sentiment, count);
            }

            int melPositive = 0, melNegative = 0, sydPositive = 0, sydNegative = 0, total = 0;
            HashMap<String, Integer> melDay = null, sydDay = null;

            melDay = melWeekdaySentimentsMap.getOrDefault("Mon", mock);
            sydDay = sydWeekdaySentimentsMap.getOrDefault("Mon", mock);
            melPositive = melDay.getOrDefault("positive", 0);
            melNegative = melDay.getOrDefault("negative", 0);
            sydPositive = sydDay.getOrDefault("positive", 0);
            sydNegative = sydDay.getOrDefault("negative", 0);
            total = melPositive + melNegative + sydPositive + sydNegative;
            bullet = new Bullet();
            bullet.setRanges(new int[]{total / 4, total / 2, total});
            bullet.setMarkers(new int[]{0});
            bullet.setMeasures(new int[]{melPositive + sydPositive});
            bullets.add(bullet);

            melDay = melWeekdaySentimentsMap.getOrDefault("Tue", mock);
            sydDay = sydWeekdaySentimentsMap.getOrDefault("Tue", mock);
            melPositive = melDay.getOrDefault("positive", 0);
            melNegative = melDay.getOrDefault("negative", 0);
            sydPositive = sydDay.getOrDefault("positive", 0);
            sydNegative = sydDay.getOrDefault("negative", 0);
            total = melPositive + melNegative + sydPositive + sydNegative;
            bullet = new Bullet();
            bullet.setRanges(new int[]{total / 4, total / 2, total});
            bullet.setMarkers(new int[]{0});
            bullet.setMeasures(new int[]{melPositive + sydPositive});
            bullets.add(bullet);

            melDay = melWeekdaySentimentsMap.getOrDefault("Wed", mock);
            sydDay = sydWeekdaySentimentsMap.getOrDefault("Wed", mock);
            melPositive = melDay.getOrDefault("positive", 0);
            melNegative = melDay.getOrDefault("negative", 0);
            sydPositive = sydDay.getOrDefault("positive", 0);
            sydNegative = sydDay.getOrDefault("negative", 0);
            total = melPositive + melNegative + sydPositive + sydNegative;
            bullet = new Bullet();
            bullet.setRanges(new int[]{total / 4, total / 2, total});
            bullet.setMarkers(new int[]{0});
            bullet.setMeasures(new int[]{melPositive + sydPositive});
            bullets.add(bullet);

            melDay = melWeekdaySentimentsMap.getOrDefault("Thu", mock);
            sydDay = sydWeekdaySentimentsMap.getOrDefault("Thu", mock);
            melPositive = melDay.getOrDefault("positive", 0);
            melNegative = melDay.getOrDefault("negative", 0);
            sydPositive = sydDay.getOrDefault("positive", 0);
            sydNegative = sydDay.getOrDefault("negative", 0);
            total = melPositive + melNegative + sydPositive + sydNegative;
            bullet = new Bullet();
            bullet.setRanges(new int[]{total / 4, total / 2, total});
            bullet.setMarkers(new int[]{0});
            bullet.setMeasures(new int[]{melPositive + sydPositive});
            bullets.add(bullet);

            melDay = melWeekdaySentimentsMap.getOrDefault("Fri", mock);
            sydDay = sydWeekdaySentimentsMap.getOrDefault("Fri", mock);
            melPositive = melDay.getOrDefault("positive", 0);
            melNegative = melDay.getOrDefault("negative", 0);
            sydPositive = sydDay.getOrDefault("positive", 0);
            sydNegative = sydDay.getOrDefault("negative", 0);
            total = melPositive + melNegative + sydPositive + sydNegative;
            bullet = new Bullet();
            bullet.setRanges(new int[]{total / 4, total / 2, total});
            bullet.setMarkers(new int[]{0});
            bullet.setMeasures(new int[]{melPositive + sydPositive});
            bullets.add(bullet);

            melDay = melWeekdaySentimentsMap.getOrDefault("Sat", mock);
            sydDay = sydWeekdaySentimentsMap.getOrDefault("Sat", mock);
            melPositive = melDay.getOrDefault("positive", 0);
            melNegative = melDay.getOrDefault("negative", 0);
            sydPositive = sydDay.getOrDefault("positive", 0);
            sydNegative = sydDay.getOrDefault("negative", 0);
            total = melPositive + melNegative + sydPositive + sydNegative;
            bullet = new Bullet();
            bullet.setRanges(new int[]{total / 4, total / 2, total});
            bullet.setMarkers(new int[]{0});
            bullet.setMeasures(new int[]{melPositive + sydPositive});
            bullets.add(bullet);

            melDay = melWeekdaySentimentsMap.getOrDefault("Sun", mock);
            sydDay = sydWeekdaySentimentsMap.getOrDefault("Sun", mock);
            melPositive = melDay.getOrDefault("positive", 0);
            melNegative = melDay.getOrDefault("negative", 0);
            sydPositive = sydDay.getOrDefault("positive", 0);
            sydNegative = sydDay.getOrDefault("negative", 0);
            total = melPositive + melNegative + sydPositive + sydNegative;
            bullet = new Bullet();
            bullet.setRanges(new int[]{total / 4, total / 2, total});
            bullet.setMarkers(new int[]{0});
            bullet.setMeasures(new int[]{melPositive + sydPositive});
            bullets.add(bullet);
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return bullets;
    }
}

package com.aca.controller;

import com.aca.dao.CouchConnector;
import com.aca.po.Bullet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.aca.po.SentimentMeter;
import com.aca.service.DayOfWeekSentiment;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
    @Autowired
    private DayOfWeekSentiment dayOfWeekSentiment;
    @Autowired
    private CouchConnector couchConnector = new CouchConnector();


    @RequestMapping(value = "/")
    public String test(HttpServletResponse response) throws IOException {
        return "sentiment";
    }

    @ResponseBody
    @RequestMapping(value = "/sentiment/meter/json", produces = "application/json")
    public SentimentMeter sentimentJson(HttpServletResponse response) throws IOException {
        SentimentMeter sm = new SentimentMeter();

        List<JsonObject> objs = couchConnector.getSentimentMeter("mel");
        double negative = objs.get(0).get("value").getAsInt();
        double positive = objs.get(1).get("value").getAsInt();
        double total = negative + positive;
        if (total == 0) sm.setMelbourneRatio(0.0);
        else sm.setMelbourneRatio(positive / total * 100.0);
        sm.setMelbourneTotal((int) total);
        sm.setMelbournePositive((int) positive);

        objs = couchConnector.getSentimentMeter("syd");
        negative = objs.get(0).get("value").getAsInt();
        positive = objs.get(1).get("value").getAsInt();
        total = negative + positive;
        if (total == 0) sm.setSydneyRatio(0.0);
        else sm.setSydneyRatio(positive / total * 100.0);
        sm.setSydneyTotal((int) total);
        sm.setSydneyPositive((int) positive);

        return sm;
    }

    @ResponseBody
    @RequestMapping(value = "/sentiment/bar/json", produces = "application/json")
    public List<Bullet> testJson(HttpServletResponse response) throws IOException {
        List<Bullet> bullets = new ArrayList<>();
        Bullet bullet = new Bullet();
        bullet.setTitle("hello");
        bullet.setSubtitle("world");
        bullet.setRanges(new int[]{10,50,100});
        bullet.setMarkers(new int[]{60});
        bullet.setMeasures(new int[]{70});
        bullets.add(bullet);
        bullets.add(bullet);
        bullets.add(bullet);
        bullets.add(bullet);
        bullets.add(bullet);
        bullets.add(bullet);
        bullets.add(bullet);
        return bullets;
    }

    /*
    @ResponseBody
    @RequestMapping(value = "/do")
    public List<CaseOne> testDo(HttpServletResponse response) throws IOException {
        Map<Tuple2<Integer, Boolean>, Integer> result = dayOfWeekSentiment.doMelbourne();
        List<CaseOne> resultJsons = new ArrayList<>();
        for (Tuple2<Integer, Boolean> key : result.keySet()) {
            CaseOne json = new CaseOne();
            json.setWeekOfDay((Integer) key.productElement(0));
            json.setSentiment((Boolean) key.productElement(1));
            json.setCount(result.get(key));
            resultJsons.add(json);
        }
        return resultJsons;
    }
    */
}

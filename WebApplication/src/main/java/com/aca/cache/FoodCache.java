package com.aca.cache;

import com.aca.dao.CouchConnector;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
@Scope("singleton")
public class FoodCache implements InitializingBean {
    @Autowired
    private CouchConnector couchConnector;

    public static HashMap<Integer, JsonObject> melAreas = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        try {
            List<JsonObject> foodAreasJson = couchConnector.getAllDocs("food");
            for (JsonObject o : foodAreasJson) {
                int ogc_fid = o.getAsJsonObject("properties").get("ogc_fid").getAsInt();
                melAreas.put(ogc_fid, o);
            }
        } catch (Exception e) {
            /* Initialiaztion failed! */
            e.printStackTrace();
        }
    }
}

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
public class PostcodeCache implements InitializingBean {
    @Autowired
    private CouchConnector couchConnector;

    public static HashMap<String, JsonObject> vicAreas = new HashMap<>();
    public static HashMap<String, JsonObject> nswAreas = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        try {
            List<JsonObject> vicAreasJson = couchConnector.getAllDocs("vic_postcode");
            for (JsonObject o : vicAreasJson) {
                String postcode = o.getAsJsonObject("properties").get("postcode").getAsString();
                vicAreas.put(postcode, o);
            }

            List<JsonObject> nswAreasJson = couchConnector.getAllDocs("nsw_postcode");
            for (JsonObject o : nswAreasJson) {
                String postcode = o.getAsJsonObject("properties").get("postcode").getAsString();
                nswAreas.put(postcode, o);
            }
        } catch (Exception e) {
            /* Initialiaztion failed! */
            e.printStackTrace();
        }
    }
}

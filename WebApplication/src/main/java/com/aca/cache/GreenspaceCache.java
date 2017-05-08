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
public class GreenspaceCache implements InitializingBean {
    @Autowired
    private CouchConnector couchConnector;

    public static HashMap<String, JsonObject> vicAreas = new HashMap<>();
    public static HashMap<String, JsonObject> nswAreas = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        try {
            List<JsonObject> vicAreasJson = couchConnector.getAllDocs("vic_greenspace");
            for (JsonObject o : vicAreasJson) {
                String greenspace_pid = o.getAsJsonObject("properties").get("greenspace_pid").getAsString();
                vicAreas.put(greenspace_pid, o);
            }

            List<JsonObject> nswAreasJson = couchConnector.getAllDocs("nsw_greenspace");
            for (JsonObject o : nswAreasJson) {
                String greenspace_pid = o.getAsJsonObject("properties").get("greenspace_pid").getAsString();
                nswAreas.put(greenspace_pid, o);
            }
        } catch (Exception e) {
            /* Initialiaztion failed! */
            e.printStackTrace();
        }
    }
}

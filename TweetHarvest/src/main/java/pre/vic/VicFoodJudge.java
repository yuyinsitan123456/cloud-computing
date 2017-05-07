package pre.vic;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import th.Main;

import java.awt.geom.Path2D;
import java.util.*;

public class VicFoodJudge {

    private Map<Integer, Path2D> foods = new HashMap<>();

    public VicFoodJudge(String dbAddr, String dbUser, String dbPass) {
        System.out.println("Start VicFood downloading.");

        CouchDbProperties properties = new CouchDbProperties()
                .setDbName("food")
                .setCreateDbIfNotExist(true)
                .setProtocol("http")
                .setHost(dbAddr)
                .setPort(5984)
                .setMaxConnections(100)
                .setUsername(dbUser)
                .setPassword(dbPass)
                .setConnectionTimeout(0);

        CouchDbClient db = new CouchDbClient(properties);

        List<JsonObject> allDocs = db.view("_all_docs")
                .includeDocs(true)
                .query(JsonObject.class);
        db.shutdown();
        System.out.println("VicFood downloading finished.");

        List<Double> longitudes = null;
        List<Double> latitudes = null;

        for (JsonObject o : allDocs) {
            longitudes = new ArrayList<>();
            latitudes = new ArrayList<>();

            JsonArray coordinates = o.getAsJsonObject("geometry").getAsJsonArray("coordinates").get(0).getAsJsonArray();
            for (JsonElement coordsEle : coordinates) {
                JsonArray coords = coordsEle.getAsJsonArray();
                longitudes.add(coords.get(0).getAsDouble());
                latitudes.add(coords.get(1).getAsDouble());
            }

            Path2D area = new Path2D.Double();
            for (int num = 0; num < longitudes.size(); num++) {
                if (num == 0) {
                    area.moveTo(longitudes.get(num), latitudes.get(num));
                } else {
                    area.lineTo(longitudes.get(num), latitudes.get(num));
                }
            }
            area.closePath();

            int ogc_fid = o.getAsJsonObject("properties").get("ogc_fid").getAsInt();
            foods.put(ogc_fid, area);
        }
    }

    public Integer judge(double longitude, double latitude) {
        Iterator iter = foods.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Path2D area = (Path2D) entry.getValue();
            if (area.contains(longitude, latitude)) {
                return (Integer) entry.getKey();
            }
        }
        return null;
    }
}

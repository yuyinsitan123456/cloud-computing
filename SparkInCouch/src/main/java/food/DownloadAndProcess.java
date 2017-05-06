package food;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.awt.geom.Path2D;
import java.util.*;

public class DownloadAndProcess {

    public static void main(String[] args) {
        CouchDbProperties properties = new CouchDbProperties()
                .setDbName("food")
                .setCreateDbIfNotExist(true)
                .setProtocol("http")
                .setHost("127.0.0.1")
                .setPort(5984)
                .setMaxConnections(100)
                .setUsername("couchdb")
                .setPassword("123456")
                .setConnectionTimeout(0);

        CouchDbClient db = new CouchDbClient(properties);

        List<JsonObject> allDocs = db.view("_all_docs")
                .includeDocs(true)
                .query(JsonObject.class);

        /* DS */
        List<Double> longitudes = null;
        List<Double> latitudes = null;
        Map<Integer, Path2D> foods = new HashMap<>();

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


        /* judge */
        Iterator iter = foods.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Path2D area = (Path2D) entry.getValue();
            if (area.contains(144.960144, -37.808206)) {
                System.out.println((int) entry.getKey());
                break;
            }
        }
    }

}

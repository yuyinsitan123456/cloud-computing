package pre.nsw;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import th.Main;

import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NswPostcodeJudge {
    private Map<Path2D, String> list = null;

    public NswPostcodeJudge(String dbAddr, String dbUser, String dbPass) {
        System.out.println("Start NswPostcode downloading.");

        CouchDbProperties properties = new CouchDbProperties()
                .setDbName("nsw_postcode")
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
        System.out.println("NswPostcode downloading finished.");

        list = new HashMap<>();

        for (JsonObject o : allDocs) {
            JsonArray arr1 = o.getAsJsonObject("geometry").getAsJsonArray("coordinates");
            for (JsonElement arr2Ele : arr1) {
                JsonArray arr2 = arr2Ele.getAsJsonArray();
                for (JsonElement arr3Ele : arr2) {
                    JsonArray arr3 = arr3Ele.getAsJsonArray();

                    Path2D area = new Path2D.Double();
                    int i = 0;
                    for (JsonElement arr4Ele : arr3) {
                        JsonArray arr4 = arr4Ele.getAsJsonArray();
                        if (i == 0) {
                            area.moveTo(arr4.get(0).getAsDouble(), arr4.get(1).getAsDouble());
                        } else {
                            area.lineTo(arr4.get(0).getAsDouble(), arr4.get(1).getAsDouble());
                        }
                        ++i;
                    }
                    area.closePath();
                    list.put(area, o.getAsJsonObject("properties").get("postcode").getAsString());
                }
            }
        }
    }

    public String judge(double longitude, double latitude) {
        for (Path2D postcodeArea : list.keySet()) {
            if (postcodeArea.contains(longitude, latitude)) {
                return list.get(postcodeArea);
            }
        }
        return null;
    }
}

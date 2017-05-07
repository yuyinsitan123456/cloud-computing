package pre.nsw;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import th.Main;

import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NswGreenspaceJudge {
    private Map<Path2D, String> greenspaces = new HashMap<>();

    public NswGreenspaceJudge(String dbAddr, String dbUser, String dbPass) {
        System.out.println("Start NswGreenspace downloading.");

        CouchDbProperties properties = new CouchDbProperties()
                .setDbName("nsw_greenspace")
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
        System.out.println("NswGreenspace downloading finished.");

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
                    greenspaces.put(area, o.getAsJsonObject("properties").get("greenspace_pid").getAsString());
                }
            }
        }
    }

    public String judge(double longitude, double latitude) {
        Iterator iter = greenspaces.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Path2D area = (Path2D) entry.getKey();
            if (area.contains(longitude, latitude)) {
                return (String) entry.getValue();
            }
        }
        return null;
    }
}

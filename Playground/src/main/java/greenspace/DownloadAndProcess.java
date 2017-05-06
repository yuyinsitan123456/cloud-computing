package greenspace;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.awt.geom.Path2D;
import java.util.*;

public class DownloadAndProcess {

    public static void main(String[] args) {
        System.out.println("Downloading started.");

        CouchDbProperties properties = new CouchDbProperties()
                .setDbName("nsw_greenspace")
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

        System.out.println("Downloading finished.");

         /* DS */
        Map<Path2D, String> greenspaces = new HashMap<>();

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
                    greenspaces.put(area, o.getAsJsonObject("properties").get("greenspace_label_name").getAsString());
                }
            }
        }

        /* judge */
        Iterator iter = greenspaces.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Path2D area = (Path2D) entry.getKey();
            if (area.contains(150.784095635553, -33.7360012037549)) {
                System.out.println((String) entry.getValue());
                break;
            }
        }
    }
}

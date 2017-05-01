package couch;

import com.google.gson.JsonObject;
import java.util.Date;
import java.util.List;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

public class TweetsInOneDay {

    public static String startDate = "";
    public static String endDate = "";
    public static String dbHost = "127.0.0.1";
    public static String startKey = "";
    public static String endKey = "";
    public static String view = "test/createdAt";

    public static void generateStartAndEndDate() {
        Date dt = new Date();
        /* Test date */
        dt.setMonth(3);
        dt.setDate(30);
        
        String[] s = dt.toString().split(" ");

        dt.setHours(0);
        dt.setMinutes(0);
        dt.setSeconds(0);
        startDate = dt.toString().replace(s[4], "+0000");

        dt.setHours(23);
        dt.setMinutes(59);
        dt.setSeconds(59);
        endDate = dt.toString().replace(s[4], "+0000");

        System.out.println(startDate + " - " + endDate);
    }

    public static void main(String[] args) {
        generateStartAndEndDate();
        
        System.out.println("===== Retrieve by Date =====");
        
        CouchDbProperties properties = new CouchDbProperties()
                .setDbName("melbourne")
                .setCreateDbIfNotExist(true)
                .setProtocol("http")
                .setHost(dbHost)
                .setPort(5984)
                .setMaxConnections(100)
                .setConnectionTimeout(0);

        CouchDbClient db = new CouchDbClient(properties);

        long till = System.currentTimeMillis();

        List<JsonObject> allDocs = db.view(view)
                .startKey(startDate)
                .endKey(endDate)
                .query(JsonObject.class);

        System.out.println("Document count: " + allDocs.size());
        System.out.println("Total time: " + (System.currentTimeMillis() - till));

        /* Get _id of the first document */
        List<JsonObject> firstDoc = db.view(view)
                .startKey(startDate)
                .endKey(endDate)
                .limit(1)
                .query(JsonObject.class);

        for (JsonObject doc : firstDoc) {
            startKey = doc.get("value").getAsString();
        }

        /* Get _id of the last document */
        List<JsonObject> lastDoc = db.view(view)
                .descending(Boolean.TRUE)
                .startKey(endDate)
                .endKey(startDate)
                .limit(1)
                .query(JsonObject.class);

        for (JsonObject doc : lastDoc) {
            endKey = doc.get("value").getAsString();
        }
        
        /* Check */
        System.out.println("===== Retrieve by _id =====");
        
        allDocs = db.view("_all_docs")
                .startKey(startKey)
                .endKey(endKey)
                .query(JsonObject.class);

        System.out.println("Document count: " + allDocs.size());
        System.out.println("Total time: " + (System.currentTimeMillis() - till));
    }
}

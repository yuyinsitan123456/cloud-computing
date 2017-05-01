package couch;

import com.google.gson.JsonObject;
import java.util.Date;
import java.util.List;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

public class TweetsInOneDay {

    public static void main(String[] args) {
        Date dt = new Date();
        //dt.setDate(26);
        String[] s = dt.toString().split(" ");
        
        dt.setHours(0);
        dt.setMinutes(0);
        dt.setSeconds(0);
        String startKey = dt.toString();
        startKey = startKey.replace(s[4], "+0000");

        dt.setHours(23);
        dt.setMinutes(59);
        dt.setSeconds(59);
        String endKey = dt.toString();
        endKey = endKey.replace(s[4], "+0000");
        
        System.out.println(startKey + " - " + endKey);

        CouchDbProperties properties = new CouchDbProperties()
                .setDbName("melbourne")
                .setCreateDbIfNotExist(true)
                .setProtocol("http")
                .setHost("127.0.0.1")
                .setPort(5984)
                .setMaxConnections(100)
                .setConnectionTimeout(0);

        CouchDbClient db = new CouchDbClient(properties);

        long till = System.currentTimeMillis();

        List<JsonObject> allDocs = db.view("test/createdAt")
                .startKey(startKey)
                .endKey(endKey)
                .query(JsonObject.class);

        /*
        for (JsonObject doc : allDocs) {
            System.out.println(doc.get("value").getAsString());
        }
        */
        System.out.println("Document count: " + allDocs.size());

        System.out.println("Total time: " + (System.currentTimeMillis() - till));
        
        /* First */
        List<JsonObject> firstDoc = db.view("test/createdAt")
                .startKey(startKey)
                .endKey(endKey)
                .limit(1)
                .query(JsonObject.class);
        
        for (JsonObject doc : firstDoc) {
            System.out.println(doc.get("value").getAsString());
        }
        
        /* Last */
        List<JsonObject> lastDoc = db.view("test/createdAt")
                .descending(Boolean.TRUE)
                .startKey(endKey)
                .endKey(startKey)
                .limit(1)
                .query(JsonObject.class);
        
        for (JsonObject doc : lastDoc) {
            System.out.println(doc.get("value").getAsString());
        }
    }
}

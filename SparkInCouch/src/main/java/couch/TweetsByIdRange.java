package couch;

import com.google.gson.JsonObject;
import java.util.List;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

public class TweetsByIdRange {
    public static void main(String[] args) {

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

        List<JsonObject> allDocs = db.view("_all_docs")
                .startKey("857021409410793472")
                .endKey("857383785633009664")
                .query(JsonObject.class);

        /*
        for (JsonObject doc : allDocs) {
            System.out.println(doc.get("value").getAsString());
        }
        */
        System.out.println("Document count: " + allDocs.size());

        System.out.println("Total time: " + (System.currentTimeMillis() - till));
    }
}

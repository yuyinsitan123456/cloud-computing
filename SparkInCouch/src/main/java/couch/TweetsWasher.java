package couch;

import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.util.List;

public class TweetsWasher {
    /*
    http://127.0.0.1:5984/melbourne/_design/test/_view/idTimeText?limit=10&skip=1234
     */
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
                .includeDocs(true)
                .query(JsonObject.class);

        int i = 0;
        for (JsonObject doc : allDocs) {
            boolean value = true;
            if (i % 2 == 1) {
                value = false;
            }

            if (doc.get("sentiment") != null) {
                db.remove(doc.get("_id").getAsString(), doc.get("_rev").getAsString());
            } else {
                doc.addProperty("sentiment", value);
                db.update(doc);
            }
            ++i;
        }

    }

}

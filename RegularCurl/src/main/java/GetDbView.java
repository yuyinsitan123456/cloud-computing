import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.util.List;

public class GetDbView {
    public void getDbView(String dbName, String viewName) {
        String dbAddr = "127.0.0.1";
        String dbUser = "couchdb";
        String dbPassword = "123456";

        CouchDbProperties properties = new CouchDbProperties()
                .setDbName(dbName)
                .setCreateDbIfNotExist(true)
                .setProtocol("http")
                .setHost(dbAddr)
                .setPort(5984)
                .setUsername(dbUser)
                .setPassword(dbPassword)
                .setMaxConnections(100)
                .setConnectionTimeout(0);

        CouchDbClient db = new CouchDbClient(properties);

        List<JsonObject> allDocs = db.view(viewName)
                .group(true)
                .query(JsonObject.class);

        for (JsonObject o : allDocs) {
            System.out.println(viewName);
            System.out.println(o.toString());
        }

        db.shutdown();
    }
}

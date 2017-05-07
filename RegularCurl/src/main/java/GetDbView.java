import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.util.List;

public class GetDbView {
    public void getDbView(String dbName, String viewName) {
        CouchDbProperties properties = new CouchDbProperties()
                .setDbName(dbName)
                .setCreateDbIfNotExist(true)
                .setProtocol("http")
                .setHost(Main.dbAddr)
                .setPort(5984)
                .setUsername(Main.dbUser)
                .setPassword(Main.dbPassword)
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

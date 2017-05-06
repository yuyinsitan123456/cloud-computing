package nlp;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.DocumentConflictException;

public class CouchInsertor {
    private CouchDbClient dbClient = null;
    private final JsonParser parser = new JsonParser();

    public CouchInsertor(String dbName, String dbUser, String dbPassword) {
        CouchDbProperties properties = new CouchDbProperties()
                .setDbName(dbName)
                .setCreateDbIfNotExist(true)
                .setProtocol("http")
                .setHost("127.0.0.1")
                .setPort(5984)
                .setMaxConnections(100)
                .setUsername(dbUser)
                .setPassword(dbPassword)
                .setConnectionTimeout(0);
        try {
            dbClient = new CouchDbClient(properties);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void insert(JsonObject tweet) {
        if (dbClient != null) {
            try {
                dbClient.save(tweet);
            } catch (DocumentConflictException ex) {
                // Regular conclusion of duplication occurrence, so do nothing
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

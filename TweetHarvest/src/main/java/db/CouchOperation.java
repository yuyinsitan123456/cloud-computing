package db;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import exhandle.ErrorHandler;
import org.lightcouch.DocumentConflictException;

public class CouchOperation implements DatabaseOperation, AutoCloseable {

    private CouchDbClient dbClient = null;
    private final JsonParser parser = new JsonParser();

    public CouchOperation(String dbAddr, String dbName, String dbUser, String dbPassword) {
        CouchDbProperties properties = new CouchDbProperties()
                .setDbName(dbName)
                .setCreateDbIfNotExist(true)
                .setProtocol("http")
                .setHost(dbAddr)
                .setPort(5984)
                .setMaxConnections(100)
                .setUsername(dbUser)
                .setPassword(dbPassword)
                .setConnectionTimeout(0);
        try {
            dbClient = new CouchDbClient(properties);
        } catch (Exception ex) {
            ErrorHandler.process(this.getClass(), ex, true,
                    "Failed to establish CouchDB connection. Please check CouchDB settings.");
        }
    }

    @Override
    public void insertTweet(String tweet) {
        if (dbClient != null) {
            try {
                JsonObject o = parser.parse(tweet).getAsJsonObject();
                String id_str = o.get("id_str").getAsString();

                /* Insert only when the tweet is a new one */
                
                // If we don't check duplication?
                // if (!dbClient.contains(id_str)) {
                    o.addProperty("_id", id_str);
                    dbClient.save(o);
                // }
            } catch (DocumentConflictException ex) {
                // Do nothing
            } catch (Exception ex) {
                ErrorHandler.process(this.getClass(), ex, true,
                        "Failed to insert tweet into CouchDB. Please check CouchDB settings.");
            }
        }
    }

    @Override
    public void close() {
        dbClient.shutdown();
    }
}

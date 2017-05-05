package db;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.DocumentConflictException;

import java.util.List;

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
            System.out.println("Failed to establish CouchDB connection. Please check CouchDB settings.");
        }
    }

    @Override
    public void insertTweet(JsonObject tweet) {
        if (dbClient != null) {
            try {
                String id_str = tweet.get("id_str").getAsString();

                /* Insert only when the tweet is a new one */
                // if (!dbClient.contains(id_str)) {
                tweet.addProperty("_id", id_str);
                dbClient.save(tweet);
                // }
            } catch (DocumentConflictException ex) {
                // Regular conclusion of duplication occurrence, so do nothing
            } catch (Exception ex) {
                System.out.println("Failed to insert tweet into CouchDB. Please check CouchDB settings.");
            }
        }
    }

    @Override
    public List<JsonObject> getAllDocs() {
        return dbClient.view("_all_docs")
                .includeDocs(true)
                .query(JsonObject.class);
    }

    @Override
    public void close() {
        dbClient.shutdown();
    }
}

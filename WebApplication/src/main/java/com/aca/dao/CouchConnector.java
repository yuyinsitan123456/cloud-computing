package com.aca.dao;

import com.aca.po.SentimentMeter;
import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouchConnector {
    public static final String dbAddr = "127.0.0.1";
    public static final String dbUser = "couchdb";
    public static final String dbPassword = "123456";

    public List<JsonObject> getSentimentMeter(String dbName) {
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
        CouchDbClient dbClient = new CouchDbClient(properties);
        List<JsonObject> allDocs = dbClient.view("view/sentiment")
                .group(true)
                .query(JsonObject.class);
        return allDocs;
    }
}

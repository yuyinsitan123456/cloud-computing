package db;

import com.google.gson.JsonObject;

import java.util.List;

public interface DatabaseOperation {
    public abstract void insertTweet(JsonObject tweet);
    public abstract List<JsonObject> getAllDocs();
}

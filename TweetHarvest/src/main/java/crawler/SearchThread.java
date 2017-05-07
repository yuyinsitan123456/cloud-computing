package crawler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.twitter.hbc.core.endpoint.Location;
import java.util.List;
import db.CouchOperation;
import db.DatabaseOperation;
import exhandle.ErrorHandler;
import geo.LocationWithToken;
import pre.nlp.NLPProcessor;
import setting.OAuthSet;
import th.Main;
import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.conf.ConfigurationBuilder;

public class SearchThread extends Thread {

    private static final int TWEET_COUNT_PER_QUERY = 100;
    private static final int QUERY_INTERVAL = 60000;

    private final JsonParser parser = new JsonParser();
    private Twitter twitter = null;
    private Location location = null;
    private DatabaseOperation db = null;
    private boolean resetMode = false;
    private NLPProcessor nlp = null;

    public SearchThread(OAuthSet oauth, String dbAddr, LocationWithToken locationWithToken,
            String dbUser, String dbPassword, boolean resetMode, NLPProcessor nlp) {
        this.location = locationWithToken.getLocation();
        
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(oauth.getConsumerKey())
                .setOAuthConsumerSecret(oauth.getConsumerSecret())
                .setOAuthAccessToken(oauth.getAccessToken())
                .setOAuthAccessTokenSecret(oauth.getAccessTokenSecret())
                .setJSONStoreEnabled(true);

        twitter = new TwitterFactory(cb.build()).getInstance();
        
        db = new CouchOperation(dbAddr, locationWithToken.getToken(), dbUser, dbPassword);
        
        this.resetMode = resetMode;
        this.nlp = nlp;
    }

    @Override
    public void run() {
        try {
            long lastId = Long.MAX_VALUE;
            /* Go on fetching even the current query reaches the end */
            while (true) {
                /* Reset the MaxId if reset mode is on */
                if (resetMode) lastId = Long.MAX_VALUE;
                
                Query query;
                query = new Query();

                double centreLat = (location.southwestCoordinate().latitude()
                        + location.northeastCoordinate().latitude()) / 2.0;
                double centreLong = (location.southwestCoordinate().longitude()
                        + location.northeastCoordinate().longitude()) / 2.0;
                double radius = Math.abs(location.southwestCoordinate().latitude()
                        - location.northeastCoordinate().latitude()) * Math.sqrt(2.0) / 2.0 * 100;
                query.setGeoCode(new GeoLocation(centreLat, centreLong), radius, Query.Unit.km);
                query.setLang("en");
                query.setCount(TWEET_COUNT_PER_QUERY);

                query.setMaxId(lastId - 1);

                QueryResult result;
                do {
                    result = twitter.search(query);
                    List<Status> tweets = result.getTweets();
                    for (Status status : tweets) {
                        String rawJSON = TwitterObjectFactory.getRawJSON(status);
                        JsonObject tweet = parser.parse(rawJSON).getAsJsonObject();

                        if (nlp != null) nlp.addSentiment(tweet);

                        String vicPostcode = null;
                        String nswPostcode = null;
                        String vicGreenspace = null;
                        String nswGreenspace = null;
                        Integer vicFood = null;
                        if (tweet.has("coordinates") && tweet.get("coordinates").isJsonObject()) {
                            JsonArray coords = tweet.getAsJsonObject("coordinates").getAsJsonArray("coordinates");
                            double longitude = coords.get(0).getAsDouble();
                            double latitude = coords.get(1).getAsDouble();
                            vicPostcode = Main.vicPostcodeJudge.judge(longitude, latitude);
                            nswPostcode = Main.nswPostcodeJudge.judge(longitude, latitude);
                            vicGreenspace = Main.vicGreenspaceJudge.judge(longitude, latitude);
                            nswGreenspace = Main.nswGreenspaceJudge.judge(longitude, latitude);
                            vicFood = Main.vicFoodJudge.judge(longitude, latitude);
                        }
                        tweet.addProperty("vic_postcode", vicPostcode);
                        tweet.addProperty("nsw_postcode", nswPostcode);
                        tweet.addProperty("vic_greenspace", vicGreenspace);
                        tweet.addProperty("nsw_greenspace", nswGreenspace);
                        tweet.addProperty("vic_food", vicFood);

                        db.insertTweet(tweet);
                        
                        /* Update lastId record */
                        if (status.getId() < lastId) {
                            lastId = status.getId();
                        }
                    }

                    Thread.sleep(QUERY_INTERVAL);
                } while ((query = result.nextQuery()) != null);
            }
        } catch (TwitterException ex) {
            ErrorHandler.process(this.getClass(), ex, false,
                        "Failed to get rate limit status. Please check SearchThread settings.");
        } catch (InterruptedException ex) {
            ErrorHandler.process(this.getClass(), ex, false,
                        "InterruptedException occurred in SearchThread. Please check SearchThread settings.");
        } catch (Exception ex) {
            ErrorHandler.process(this.getClass(), ex, true,
                        "Exception occurred in SearchThread. Please check SearchThread settings.");
        }
    }
}

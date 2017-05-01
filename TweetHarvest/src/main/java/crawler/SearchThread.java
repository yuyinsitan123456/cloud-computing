package crawler;

import com.twitter.hbc.core.endpoint.Location;
import java.util.List;
import db.CouchOperation;
import db.DatabaseOperation;
import exhandle.ErrorHandler;
import geo.LocationWithToken;
import setting.OAuthSet;
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

    private Twitter twitter = null;
    private Location location = null;
    private DatabaseOperation db = null;
    private boolean resetMode = false;

    public SearchThread(OAuthSet oauth, String dbAddr, LocationWithToken locationWithToken,
            String dbUser, String dbPassword, boolean resetMode) {
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
                    for (Status tweet : tweets) {
                        String rawJSON = TwitterObjectFactory.getRawJSON(tweet);
                        db.insertTweet(rawJSON);
                        
                        /* Update lastId record */
                        if (tweet.getId() < lastId) {
                            lastId = tweet.getId();
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

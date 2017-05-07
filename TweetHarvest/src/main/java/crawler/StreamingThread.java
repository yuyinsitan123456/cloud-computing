package crawler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.Location;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import db.CouchOperation;
import db.DatabaseOperation;
import exhandle.ErrorHandler;
import geo.LocationWithToken;
import pre.nlp.NLPProcessor;
import setting.OAuthSet;
import th.Main;

public class StreamingThread extends Thread {

    private final JsonParser parser = new JsonParser();
    private final BlockingQueue<String> msgQueue;
    private final BasicClient client;
    private Location location = null;
    private DatabaseOperation db = null;
    private NLPProcessor nlp = null;
    private final int HEARTBEART_INTERVAL = 2000;

    public StreamingThread(OAuthSet oauth, String dbAddr, LocationWithToken locationWithToken,
                           String dbUser, String dbPassword, NLPProcessor nlp) {
        this.location = locationWithToken.getLocation();

        List<Location> locations = Arrays.asList(location);
        List<String> languages = Arrays.asList("en");

        msgQueue = new LinkedBlockingQueue<>(10000);

        Hosts hosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();

        endpoint.locations(locations);
        endpoint.languages(languages);

        Authentication auth = new OAuth1(oauth.getConsumerKey(),
                oauth.getConsumerSecret(),
                oauth.getAccessToken(),
                oauth.getAccessTokenSecret());

        ClientBuilder builder = new ClientBuilder()
                .retries(10)
                .name("StreamingCrawler")
                .hosts(hosts)
                .authentication(auth)
                .endpoint(endpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        client = builder.build();

        db = new CouchOperation(dbAddr, locationWithToken.getToken(), dbUser, dbPassword);

        this.nlp = nlp;
    }

    @Override
    public void run() {
        Runnable isAlive = () -> {
            while (true) {
                try {
                    if (isDone()) {
                        ErrorHandler.process(this.getClass(), new Exception("Streaming crawler is shutting down."), true,
                        "Authentication error or network error. Please check StreamingThread settings.");
                    }
                    Thread.sleep(HEARTBEART_INTERVAL);
                } catch (InterruptedException ex) {
                    Logger.getLogger(StreamingThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        new Thread(isAlive).start();

        client.connect();
        while (!client.isDone()) {
            try {
                String msg = msgQueue.take();
                JsonObject tweet = parser.parse(msg).getAsJsonObject();

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
                tweet.addProperty("food_relevant", Main.foodRelevantJudge.judge(tweet));

                db.insertTweet(tweet);
            } catch (InterruptedException ex) {
                ErrorHandler.process(this.getClass(), ex, false,
                        "InterruptedException occurred in StreamingThread. Please check StreamingThread settings.");
            }
        }
        client.stop();
    }

    public boolean isDone() {
        if (client != null) {
            return client.isDone();
        }
        return false;
    }
}

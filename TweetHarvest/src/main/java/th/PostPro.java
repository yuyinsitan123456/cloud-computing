package th;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import crawler.SearchThread;
import crawler.StreamingThread;
import exhandle.ErrorHandler;
import org.apache.commons.cli.*;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbInfo;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.DocumentConflictException;
import pre.food.FoodRelevantJudge;
import pre.nlp.NLPProcessor;
import pre.nsw.NswGreenspaceJudge;
import pre.nsw.NswPostcodeJudge;
import pre.vic.VicFoodJudge;
import pre.vic.VicGreenspaceJudge;
import pre.vic.VicPostcodeJudge;
import pre.vic.VicPostcodes;
import setting.CommandLineProcess;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostPro {
    private static final int COUNT_PER_QUERY = 500;
    private static boolean isUpdating = true;

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("from", true, "DB name of the source");
        options.addOption("to", true, "DB name of the destination");
        options.addOption("dbAddr", true, "Database address");
        options.addOption("dbUser", true, "Database username");
        options.addOption("dbPass", true, "Database password");
        options.addOption("help", false, "help");

        try {
            CommandLine line = parser.parse(options, args);
            String fromDbName = line.getOptionValue("from", "melbourne");
            String toDbName = line.getOptionValue("to", "melbourne");
            String dbAddr = line.getOptionValue("dbAddr", "127.0.0.1");
            String dbUser = line.getOptionValue("dbUser", "couchdb");
            String dbPass = line.getOptionValue("dbPass", "123456");

            NLPProcessor nlp = new NLPProcessor(dbAddr, dbUser, dbPass);

            VicPostcodes vicPostcodes = new VicPostcodes(dbAddr, dbUser, dbPass);
            VicPostcodeJudge vicPostcodeJudge = new VicPostcodeJudge(
                    vicPostcodes.getCords(), vicPostcodes.getPostID());
            NswPostcodeJudge nswPostcodeJudge = new NswPostcodeJudge(dbAddr, dbUser, dbPass);
            VicGreenspaceJudge vicGreenspaceJudge = new VicGreenspaceJudge(dbAddr, dbUser, dbPass);
            NswGreenspaceJudge nswGreenspaceJudge = new NswGreenspaceJudge(dbAddr, dbUser, dbPass);
            VicFoodJudge vicFoodJudge = new VicFoodJudge(dbAddr, dbUser, dbPass);
            FoodRelevantJudge foodRelevantJudge = new FoodRelevantJudge();

            /* Read */
            CouchDbProperties fromDbProperties = new CouchDbProperties()
                    .setDbName(fromDbName)
                    .setCreateDbIfNotExist(true)
                    .setProtocol("http")
                    .setHost(dbAddr)
                    .setPort(5984)
                    .setUsername(dbUser)
                    .setPassword(dbPass)
                    .setMaxConnections(100)
                    .setConnectionTimeout(0);

            CouchDbClient fromDb = new CouchDbClient(fromDbProperties);
            CouchDbClient toDb = null;
            CouchDbInfo dbInfo = fromDb.context().info();
            long docCount = dbInfo.getDocCount();

            /* If fromDb != toDb */
            if (!fromDbName.equals(toDbName)) {
                CouchDbProperties toDbProperties = new CouchDbProperties()
                        .setDbName(toDbName)
                        .setCreateDbIfNotExist(true)
                        .setProtocol("http")
                        .setHost(dbAddr)
                        .setPort(5984)
                        .setUsername(dbUser)
                        .setPassword(dbPass)
                        .setMaxConnections(100)
                        .setConnectionTimeout(0);
                toDb = new CouchDbClient(toDbProperties);
                isUpdating = false;
            }

            /* Update or Save */
            for (int i = 0; i <= docCount / COUNT_PER_QUERY; i++) {
                List<JsonObject> allDocs = fromDb.view("_all_docs")
                        .limit((int) COUNT_PER_QUERY)
                        .skip(COUNT_PER_QUERY * i)
                        .includeDocs(true)
                        .query(JsonObject.class);

                int processedCount = 0;

                for (JsonObject tweet : allDocs) {
                    nlp.addSentiment(tweet);

                    String vicPostcode = null;
                    String nswPostcode = null;
                    String vicGreenspace = null;
                    String nswGreenspace = null;
                    Integer vicFood = null;
                    if (tweet.has("coordinates") && tweet.get("coordinates").isJsonObject()) {
                        JsonArray coords = tweet.getAsJsonObject("coordinates").getAsJsonArray("coordinates");
                        double longitude = coords.get(0).getAsDouble();
                        double latitude = coords.get(1).getAsDouble();
                        vicPostcode = vicPostcodeJudge.judge(longitude, latitude);
                        nswPostcode = nswPostcodeJudge.judge(longitude, latitude);
                        vicGreenspace = vicGreenspaceJudge.judge(longitude, latitude);
                        nswGreenspace = nswGreenspaceJudge.judge(longitude, latitude);
                        vicFood = vicFoodJudge.judge(longitude, latitude);
                    }
                    tweet.addProperty("vic_postcode", vicPostcode);
                    tweet.addProperty("nsw_postcode", nswPostcode);
                    tweet.addProperty("vic_greenspace", vicGreenspace);
                    tweet.addProperty("nsw_greenspace", nswGreenspace);
                    tweet.addProperty("vic_food", vicFood);
                    tweet.addProperty("food_relevant", foodRelevantJudge.judge(tweet));

                    updateOrSave(fromDb, toDb, tweet);

                    ++processedCount;
                }

                System.out.println("=======  Phase " + i + " : " + processedCount + " processed  =======");
            }


        } catch (ParseException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("th.PostPro", options);
        }
    }

    public static void updateOrSave(CouchDbClient fromDb, CouchDbClient toDb, JsonObject tweet) {
        if (isUpdating) {
            fromDb.update(tweet);
        } else {
            try {
                String id_str = tweet.get("id_str").getAsString();

                /* Insert only when the tweet is a new one */
                tweet.addProperty("_id", id_str);
                tweet.remove("_rev");
                toDb.save(tweet);
            } catch (DocumentConflictException ex) {
                // Regular conclusion of duplication occurrence, so do nothing
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

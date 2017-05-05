package pp;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.google.gson.JsonObject;
import org.apache.commons.cli.*;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbInfo;
import org.lightcouch.CouchDbProperties;
import pre.NLPProcessor;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        int COUNT_PER_QUERY = 100;
        String postProcessDbName = "melbourne";
        String dbAddr = "127.0.0.1";
        String dbUser = "couchdb";
        String dbPassword = "123456";

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("dbName", true, "Database name");
        try {
            CommandLine line = parser.parse(options, args);
            postProcessDbName = line.getOptionValue("dbName", "melbourne");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        NLPProcessor nlp = new NLPProcessor(dbAddr, dbUser, dbPassword);

        CouchDbProperties properties = new CouchDbProperties()
                .setDbName(postProcessDbName)
                .setCreateDbIfNotExist(true)
                .setProtocol("http")
                .setHost(dbAddr)
                .setPort(5984)
                .setUsername(dbUser)
                .setPassword(dbPassword)
                .setMaxConnections(100)
                .setConnectionTimeout(0);

        CouchDbClient db = new CouchDbClient(properties);
        CouchDbInfo dbInfo = db.context().info();
        long docCount = dbInfo.getDocCount();

        for (int i = 0; i <= docCount / COUNT_PER_QUERY; i++) {
            List<JsonObject> allDocs = db.view("_all_docs")
                    .limit((int) COUNT_PER_QUERY)
                    .skip(COUNT_PER_QUERY * i)
                    .includeDocs(true)
                    .query(JsonObject.class);

            int processedCount = 0;

            for (JsonObject o : allDocs) {
                if (!o.has("sentiment")) {
                    nlp.addSentiment(o);
                    db.update(o);
                    ++processedCount;
                }
            }

            System.out.println("=======  Phase " + i + " : " + processedCount + " processed  =======");
        }
    }
}
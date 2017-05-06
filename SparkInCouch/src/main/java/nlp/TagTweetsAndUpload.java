package nlp;


import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.lm.NGramProcessLM;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbInfo;
import org.lightcouch.CouchDbProperties;

import java.io.*;
import java.util.List;

public class TagTweetsAndUpload {
    public static final int COUNT_PER_QUERY = 1000;
    public static void main(String[] args) throws IOException {
        CouchInsertor couchInsertor = new CouchInsertor("tagged", "couchdb", "123456");

        TrainingSetPrep t = new TrainingSetPrep();
        JsonParser parser = new JsonParser();

        String[] mCategories = {"positive", "negative"};
        int nGram = 2;
        DynamicLMClassifier<NGramProcessLM> mClassifier = DynamicLMClassifier.createNGramProcess(mCategories, nGram);

        CouchDbProperties properties = new CouchDbProperties()
                .setDbName("sydney")
                .setCreateDbIfNotExist(true)
                .setProtocol("http")
                .setHost("127.0.0.1")
                .setPort(5984)
                .setMaxConnections(100)
                .setUsername("couchdb")
                .setPassword("123456")
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

            System.out.println("=======  " + i + "  =======");

            for (JsonObject o : allDocs) {
                JsonObject result = t.addSentimentTag(o);
                if (result != null) {
                    System.out.println(result.get("text").getAsString());
                    result.addProperty("_id", o.get("_id").getAsString());

                    couchInsertor.insert(result);

                    Classified<CharSequence> classified = new Classified<CharSequence>(result.get("text").getAsString(), new Classification(result.get("sentiment").getAsString()));
                    mClassifier.handle(classified);
                }
            }
        }

        /*
        FileOutputStream fileOut = new FileOutputStream("/Users/nek/Desktop/mClassifier.model");
        ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
        mClassifier.compileTo(objOut);
        */
    }
}

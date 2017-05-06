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

import java.util.List;

public class DownloadAndTrain {
    public static void main(String[] args) {
        TrainingSetPrep t = new TrainingSetPrep();
        JsonParser parser = new JsonParser();

        String[] mCategories = {"positive", "negative"};
        int nGram = 2;
        DynamicLMClassifier<NGramProcessLM> mClassifier = DynamicLMClassifier.createNGramProcess(mCategories, nGram);

        CouchDbProperties properties = new CouchDbProperties()
                .setDbName("tagged")
                .setCreateDbIfNotExist(true)
                .setProtocol("http")
                .setHost("127.0.0.1")
                .setPort(5984)
                .setMaxConnections(100)
                .setUsername("couchdb")
                .setPassword("123456")
                .setConnectionTimeout(0);

        CouchDbClient db = new CouchDbClient(properties);

        List<JsonObject> allDocs = db.view("_all_docs")
                .includeDocs(true)
                .query(JsonObject.class);
        for (JsonObject o : allDocs) {
            try {
                Classified<CharSequence> classified = new Classified<CharSequence>(o.get("text").getAsString(), new Classification(o.get("sentiment").getAsString()));
                mClassifier.handle(classified);
            } catch (Exception e){

            }
        }

        Classification classification = mClassifier.classify("I love it");
        System.out.println(classification.bestCategory());

        classification = mClassifier.classify("I hate it sorry anxiety shame");
        System.out.println(classification.bestCategory());

        classification = mClassifier.classify("anxiety");
        System.out.println(classification.bestCategory());

        classification = mClassifier.classify("I am getting tired of this..");
        System.out.println(classification.bestCategory());
    }
}

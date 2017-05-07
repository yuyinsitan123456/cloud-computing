package pre.nlp;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.lm.NGramProcessLM;
import com.google.gson.JsonObject;
import db.CouchOperation;
import db.DatabaseOperation;

import java.util.List;

public class NLPProcessor {
    private TrainingSetPrep tsp = null;
    private DatabaseOperation db = null;
    private DynamicLMClassifier<NGramProcessLM> mClassifier = null;

    public NLPProcessor(String dbAddr, String dbUser, String dbPassword) {
        tsp = new TrainingSetPrep();

        String[] mCategories = {"positive", "negative"};
        int nGram = 2;
        mClassifier = DynamicLMClassifier.createNGramProcess(mCategories, nGram);

        System.out.println("Start to download tagged tweets from DB.");

        db = new CouchOperation(dbAddr, "tagged", dbUser, dbPassword);
        List<JsonObject> taggedTweets = db.getAllDocs();

        System.out.println("Downloading finished. Start training.");

        for (JsonObject tweet : taggedTweets) {
            try {
                Classified<CharSequence> classified = new Classified<CharSequence>(tweet.get("text").getAsString(), new Classification(tweet.get("sentiment").getAsString()));
                mClassifier.handle(classified);
            } catch (Exception e){
                // Some of the documents there may not be a tagged tweet. Just skip them.
            }
        }
    }

    public JsonObject addSentiment(JsonObject tweet) {
        JsonObject tag = tsp.addSentimentTag(tweet);
        if (tag == null) {
            String sentiment = mClassifier.classify(tweet.get("text").getAsString()).bestCategory();
            tweet.addProperty("sentiment", sentiment);
            return tweet;
        } else {
            tweet.addProperty("sentiment", tag.get("sentiment").getAsString());
            return tweet;
        }
    }
}

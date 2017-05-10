package nlp;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.util.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbInfo;
import org.lightcouch.CouchDbProperties;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DownloadAndTrain {
    public static void main(String[] args) throws IOException {
        TrainingSetPrep t = new TrainingSetPrep();
        JsonParser parser = new JsonParser();

        String[] mCategories = {"negative", "positive"};
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
            } catch (Exception e) {

            }
        }


        Classification classification = mClassifier.classify("I am sad shit you");
        System.out.println(classification.bestCategory());

        /*
        classification = mClassifier.classify("I hate it sorry anxiety shame");
        System.out.println(classification.bestCategory());

        classification = mClassifier.classify("anxiety");
        System.out.println(classification.bestCategory());

        classification = mClassifier.classify("I am getting tired of this..");
        System.out.println(classification.bestCategory());
        */

        /*
        File mPolarityDir = new File("/Users/nek/Desktop/CCCAsg2", "txt_sentoken");

        System.out.println("\nEvaluating.");
        int numTests = 0;
        int numCorrect = 0;
        for (int i = 0; i < mCategories.length; ++i) {
            String category = mCategories[i];
            File file = new File(mPolarityDir, mCategories[i]);
            File[] trainFiles = file.listFiles();
            for (int j = 0; j < trainFiles.length; ++j) {
                File trainFile = trainFiles[j];

                String review = Files.readFromFile(trainFile, "ISO-8859-1");
                ++numTests;
                Classification classification
                        = mClassifier.classify(review);
                if (classification.bestCategory().equals(category)) {
                    System.out.println(trainFile.getAbsolutePath());
                    ++numCorrect;
                }
            }
        }
        System.out.println("  # Test Cases=" + numTests);
        System.out.println("  # Correct=" + numCorrect);
        System.out.println("  % Correct="
                + ((double) numCorrect) / (double) numTests);
                */
    }
}

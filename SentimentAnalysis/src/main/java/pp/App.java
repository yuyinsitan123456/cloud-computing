package pp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.util.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Hello world!
 *
 */
public class App {
	final static int size = 3;
	// File mPolarityDir;
	String[] mCategories = { "positive", "negative" };
	DynamicLMClassifier<NGramProcessLM> mClassifier;
	static String[] test = new String[size];
	static List<JsonObject> objs;

	App() {
		int nGram = 2;
		mClassifier = DynamicLMClassifier.createNGramProcess(mCategories, nGram);
	}

	void train() throws IOException {
		int numTrainingCases = 0;
		int numTrainingChars = 0;

		System.out.println("\nTraining.");
		for (int i = 0; i < mCategories.length; ++i) {
			String category = mCategories[i];
			ReadFiles read = new ReadFiles("C:/Users/ChangLong/Desktop/cloud/test.txt");
			Classification classification = new Classification(category);
			String line = read.getLine();
			while (line != null) {
				// if (isTrainingFile(trainFile)) {
				if (category.equals(line.split("#")[1])) {
					++numTrainingCases;
					String review = line.split("#")[0];
					numTrainingChars += review.length();
					Classified<CharSequence> classified = new Classified<CharSequence>(review, classification);
					mClassifier.handle(classified);
				}
				line = read.getLine();
				if (line == null) {
					break;
				}
				// }
			}
		}
		System.out.println("  # Training Cases=" + numTrainingCases);
		System.out.println("  # Training Chars=" + numTrainingChars);
	}

	void evaluate(String[] test) throws IOException {
		System.out.println("\nEvaluating.");
		int numTests = 0;
		int numCorrect = 0;

		for (int j = 0; j < test.length; ++j) {

			String review = test[j].trim().toLowerCase();
			++numTests;
			Classification classification = mClassifier.classify(review);
			System.out.println(classification.bestCategory());

		}

	}

	public static void couchDBconnection() {
		CouchDbProperties properties = new CouchDbProperties().setDbName("melbourne").setCreateDbIfNotExist(true)
				.setProtocol("http").setHost("127.0.0.1").setPort(5984).setMaxConnections(100).setConnectionTimeout(0);

		CouchDbClient dbClient = new CouchDbClient(properties);
		objs = dbClient.view("_all_docs").includeDocs(true).skip(30).limit(size).query(JsonObject.class);
	}

	public void initialize(){
		GetlabeledTweet1 gt = new GetlabeledTweet1();
    	
        SparkConf sparkConf = new SparkConf().setAppName("spark cloudant connecter").setMaster("local");
        sparkConf.set("spark.streaming.concurrentJobs", "30");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        SQLContext sqlContext = new SQLContext(sc);

        System.out.print("initialization successfully");

        Dataset<Row> st = sqlContext.read().format("com.cloudant.spark")
                //.option("view", "_design/test/_view/createdAt")
                .option("cloudant.protocol", "https")
                .option("cloudant.host", "wenhaoz3.cloudant.com")
                .option("cloudant.username", "wenhaoz3")
                .option("cloudant.password", "38383838")
                .load("melbourne");
       // String file3 = "C:/Users/ChangLong/Desktop/cloud/test.txt";
        JavaRDD<Row> rdd = st.toJavaRDD();
        //JavaRDD<String> labeledTweet = rdd.map((row r));
        
       // String file2 = "C:/JAVA/mywork/workspace/cloud/src/smallTwitter.json";
    	//String file2 = "C:/Users/ChangLong/Desktop/cloud/aaa.txt";
		
		//gt.readStream(rdd.toString());
    }

	public static void main(String[] args) throws FileNotFoundException, IOException {
		// CouchDbClient dbClient2 = new CouchDbClient("melbourne", true,
		// "http", "127.0.0.1", 5984, "username", "secret");
		// couchDBconnection();
		App app = new App();
		app.initialize();
		/*
		 * System.out.println("totaltweets: "+gt.getTotalTweets());
		 * System.out.println("positivetweets: "+gt.getPositiveTweet());
		 * System.out.println("negativetweets: "+gt.getNegativeTweet());
		 */
		/*
		 * for (int i=0;i<objs.size();i++) { JsonObject obj = objs.get(i);
		 * System.out.println(obj.get("text").toString()); test[i] =
		 * obj.get("text").getAsString(); }
		 */
/*		test[0] = "I am so happy right now.";
		test[1] = "I hate do the homework, it's fucked.";
		test[2] = "It's rainning, feel unhappy";
		try {
			App app = new App();
			app.train();
			app.evaluate(test);
		} catch (Throwable t) {
			System.out.println("Thrown: " + t);
			t.printStackTrace(System.out);
		}*/
	}

}

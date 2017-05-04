package pp;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.spark.api.java.function.Function;

public class GatherTweets implements Function<String, String>{
	GetlabeledTweet1 gt = new GetlabeledTweet1();
	public String call(String s) throws FileNotFoundException, IOException { return gt.readStream(s); }
}

package pp;

import java.io.*;
import java.util.List;

import org.apache.spark.api.java.function.Function;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class GetLabelledTweet {
    String[] positivehashtags = {"mentalhealth", "recovery", "courage", "courageous", "empowerment", "empower",
            "empoweryourself", "motivationalquotes", "positivethinking", "positivequotes", "positivity",
            "positivevibrations", "onelove", "onelife", "oneway", "onelive", "bless", "likeforlike", "support", "proud",
            "positive", "blessed", "peace", "beauty", "finest", "fashion", "motivation", "happy", "lovelife", "passion",
            "love", "mentalhealthawareness", "hardworkingkids", "youthpower", "promotinghealth", "helpothers",
            "healthylife", "hello", "hi", "goodday", "shouldbegood", "firstmeet", "smile", "lovemyfriends", "beautiful",
            "homesweethome", "great", "maybeparty", "newme", "betterme", "kiss", "believe", "positivemind",
            "positivethought", "positivepeople", "blondes", "mylove", "happiness", "findyoursoul", "findyoursmile",
            "spiritual", "positivevibetribe", "affirmation", "harmony", "mygym", "motivate", "strength", "focus",
            "focused", "beastmode", "gym", "selfmotivated", "positiveattitude", "training", "trainingpartner",
            "miracles", "grow", "weighttraining", "gettingitdone", "nice", "indipendent", "journey", "stayhealthy",
            "getstrong", "active", "goodhabits", "goodjob", "excercise", "excellent", "inspiration", "optimism",
            "cleanse", "relax", "inspire", "spirit", "healing", "energies"};
    String[] negativehagstags = {"negative", "police", "drugtest", "trueface", "followforfollow", "negativfilm",
            "negativity", "eccentric", "restarted", "chemistry", "people", "nope", "sentiment", "latergram", "drawing",
            "fuck", "fucking", "fucked", "shit", "damn", "monday", "entrepreneurs", "entrepreneurship", "keeplearning",
            "limitless", "loyaltyiseverything", "selfish", "determinacao", "sleep", "wakeup", "depression", "suicide",
            "anxiety", "wanttodie", "selfharm", "sadness", "unhappy", "selfinjury", "worthless", "goals", "lifegoals",
            "luggage", "pay", "unnecessary", "money", "cost", "extra", "shouldnt", "mediumformat", "mediumformatfilm",
            "geometric", "minimalzine", "learnminimalism", "minimalism", "minimalmood"};

    public String readStream(String tweet) throws FileNotFoundException, IOException {
        RemoveNoise rn = new RemoveNoise();
        StringBuffer sb;

        JsonReader reader = new JsonReader(new StringReader(tweet));
        reader.setLenient(true);
        reader.beginObject();
        String textTemp = "";

        while (reader.hasNext()) {

            String s22 = reader.nextName();
            if (s22.equals("text")) {

                textTemp = reader.nextString().toLowerCase();

                if (textTemp.contains("🐼") || textTemp.contains("😄") || textTemp.contains("😊")
                        || textTemp.contains("😍") || textTemp.contains("😝") || textTemp.contains("😃")
                        || textTemp.contains("🤣") || textTemp.contains("🙂")) {

                    sb = new StringBuffer();
                    String[] temp = textTemp.split("\\s+");
                    temp = rn.removeNoise(temp);
                    for (int i = 0; i < temp.length; i++) {
                        sb.append(temp[i]);
                        sb.append(" ");
                    }
                    textTemp = sb.toString();

                    return textTemp + "  #positive";
                } else if (textTemp.contains("😡") || textTemp.contains("😫") || textTemp.contains("😣")
                        || textTemp.contains("😤") || textTemp.contains("☹️") || textTemp.contains("😒")) {
                    sb = new StringBuffer();

                    String[] temp = textTemp.split("\\s+");
                    temp = rn.removeNoise(temp);
                    for (int i = 0; i < temp.length; i++) {
                        sb.append(temp[i]);
                        sb.append(" ");
                    }
                    textTemp = sb.toString();
                    return textTemp + "  #negative";
                }
            } else if (!s22.equals("entities") && !s22.equals("text")) {
                reader.skipValue();
            } else if (s22.equals("entities")) {
                JsonToken check2 = reader.peek();
                if (check2 == JsonToken.NULL) {
                    reader.skipValue();
                } else {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String s222 = reader.nextName();
                        if (!s222.equals("hashtags")) {
                            reader.skipValue();
                        } else if (s222.equals("hashtags")) {
                            JsonToken check3 = reader.peek();
                            if (check3 == JsonToken.NULL) {
                                reader.skipValue();
                            } else {
                                reader.beginArray();
                                int totalScore = 0;
                                while (reader.hasNext()) {
                                    reader.beginObject();
                                    while (reader.hasNext()) {
                                        String s2222 = reader.nextName();
                                        if (!s2222.equals("text")) {
                                            reader.skipValue();
                                        } else if (s2222.equals("text")) {
                                            String hashtagss = reader.nextString();
                                            for (int i = 0; i < positivehashtags.length; i++) {
                                                if (positivehashtags[i].equals(hashtagss)) {
                                                    totalScore = totalScore + 1;
                                                }
                                            }
                                            for (int i = 0; i < negativehagstags.length; i++) {
                                                if (negativehagstags[i].equals(hashtagss)) {
                                                    totalScore = totalScore - 1;
                                                }
                                            }

                                        }
                                    }
                                    reader.endObject();
                                }
                                reader.endArray();

                                sb = new StringBuffer();
                                String[] temp = textTemp.split("\\s+");
                                temp = rn.removeNoise(temp);
                                for (int i = 0; i < temp.length; i++) {
                                    sb.append(temp[i]);
                                    sb.append(" ");
                                }
                                textTemp = sb.toString();
                                if (totalScore > 0 && !textTemp.trim().equals("")) {
                                    return textTemp + "  #positive";
                                } else if (totalScore < 0 && !textTemp.trim().equals("")) {
                                    return textTemp + "  #negative";
                                }
                            }
                        }
                    }
                    reader.endObject();
                }
            }
        }
        reader.endObject();

        return "";
    }


    public static void main(String[] args) {
        GetLabelledTweet glt = new GetLabelledTweet();
        try {
            BufferedReader br = new BufferedReader(new FileReader("/Users/"));
String thisLine ="";
            while ((thisLine = br.readLine()) != null) {
                System.out.println(thisLine);
            }
            glt.readStream("fuckyou");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

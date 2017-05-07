package pre.nlp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Set;

public class TrainingSetPrep {
    private final RemoveNoise removeNoise = new RemoveNoise();

    private final Set<String> positiveHashtags = new HashSet<String>();
    private final Set<String> negativeHashtags = new HashSet<String>();

    public TrainingSetPrep() {
        String[] pos = {"mentalhealth", "recovery", "courage", "courageous", "empowerment", "empower",
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
        for (String p : pos) {
            positiveHashtags.add(p);
        }

        String[] neg = {"negative", "police", "drugtest", "trueface", "followforfollow", "negativfilm",
                "negativity", "eccentric", "restarted", "chemistry", "people", "nope", "sentiment", "latergram", "drawing",
                "fuck", "fucking", "fucked", "shit", "damn", "monday", "entrepreneurs", "entrepreneurship", "keeplearning",
                "limitless", "loyaltyiseverything", "selfish", "determinacao", "sleep", "wakeup", "depression", "suicide",
                "anxiety", "wanttodie", "selfharm", "sadness", "unhappy", "selfinjury", "worthless", "goals", "lifegoals",
                "luggage", "pay", "unnecessary", "money", "cost", "extra", "shouldnt", "mediumformat", "mediumformatfilm",
                "geometric", "minimalzine", "learnminimalism", "minimalism", "minimalmood"};
        for (String n : neg) {
            negativeHashtags.add(n);
        }
    }

    public JsonObject addSentimentTag(JsonObject o) {
        String text = o.get("text").getAsString().toLowerCase();

        if (text.contains("🐼") || text.contains("😄") || text.contains("😊")
                || text.contains("😍") || text.contains("😝") || text.contains("😃")
                || text.contains("🤣") || text.contains("🙂")) {
            return generateJson(removeNoise.removeNoise(text), "positive");
        }
        if (text.contains("😡") || text.contains("😫") || text.contains("😣")
                || text.contains("😤") || text.contains("☹️") || text.contains("😒")) {
            return generateJson(removeNoise.removeNoise(text), "negative");
        }

        if (o.has("entities")) {
            o = o.get("entities").getAsJsonObject();
            if (o.has("hashtags")) {
                JsonArray jsonArray = o.get("hashtags").getAsJsonArray();
                if (jsonArray.size() != 0) {
                    int totalScore = 0;
                    for (JsonElement e : jsonArray) {
                        JsonObject tagObj = e.getAsJsonObject();
                        String tag = tagObj.get("text").getAsString();
                        if (positiveHashtags.contains(tag)) ++totalScore;
                        if (negativeHashtags.contains(tag)) --totalScore;
                    }

                    text = removeNoise.removeNoise(text);
                    if (totalScore > 0 && !text.trim().equals("")) return generateJson(text, "positive");
                    if (totalScore < 0 && !text.trim().equals("")) return generateJson(text, "negative");
                }
            }
        }

        /* cannot make judgement (neutral) */
        return null;
    }

    private JsonObject generateJson(String text, String sentiment) {
        JsonObject o = new JsonObject();
        o.addProperty("text", text);
        o.addProperty("sentiment", sentiment);
        return o;
    }
}

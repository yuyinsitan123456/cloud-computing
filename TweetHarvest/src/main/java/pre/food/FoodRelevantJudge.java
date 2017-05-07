package pre.food;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashSet;
import java.util.Set;

public class FoodRelevantJudge {
    private Set<String> foodHashtagsSet = new HashSet<>();
    private final String[] foodHashtagsList = {"healthbox", "health", "food", "sporcu", "sporcuyemekleri", "fit",
            "fitkalmak", "diyet", "diyetisyen", "beslenmeçantası", "beslenmekutusu", "istanbul", "mutfak", "foodblogger",
            "foodblog", "yemektarifi", "yemekyemek", "sanfrancisco", "bored", "brunett", "moscow", "pretty", "funny",
            "bayarea", "losangeles", "vegas", "cutie", "cute", "smile", "fitgirl", "amsterdam", "bestoftheday", "photooftheday",
            "outfit", "amazing", "look", "instadaily", "picoftheday", "chips", "sandwich", "lunch", "foodpornkids",
            "cookingclass", "kidscooking", "minichefs", "cook", "bake", "kitchen", "chef", "eat", "statenisland", "joinus",
            "casabelvedere", "robevecchie", "foodporn", "butter", "spicyfood", "spicy", "instagram", "nochill",
            "funnymemes", "meme", "memes", "dankmemes", "bruh", "cheflife",
            "goodeats", "fooddinner", "risotto", "milanese", "milano", "italian", "italianfood", "italianfoodbloggers",
            "foodie", "foodphotooftheday", "foodlover", "foodshare", "overseaslife", "fooddiary ",
            "foodgasm", "donut", "donuts", "tłustyczwartek", "pączki", "instafood", "essen", "pastel", "sweet", "", "",
            "tortinoalcioccolatoconcuoremorbido", "tortino", "chocolate", "peperoncino", "vaniglia", "icecream",
            "details", "souffle", "pitti", "torreacenaia", "hotandcold", "petali", "passito", "kingfisher", "toscanaamoremi",
            "india", "gurgoan", "swag", "spaghetti", "steak", "noodles", "dumplings", "cuisine", "sharjah", "coral", "beach", "buffet",
            "chinese", "chinesefood", "mydubai", "foodgram", "yummy", "yum", "cheers", "desserts", "dessertanddrinks",
            "dessertporn", "delicious", "instaeat", "drinks", "eatgram",
            "peaunat", "peanutlove", "peanutbutter", "caramelbars", "girlscoutcookies"
    };

    public FoodRelevantJudge() {
        for (String foodHashtag : foodHashtagsList) {
            foodHashtagsSet.add(foodHashtag.toLowerCase());
        }
    }

    public boolean judge(JsonObject tweet) {
        if (tweet.has("entities")) {
            tweet = tweet.get("entities").getAsJsonObject();
            if (tweet.has("hashtags")) {
                JsonArray jsonArray = tweet.get("hashtags").getAsJsonArray();
                if (jsonArray.size() != 0) {
                    for (JsonElement e : jsonArray) {
                        JsonObject tagObj = e.getAsJsonObject();
                        String tagInLowerCase = tagObj.get("text").getAsString().toLowerCase();
                        if (foodHashtagsSet.contains(tagInLowerCase)) return true;
                    }
                }
            }
        }
        return false;
    }
}

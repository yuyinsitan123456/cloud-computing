package aurin.food;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nlp.CouchInsertor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReadFileAndUpload {

    public static void main(String[] args) {
        /* Read file */
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("/Users/nek/Desktop/CCCAsg2/AURIN/Melbourne/City_of_Melbourne_CLUE_Employment_By_Industry_2010.json"))) {
            String str = null;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileContent = sb.toString();

        /* Ready to upload */
        CouchInsertor db = new CouchInsertor("aurin/food", "couchdb", "123456");

        /* Convert fileContent into big JsonObject */
        JsonObject file = new JsonParser().parse(fileContent).getAsJsonObject();
        JsonArray features = file.getAsJsonArray("features");

        /* Upload! */
        for (JsonElement foodElement : features) {
            JsonObject foodJson = foodElement.getAsJsonObject();

            int food_and_beverage_services = 0;
            if (foodJson.getAsJsonObject("properties").get("food_and_beverage_services").isJsonPrimitive()) {
                food_and_beverage_services = foodJson.getAsJsonObject("properties").get("food_and_beverage_services").getAsInt();
            }
            if (food_and_beverage_services != 0) {
                db.insert(foodJson);
            }
        }
    }
}

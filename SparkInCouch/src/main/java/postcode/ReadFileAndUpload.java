package postcode;

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
        try (BufferedReader br = new BufferedReader(new FileReader("/Users/nek/Desktop/CCCAsg2/AURIN/Melbourne/PSMA_Postcodes__Polygon___August_2016_.json"))) {
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
        CouchInsertor db = new CouchInsertor("vic_postcode", "couchdb", "123456");

        /* Convert fileContent into big JsonObject */
        JsonObject file = new JsonParser().parse(fileContent).getAsJsonObject();
        JsonArray features = file.getAsJsonArray("features");
        for (JsonElement postcodeElement : features) {
            JsonObject postcodeJson = postcodeElement.getAsJsonObject();

            db.insert(postcodeJson);

            /*
            JsonArray arr = postcodeJson.getAsJsonObject("geometry").getAsJsonArray("coordinates").get(0).getAsJsonArray();

            for (JsonElement coordsArr : arr) {
                System.out.println(coordsArr.getAsJsonArray().get(0).getAsJsonArray().get(0).getAsDouble());
            }

            System.out.println(postcodeJson.getAsJsonObject("properties").get("pc_pid").getAsString());
            */
        }
    }
}

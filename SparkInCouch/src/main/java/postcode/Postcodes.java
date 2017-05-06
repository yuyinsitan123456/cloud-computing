package postcode;

import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Postcodes {

    private List<JSONArray> Cords = new ArrayList<JSONArray>();//contains all cords in all postID
    private List<String> postID = new ArrayList<String>();//contains all postID

    public List<JSONArray> getCords() {
        return Cords;
    }

    public void setCords(List<JSONArray> cords) {
        Cords = cords;
    }

    public List<String> getPostID() {
        return postID;
    }

    public void setPostID(List<String> postID) {
        this.postID = postID;
    }

    public Postcodes(List<JsonObject> docs) {
        /* Adaptor */
        JSONArray polyCordsObj = new JSONArray();
        for (JsonObject obj : docs) {
            JSONObject orgJson = new JSONObject(obj.toString());
            polyCordsObj.put(orgJson);
        }

        // JSONArray polyCordsObj = dataJson.getJSONArray("features");
        int j = polyCordsObj.length();

        for (int i = 0; i < j; i++) {
            //getting cords
            JSONObject feature = polyCordsObj.getJSONObject(i);
            JSONObject cordsSet = feature.getJSONObject("geometry");
            JSONArray polyCords = cordsSet.getJSONArray("coordinates");
            Cords.add(polyCords);


            //getting postID
            JSONObject properties = feature.getJSONObject("properties");
            String postcode = properties.getString("postcode");
            postID.add(postcode);
        }
    }
}

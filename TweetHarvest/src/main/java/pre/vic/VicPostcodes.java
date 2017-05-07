package pre.vic;

import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import th.Main;

import java.util.ArrayList;
import java.util.List;

public class VicPostcodes {

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

    public VicPostcodes() {
        System.out.println("Start VicPostcode downloading.");

        CouchDbProperties dbProperties = new CouchDbProperties()
                .setDbName("vic_postcode")
                .setCreateDbIfNotExist(true)
                .setProtocol("http")
                .setHost("127.0.0.1")
                .setPort(5984)
                .setMaxConnections(100)
                .setUsername(Main.DB_USER)
                .setPassword(Main.DB_PASSWORD)
                .setConnectionTimeout(0);

        CouchDbClient db = new CouchDbClient(dbProperties);

        List<JsonObject> allDocs = db.view("_all_docs")
                .includeDocs(true)
                .query(JsonObject.class);

        System.out.println("VicPostcode Downloading finished.");


        /* Adaptor */
        JSONArray polyCordsObj = new JSONArray();
        for (JsonObject obj : allDocs) {
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

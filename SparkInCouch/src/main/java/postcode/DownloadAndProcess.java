package postcode;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.util.List;

public class DownloadAndProcess {

    public static void main(String[] args) {
        CouchDbProperties properties = new CouchDbProperties()
                .setDbName("vic_postcode")
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

        System.out.println("Downloading finished.");

        Postcodes postcodes = new Postcodes(allDocs);
        PostcodeJudge pj = new PostcodeJudge(postcodes.getCords(), postcodes.getPostID());

        System.out.println(pj.judge(144.9, -37.8667));
        System.out.println(pj.judge(144,
                -37));
        System.out.println(pj.judge(144,
                -37));
        System.out.println(pj.judge(144,
                -37));

    }
}

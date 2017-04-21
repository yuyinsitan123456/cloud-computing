package SC;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.twitter.hbc.httpclient.BasicClient;
import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.Response;

/**
 * An example class that handles the message queue, to be run as separate thread to ensure good performance.
 */
public class SimpleMessageHandler extends MessageHandler {
    private CouchDbClient dbClient = null;
    private Gson gson = new Gson();
    private JsonParser parser = new JsonParser();
    
    public SimpleMessageHandler(BlockingQueue<String> msgQueue, BasicClient client) {
        super(msgQueue, client);
        
        CouchDbProperties properties = new CouchDbProperties()
                .setDbName("sydney")
                .setCreateDbIfNotExist(true)
                .setProtocol("http")
                .setHost("127.0.0.1")
                .setPort(5984)
                .setMaxConnections(100)
                .setConnectionTimeout(0);

        dbClient = new CouchDbClient(properties);
    }

    public void run() {
        while (!client.isDone()) {
            try {
                String msg = msgQueue.take();
                // Utils.printInfo(new JSONObject(msg));
                // System.out.println(msg);
                
                JsonObject o = parser.parse(msg).getAsJsonObject();
                String id_str = o.get("id_str").getAsString();
                if (!dbClient.contains(id_str)) {
                    o.addProperty("_id", id_str);
                    Response response = dbClient.save(o);
                    System.out.println(response.toString());
                }    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        client.stop();
    }
}

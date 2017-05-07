import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static String dbAddr = "127.0.0.1";
    public static String dbUser = "couchdb";
    public static String dbPassword = "123456";

    private static final int INTERVAL = 30000;
    private static final GetDbView getDbView = new GetDbView();
    public static void main(String[] args) {
        Timer timer = new Timer(true);

        timer.schedule(generateTimerTask("mel", "view/sentiment"), 0, INTERVAL);
        timer.schedule(generateTimerTask("mel", "view/weekdaySentiment"), 0, INTERVAL);
        timer.schedule(generateTimerTask("mel", "view/postcode"), 0, INTERVAL);
        timer.schedule(generateTimerTask("mel", "view/postcodeSentiment"), 0, INTERVAL);
        timer.schedule(generateTimerTask("mel", "view/greenspace"), 0, INTERVAL);
        timer.schedule(generateTimerTask("mel", "view/greenspaceSentiment"), 0, INTERVAL);
        timer.schedule(generateTimerTask("mel", "view/food"), 0, INTERVAL);
        timer.schedule(generateTimerTask("mel", "view/foodSentiment"), 0, INTERVAL);
        timer.schedule(generateTimerTask("mel", "view/foodRelevant"), 0, INTERVAL);

        timer.schedule(generateTimerTask("syd", "view/sentiment"), 0, INTERVAL);
        timer.schedule(generateTimerTask("syd", "view/weekdaySentiment"), 0, INTERVAL);
        timer.schedule(generateTimerTask("syd", "view/postcode"), 0, INTERVAL);
        timer.schedule(generateTimerTask("syd", "view/postcodeSentiment"), 0, INTERVAL);
        timer.schedule(generateTimerTask("syd", "view/greenspace"), 0, INTERVAL);
        timer.schedule(generateTimerTask("syd", "view/greenspaceSentiment"), 0, INTERVAL);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    String url = "http://" + dbAddr + ":5984/_global_changes/_compact";
                    String encoding = Base64.getEncoder().encodeToString((dbUser + ":" + dbPassword).getBytes());

                    HttpClient c = HttpClientBuilder.create().build();
                    HttpPost p = new HttpPost(url);
                    p.setHeader("Content-Type", "application/json");
                    p.setHeader("Authorization", "Basic " + encoding);

                    HttpResponse response = c.execute(p);

                    BufferedReader rd = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));

                    StringBuffer result = new StringBuffer();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    System.out.println(result.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, INTERVAL * 20);

        while (true);
    }

    public static TimerTask generateTimerTask(String dbName, String viewName) {
        return new TimerTask() {
            @Override
            public void run() {
                getDbView.getDbView(dbName, viewName);
            }
        };
    }
}
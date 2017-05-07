import java.util.Timer;
import java.util.TimerTask;

public class Main {
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

        timer.schedule(generateTimerTask("syd", "view/sentiment"), 0, INTERVAL);
        timer.schedule(generateTimerTask("syd", "view/weekdaySentiment"), 0, INTERVAL);
        timer.schedule(generateTimerTask("syd", "view/postcode"), 0, INTERVAL);
        timer.schedule(generateTimerTask("syd", "view/postcodeSentiment"), 0, INTERVAL);
        timer.schedule(generateTimerTask("syd", "view/greenspace"), 0, INTERVAL);
        timer.schedule(generateTimerTask("syd", "view/greenspaceSentiment"), 0, INTERVAL);

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
package setting;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import th.Main;
import exhandle.MailNotification;
import geo.Locations;

public class CommandLineProcess {

    public static String CONSUMER_KEY_DEFAULT = "";
    public static String CONSUMER_SECRET_DEFAULT = "";
    public static String ACCESS_TOKEN_DEFAULT = "";
    public static String ACCESS_TOKEN_SECRET_DEFAULT = "";
    public static String LOCATION_TOKEN_DEFAULT = "melbourne";
    public static String DB_ADDRESS_DEFAULT = "127.0.0.1";
    public static String DB_USER_DEFAULT = "";
    public static String DB_PASSWORD_DEFAULT = "";
    public static String EMAIL_ADDRESS_DEFAULT = "";

    public static Options commandOptions() {
        Options options = new Options();
        options.addOption("ck", true, "Consumer key");
        options.addOption("cs", true, "Consumer secret");
        options.addOption("at", true, "Access token");
        options.addOption("ats", true, "Access token secret");
        options.addOption("lt", true, "Location token");
        options.addOption("dbAddr", true, "Database address");
        options.addOption("dbUser", true, "Database username");
        options.addOption("dbPass", true, "Database password");
        options.addOption("mail", true, "Email address");
        options.addOption("reset", false, "Reset mode for search crawler");
        options.addOption("nlp", false, "Start NLP_MODE pre-processor");

        options.addOption("help", false, "help");
        return options;
    }

    public static boolean optionsValidator(CommandLine line) {
        return !line.hasOption("help");
    }

    public static void processing(CommandLine line) {
        String consumerKey = line.getOptionValue("ck", CONSUMER_KEY_DEFAULT);
        String consumerSecret = line.getOptionValue("cs", CONSUMER_SECRET_DEFAULT);
        String accessToken = line.getOptionValue("at", ACCESS_TOKEN_DEFAULT);
        String accessTokenSecret = line.getOptionValue("ats", ACCESS_TOKEN_SECRET_DEFAULT);

        Main.OAUTH.setConsumerKey(consumerKey);
        Main.OAUTH.setConsumerSecret(consumerSecret);
        Main.OAUTH.setAccessToken(accessToken);
        Main.OAUTH.setAccessTokenSecret(accessTokenSecret);

        String lt = line.getOptionValue("lt", LOCATION_TOKEN_DEFAULT).toLowerCase();
        Main.LOCATION_WITH_TOKEN = Locations.getLocationWithToken(lt);

        Main.DB_ADDRESS = line.getOptionValue("dbAddr", DB_ADDRESS_DEFAULT);
        Main.DB_USER = line.getOptionValue("dbUser", DB_USER_DEFAULT);
        Main.DB_PASSWORD = line.getOptionValue("dbPass", DB_PASSWORD_DEFAULT);

        MailNotification.RECIPIENT = line.getOptionValue("mail", EMAIL_ADDRESS_DEFAULT);
        
        if (line.hasOption("reset")) {
            Main.SEARCH_CRAWLER_RESET_MODE = true;
            System.out.println("Reset mode is on!");
        }

        if (line.hasOption("nlp")) {
            Main.NLP_MODE = true;
            System.out.println("Start NLP_MODE pre-processor!");
        }
    }
}

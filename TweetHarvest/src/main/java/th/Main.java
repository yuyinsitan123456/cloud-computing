package th;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import setting.CommandLineProcess;
import geo.LocationWithToken;
import setting.OAuthSet;
import crawler.SearchThread;
import crawler.StreamingThread;

public class Main {

    public static final OAuthSet OAUTH = new OAuthSet();
    public static LocationWithToken LOCATION_WITH_TOKEN = null;
    public static String DB_ADDRESS = "";
    public static String DB_USER = "";
    public static String DB_PASSWORD = "";
    public static boolean SEARCH_CRAWLER_RESET_MODE = false;
    
    public static void main(String[] args) {        
        CommandLineParser parser = new DefaultParser();
        Options options = CommandLineProcess.commandOptions();
        
        try {
            CommandLine line = parser.parse(options, args);
            if (!CommandLineProcess.optionsValidator(line)) {
                throw new ParseException("Invalid command.");
            }
            CommandLineProcess.processing(line);

            new StreamingThread(OAUTH, DB_ADDRESS, LOCATION_WITH_TOKEN, DB_USER, DB_PASSWORD).start();
            new SearchThread(OAUTH, DB_ADDRESS, LOCATION_WITH_TOKEN, 
                    DB_USER, DB_PASSWORD, SEARCH_CRAWLER_RESET_MODE).start();
        } catch (ParseException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("th.Client", options);
        }
    }
}

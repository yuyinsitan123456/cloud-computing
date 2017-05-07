package th;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import pre.nlp.NLPProcessor;
import pre.nsw.NswGreenspaceJudge;
import pre.nsw.NswPostcodeJudge;
import pre.vic.VicFoodJudge;
import pre.vic.VicGreenspaceJudge;
import pre.vic.VicPostcodeJudge;
import pre.vic.VicPostcodes;
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
    public static boolean NLP_MODE = false;
    
    public static VicPostcodeJudge vicPostcodeJudge = null;
    public static NswPostcodeJudge nswPostcodeJudge = null;
    public static VicGreenspaceJudge vicGreenspaceJudge = null;
    public static NswGreenspaceJudge nswGreenspaceJudge = null;
    public static VicFoodJudge vicFoodJudge = null;
    
    public static void main(String[] args) {        
        CommandLineParser parser = new DefaultParser();
        Options options = CommandLineProcess.commandOptions();
        
        try {
            CommandLine line = parser.parse(options, args);
            if (!CommandLineProcess.optionsValidator(line)) {
                throw new ParseException("Invalid command.");
            }
            CommandLineProcess.processing(line);

            NLPProcessor nlp = null;
            if (NLP_MODE) nlp = new NLPProcessor(DB_ADDRESS, DB_USER, DB_PASSWORD);

            VicPostcodes vicPostcodes = new VicPostcodes();
            vicPostcodeJudge = new VicPostcodeJudge(
                    vicPostcodes.getCords(), vicPostcodes.getPostID());
            nswPostcodeJudge = new NswPostcodeJudge();
            vicGreenspaceJudge = new VicGreenspaceJudge();
            nswGreenspaceJudge = new NswGreenspaceJudge();
            vicFoodJudge = new VicFoodJudge();

            new StreamingThread(OAUTH, DB_ADDRESS, LOCATION_WITH_TOKEN,
                    DB_USER, DB_PASSWORD, nlp).start();
            new SearchThread(OAUTH, DB_ADDRESS, LOCATION_WITH_TOKEN,
                    DB_USER, DB_PASSWORD, SEARCH_CRAWLER_RESET_MODE, nlp).start();
        } catch (ParseException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("th.Main", options);
        }
    }
}

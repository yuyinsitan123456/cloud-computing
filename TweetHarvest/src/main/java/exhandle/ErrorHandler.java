package exhandle;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ErrorHandler {

    private static final MailNotification MAIL = new MailNotification();
    private static final HashMap<String, Boolean> HAPPENED = new HashMap<>();

    public static synchronized void process(Class c, Exception ex, boolean exit, String hint) {
        String key = c.getName();
        if (!HAPPENED.getOrDefault(key, Boolean.FALSE)) {
            HAPPENED.put(key, Boolean.TRUE);
            Logger.getLogger(key).log(Level.SEVERE, null, ex);
            MAIL.sendMail(getLocalHostIP() + " " + ex.getClass().toString(), ex.getMessage() + "<br><br>" + hint);
        }
        if (exit) {
            System.exit(1);
        }
    }

    public static String getLocalHostIP() {
        String ip;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress();
        } catch (Exception ex) {
            ip = "";
        }

        return ip;
    }
}

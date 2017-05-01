package exhandle;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailNotification {

    public static String USER = "australiacityanalysis@outlook.com";
    public static String PASSWORD = "";
    public static String RECIPIENT = "";

    public void sendMail(String subject, String htmlMessage) {
        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp-mail.outlook.com");
        props.put("mail.smtp.user", USER);
        props.put("mail.smtp.password", PASSWORD);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", true);

        Session session = Session.getInstance(props, null);
        MimeMessage message = new MimeMessage(session);

        /* Create the email addresses involved */
        try {
            InternetAddress from = new InternetAddress(USER);
            message.setSubject(subject);
            message.setFrom(from);
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(RECIPIENT));

            /* Create a multi-part to combine the parts */
            Multipart multipart = new MimeMultipart("alternative");

            /* Create the html part */
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(htmlMessage, "text/html");

            /* Add html part to multi part */
            multipart.addBodyPart(messageBodyPart);

            /* Associate multi-part with message */
            message.setContent(multipart);

            /* Send message */
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp-mail.outlook.com", USER, PASSWORD);

            transport.sendMessage(message, message.getAllRecipients());
        } catch (AddressException ex) {
            Logger.getLogger(MailNotification.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(MailNotification.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

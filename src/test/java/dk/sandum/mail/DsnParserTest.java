package dk.sandum.mail;

import java.io.InputStream;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import static junit.framework.Assert.*;
import junit.framework.TestCase;

/**
 *
 * @author osa
 */
public class DsnParserTest extends TestCase {
    
    public DsnParserTest(String testName) {
        super(testName);
    }

    public void testDsnParser() throws MessagingException, BounceParserException {
        InputStream is = getClass().getResourceAsStream("/sample-dsn.eml");

        Session s = Session.getDefaultInstance(new Properties());
        MimeMessage dsn = new MimeMessage(s, is);        
        assertNotNull(dsn);
        
        MailDeliveryStatus mds = BounceParser.parseMessage(dsn);
        assertNotNull(mds);
        
        assertEquals(MailDeliveryAction.failed, mds.getDeliveryAction());
    }

    public void testMdnParser() throws MessagingException, BounceParserException {
        InputStream is = getClass().getResourceAsStream("/dovecot-mdn-sample.eml");

        Session s = Session.getDefaultInstance(new Properties());
        MimeMessage dsn = new MimeMessage(s, is);        
        assertNotNull(dsn);
        
        MailDeliveryStatus mds = BounceParser.parseMessage(dsn);
        assertNotNull(mds);
        
        assertEquals(MailDeliveryAction.failed, mds.getDeliveryAction());
    }
}

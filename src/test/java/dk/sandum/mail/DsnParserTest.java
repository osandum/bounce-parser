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

    private MailDeliveryStatus parseEml(String eml) throws MessagingException, BounceParserException {
        InputStream is = getClass().getResourceAsStream(eml);

        Session s = Session.getDefaultInstance(new Properties());
        MimeMessage dsn = new MimeMessage(s, is);
        assertNotNull(dsn);

        MailDeliveryStatus mds = BounceParser.parseMessage(dsn);
        assertNotNull(mds);

        return mds;
    }

    public void testSampleDsn() throws MessagingException, BounceParserException {
        assertEquals(MailDeliveryAction.failed, parseEml("/sample-dsn.eml").getDeliveryAction());
    }

    public void testLmtpResponse() throws MessagingException, BounceParserException {
        assertEquals(MailDeliveryAction.failed, parseEml("/lmtp-sample.eml").getDeliveryAction());
    }

    public void testMdn() throws MessagingException, BounceParserException {
        assertEquals(MailDeliveryAction.failed, parseEml("/dovecot-mdn-sample.eml").getDeliveryAction());
    }
}

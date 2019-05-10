package dk.sandum.mail;

import java.io.InputStream;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author osa
 */
public class DsnParserTest {
    private final static Logger LOG = LoggerFactory.getLogger(DsnParserTest.class);

    private MailDeliveryStatus parseEml(String eml) throws MessagingException, BounceParserException {
        InputStream is = getClass().getResourceAsStream(eml);

        Session s = Session.getDefaultInstance(new Properties());
        MimeMessage dsn = new MimeMessage(s, is);
        assertNotNull(dsn);


        LOG.info("\n# ============================\n# Parse \"" + dsn.getSubject() + "\"" + dsn.getMessageID());
        MailDeliveryStatus mds = BounceParser.parseMessage(dsn);
        assertNotNull(mds);

        return mds;
    }

    @Test
    public void testSampleDsn() throws MessagingException, BounceParserException {
        assertEquals(MailDeliveryAction.failed, parseEml("/sample-dsn.eml").getDeliveryAction());
    }

    @Test
    public void testLmtpResponse() throws MessagingException, BounceParserException {
        assertEquals(MailDeliveryAction.failed, parseEml("/lmtp-sample.eml").getDeliveryAction());
    }

    @Test
    public void testQmailResponse() throws MessagingException, BounceParserException {
        assertEquals(MailDeliveryAction.failed, parseEml("/qmail-failure-sample.eml").getDeliveryAction());
    }

    @Test
    public void testMdn() throws MessagingException, BounceParserException {
        assertEquals(MailDeliveryAction.failed, parseEml("/dovecot-mdn-sample.eml").getDeliveryAction());
    }

    @Test
    public void testSkylineDk() throws MessagingException, BounceParserException {
        assertEquals(MailDeliveryAction.failed, parseEml("/skylinemail_dk.eml").getDeliveryAction());
    }

    @Test
    public void testEximResponse() throws MessagingException, BounceParserException {
        assertEquals(MailDeliveryAction.failed, parseEml("/exim-failure-sample.eml").getDeliveryAction());
    }

    @Test
    public void testGoogleResponse() throws MessagingException, BounceParserException {
        assertEquals(MailDeliveryAction.delayed, parseEml("/gmail-delayed-sample.eml").getDeliveryAction());
    }

    @Test
    public void testSurftownResponse() throws MessagingException, BounceParserException {
        assertEquals(MailDeliveryAction.failed, parseEml("/surftown-failure-sample.eml").getDeliveryAction());
    }

    @Test
    public void testBadAddress() throws MessagingException, BounceParserException {
        try {
            parseEml("/bad-address-sample.eml");
            fail("BounceParserException expected");
        }
        catch (BounceParserException ex) {
            // expected
        }
    }
}

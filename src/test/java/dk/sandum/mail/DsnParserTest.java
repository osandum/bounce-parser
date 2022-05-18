package dk.sandum.mail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author osa
 */
public class DsnParserTest {
    private final static Logger LOG = LoggerFactory.getLogger(DsnParserTest.class);

    private static File samplesDir;

    @BeforeClass
    public static void setUpClass() {
        samplesDir = new File("samples");
        assertTrue(samplesDir.getAbsolutePath() + ": samples directory not found", samplesDir.exists() && samplesDir.isDirectory());
        LOG.info("\n# Loading samples from {}", samplesDir.getAbsolutePath());
    }

    private MailDeliveryStatus parseEml(String eml) throws MessagingException, BounceParserException {
        return parseEml(_res(eml));
    }

    private static URL _res(String eml) {
        URL res = DsnParserTest.class.getClassLoader().getResource(eml);
        assertNotNull(eml, res);
        return res;
    }

    private MailDeliveryStatus parseEml(URL src) throws MessagingException, BounceParserException {
        MimeMessage dsn = _parseMail(src);

        LOG.debug("\n# ============================\n# {}\n# Parse \"{}\" {}", src, dsn.getSubject(), dsn.getMessageID());
        MailDeliveryStatus mds = BounceParser.parseMessage(dsn);
        assertNotNull(mds);

        return mds;
    }

    private static MimeMessage _parseMail(URL src) throws MessagingException {
        LOG.info("parse({})", src);
        Session s = Session.getDefaultInstance(new Properties());
        try (InputStream is = src.openStream()) {
            MimeMessage dsn = new MimeMessage(s, is);
            assertNotNull(dsn);
            return dsn;
        }
        catch (IOException ex) {
            throw new AssertionError(ex.getMessage());
        }
    }

    @Test
    public void testSampleDsn() throws MessagingException, BounceParserException {
        MailDeliveryStatus mds = parseEml("sample-dsn.eml");
        assertEquals(MailDeliveryAction.failed, mds.getDeliveryAction());
    }

    @Test
    public void testLmtpResponse() throws MessagingException, BounceParserException {
        MailDeliveryStatus mds = parseEml("lmtp-sample.eml");
        assertEquals(MailDeliveryAction.failed, mds.getDeliveryAction());
    }

    @Test
    public void testQmailResponse() throws MessagingException, BounceParserException {
        MailDeliveryStatus mds = parseEml("qmail-failure-sample.eml");
        assertEquals(MailDeliveryAction.failed, mds.getDeliveryAction());
    }

    @Test
    public void testDovecotMdn() throws MessagingException, BounceParserException {
        MailDeliveryStatus mds = parseEml("dovecot-mdn-sample.eml");
        assertEquals(MailDeliveryAction.failed, mds.getDeliveryAction());
    }

    @Test
    public void testSkylineDk() throws MessagingException, BounceParserException {
        MailDeliveryStatus mds = parseEml("skylinemail_dk.eml");
        assertEquals(MailDeliveryAction.failed, mds.getDeliveryAction());
    }

    @Test
    public void testKundenserverDe() throws MessagingException, BounceParserException {
        MailDeliveryStatus mds = parseEml("kundenserver_de.eml");
        assertEquals(MailDeliveryAction.failed, mds.getDeliveryAction());
    }

    @Test
    public void testEximResponse() throws MessagingException, BounceParserException {
        MailDeliveryStatus mds = parseEml("exim-failure-sample.eml");
        assertEquals(MailDeliveryAction.failed, mds.getDeliveryAction());
    }

    @Test
    public void testGoogleResponse() throws MessagingException, BounceParserException {
        MailDeliveryStatus mds = parseEml("gmail-delayed-sample.eml");
        assertEquals(MailDeliveryAction.delayed, mds.getDeliveryAction());
    }

    @Test
    public void testSurftownResponse() throws MessagingException, BounceParserException {
        MailDeliveryStatus mds = parseEml("surftown-failure-sample.eml");
        assertEquals(MailDeliveryAction.failed, mds.getDeliveryAction());
    }

    @Test
    public void testQuotaExceededResponse() throws MessagingException, BounceParserException {
        MailDeliveryStatus mds = parseEml("quota-exceeded-sample.eml");
        assertEquals("4.2.2", mds.getDeliveryStatus().toString());
        assertEquals(MailDeliveryAction.failed, mds.getDeliveryAction());
    }

    @Test
    public void testBadAddress() throws MessagingException, BounceParserException {
        try {
            parseEml("bad-address-sample.eml");
            fail("BounceParserException expected");
        }
        catch (BounceParserException ex) {
            // expected
        }
    }

    @Test
    public void testLocalSamples() throws MessagingException, BounceParserException, MalformedURLException {
        for (File sampleFile : samplesDir.listFiles()) {
            MailDeliveryStatus dsn = parseEml(sampleFile.toURI().toURL());
            assertNotNull(sampleFile.getAbsolutePath(), dsn);
        }
    }
}

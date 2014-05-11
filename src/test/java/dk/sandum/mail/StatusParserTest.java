package dk.sandum.mail;

import javax.mail.internet.ParseException;
import static junit.framework.Assert.*;
import junit.framework.TestCase;

/**
 *
 * @author osa
 */
public class StatusParserTest extends TestCase {

    public StatusParserTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testParse1() throws ParseException {
        MailSystemStatusCode sc = MailSystemStatusCode.parse("5.1.1");
        assertFalse(sc.isSuccess());
        assertTrue(sc.isPermanent());
    }

    public void testParse2() throws ParseException {
        MailSystemStatusCode sc = MailSystemStatusCode.parse("5.2.2 (mailbox full)");
        assertFalse(sc.isSuccess());
        assertTrue(sc.isPermanent());
    }
}

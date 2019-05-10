package dk.sandum.mail;

import javax.mail.internet.ParseException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author osa
 */
public class StatusParserTest {

    @Test
    public void testParse1() throws ParseException {
        MailSystemStatusCode sc = MailSystemStatusCode.parse("5.1.1");
        assertFalse(sc.isSuccess());
        assertTrue(sc.isPermanent());
    }

    @Test
    public void testParse2() throws ParseException {
        MailSystemStatusCode sc = MailSystemStatusCode.parse("5.2.2 (mailbox full)");
        assertFalse(sc.isSuccess());
        assertTrue(sc.isPermanent());
    }
}

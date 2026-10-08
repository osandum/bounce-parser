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

    @Test
    public void testEquals() throws ParseException {
        MailSystemStatusCode a = MailSystemStatusCode.parse("5.2.2");
        MailSystemStatusCode b = MailSystemStatusCode.parse("5.2.2 (mailbox full)");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, MailSystemStatusCode.parse("5.1.1"));
        assertNotEquals(a, MailSystemStatusCode.parse("4.2.2"));
        assertNotEquals(a, MailSystemStatusCode.parse("5.2.1"));
    }
}

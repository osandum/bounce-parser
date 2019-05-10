package dk.sandum.mail;

import javax.mail.internet.ParseException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author osa
 */
public class MailDispositionTest {

    @Test
    public void testParse() throws ParseException {
        MailDisposition md = MailDisposition.parse("automatic-action/MDN-sent-automatically; deleted");
        Assert.assertNotNull(md);
    }
}

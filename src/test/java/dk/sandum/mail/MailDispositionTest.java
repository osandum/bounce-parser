package dk.sandum.mail;

import javax.mail.internet.ParseException;
import junit.framework.TestCase;

/**
 *
 * @author osa
 */
public class MailDispositionTest extends TestCase {
    
    public MailDispositionTest(String testName) {
        super(testName);
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testParse() throws ParseException {
        MailDisposition md = MailDisposition.parse("automatic-action/MDN-sent-automatically; deleted");
    }
}

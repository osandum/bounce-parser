package dk.sandum.mail;

import org.junit.Test;

/**
 * @author osa
 */
public class ActionParserTest {

    @Test
    public void testParse1() {
        MailDeliveryAction ac = MailDeliveryAction.parse("failed");
    }

    @Test
    public void testParse2() {
        MailDeliveryAction ac = MailDeliveryAction.parse("Failed");
    }
}

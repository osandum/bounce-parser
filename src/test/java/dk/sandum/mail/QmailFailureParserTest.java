package dk.sandum.mail;

import javax.mail.internet.ParseException;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author osa
 */
public class QmailFailureParserTest {

    private final static String PLAIN_FAILURE1 = "Hi. This is the qmail-send program at qmail29.zonnet.nl.\n" +
"I'm afraid I wasn't able to deliver your message to the following addresses.\n" +
"This is a permanent error; I've given up. Sorry it didn't work out.\n" +
"---------------------------------------------------------------------\n" +
"Het volgende bericht dat u stuurde aan deze gebruiker is niet aangekomen.\n" +
"De meest voorkomende redenen zijn:\n" +
"- het verkeerd intypen van het e-mailadres of het e-mailadres bestaat niet.\n" +
"- de mailbox van de ontvangende gebruiker is vol waardoor er geen e-mail meer wo\n" +
"rdt geaccepteerd.\n" +
"- het e-mailbericht stond in de wachtrij en kon in de afgelopen 3 dagen niet wor\n" +
"den afgeleverd.\n" +
"--------------------------------------------------------------------\n" +
"The following email that you sent to this user has not been received.\n" +
"The most occuring reasons are:\n" +
"- wrongly typed e-mailaddress or the e-mailaddress doesn't exist.\n" +
"- the e-mailbox of the receiving user is full and therefore the e-mail won't be\n" +
"accepted.\n" +
"- the e-mailmessage was in queue and couldn't be delivered in the past 3 days.\n" +
"---------------------------------------------------------------------\n" +
"\n" +
"<sample6@example.nl>:\n" +
"The users mailfolder is over the allowed quota (size).\n" +
"";

    private final static String PLAIN_FAILURE2 = "Hi. This is the qmail-send program at mail19.surf-town.net.\n" +
"I'm afraid I wasn't able to deliver your message to the following addresses.\n" +
"This is a permanent error; I've given up. Sorry it didn't work out.\n" +
"\n" +
"<sample10@example.dk>:\n" +
"user is over quota\n" +
"";

    @Test
    public void testFailure1() throws ParseException {
        QmailFailure res = QmailFailure.tryParse(PLAIN_FAILURE1);

        Assert.assertNotNull(res);
    }

    @Test
    public void testFailure2() throws ParseException {
        QmailFailure res = QmailFailure.tryParse(PLAIN_FAILURE2);

        Assert.assertNotNull(res);
    }
}

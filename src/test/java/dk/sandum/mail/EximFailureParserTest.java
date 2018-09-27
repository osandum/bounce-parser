package dk.sandum.mail;

import javax.mail.internet.ParseException;
import junit.framework.TestCase;

/**
 * @author osa
 */
public class EximFailureParserTest extends TestCase {

    private final static String PLAIN_FAILURE1 = "This message was created automatically by mail delivery software.\n" +
"\n" +
"A message that you sent could not be delivered to one or more of its\n" +
"recipients. This is a permanent error. The following address(es) failed:\n" +
"\n" +
"  sample3@example.mailme.dk\n" +
"    (sa) Message rejected by abuse@mailme.dk\n" +
"    (sa) User profile spam level exceeded\n" +
"    (sa) Adjust if needed at http://www.mailme.dk\n" +
"\n" +
"------ This is a copy of the message, including all the headers. ------\n" +
"------ The body of the message is 21525 characters long; only the first\n" +
"...";
    
    private final static String PLAIN_FAILURE2 = "This message was created automatically by mail delivery software.\n" +
"\n" +
"A message that you sent could not be delivered to one or more of its\n" +
"recipients. This is a permanent error. The following address(es) failed:\n" +
"\n" +
"  sample11@example.com\n" +
"    SMTP error from remote mail server after RCPT TO:<sample11@example.com>:\n" +
"    host gmail-smtp-in.l.google.com [172.217.194.26]:\n" +
"    550-5.1.1 The email account that you tried to reach does not exist. Please try\n" +
"    550-5.1.1 double-checking the recipient's email address for typos or\n" +
"    550-5.1.1 unnecessary spaces. Learn more at\n" +
"    550 5.1.1  https://support.google.com/mail/?p=NoSuchUser u8-v6si8328409plh.253 - gsmtp\n" +
"\n" +
"------ This is a copy of the message, including all the headers. ------";
    
    public EximFailureParserTest(String testName) {
        super(testName);
    }

    public void testFailure1() throws ParseException {
        EximFailure res = EximFailure.tryParse(PLAIN_FAILURE1);
        
        assertNotNull(res);
    }

    public void testFailure2() throws ParseException {
        EximFailure res = EximFailure.tryParse(PLAIN_FAILURE2);
        
        assertNotNull(res);
    }
}

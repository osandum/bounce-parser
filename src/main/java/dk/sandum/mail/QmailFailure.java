package dk.sandum.mail;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.internet.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author osa
 */
public class QmailFailure implements Serializable {

    private String m_reportingHost;
    private String m_recipient;
    private String m_reason;

    private static final Logger LOG = LoggerFactory.getLogger(QmailFailure.class);

    private static final Pattern QMAIL_FAILURE_PATTERN
            = Pattern.compile("This is the qmail-send program at ([-.a-z0-9]*[^.])\\..*"
                   + "This is a permanent error; I've given up\\. Sorry it didn't work out\\..*"
                   + "^<([^<>]+)>:\\s+([^\\n]+)$"
                    , Pattern.DOTALL | Pattern.MULTILINE);

    public static QmailFailure tryParse(String plainText) throws ParseException {
        Matcher m = QMAIL_FAILURE_PATTERN.matcher(plainText);
        if (!m.find())
            return null;

        QmailFailure res = new QmailFailure();
        LOG.debug(m.group(1) + " says <" + m.group(2) + ">: " + m.group(3));
        res.m_reportingHost = m.group(1);
        res.m_recipient = m.group(2);
        res.m_reason = m.group(3);

        return res;
    }

    public String getReportingHost() {
        return m_reportingHost;
    }

    public String getRecipient() {
        return m_recipient;
    }

    public String getReason() {
        return m_reason;
    }


}

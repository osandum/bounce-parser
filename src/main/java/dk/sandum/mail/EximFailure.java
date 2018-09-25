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
public class EximFailure implements Serializable {

    private String m_reportingHost;
    private String m_recipient;
    private String m_reason;
    private String m_statusCode;

    private final static Logger LOG = LoggerFactory.getLogger(EximFailure.class);

    private final static Pattern EXIM_FAILURE_PATTERN
            = Pattern.compile("This message was created automatically by mail delivery software\\..*"
                     + "This is a permanent error\\. The following address\\(es\\) failed:\\s+(\\S+@\\S+)(.*)"
                     , Pattern.DOTALL | Pattern.MULTILINE);
//    (sa) Message rejected by abuse@mailme.dk
//    (sa) User profile spam level exceeded
//    (sa) Adjust if needed at http://www.mailme.dk

    private final static Pattern EXIM_TAIL1_PATTERN
            = Pattern.compile("Message rejected by (\\S*)\\s+([^\\n]+)"
                     , Pattern.DOTALL | Pattern.MULTILINE);
    private final static Pattern EXIM_TAIL2_PATTERN
            = Pattern.compile("host (\\S*)\\s+\\[[0-9.]+\\]:\\s+([^\\n]+)\\s+\\d+[- ](\\d+\\.\\d+\\.\\d+)\\s"
                     , Pattern.DOTALL | Pattern.MULTILINE);

    public static EximFailure tryParse(String plainText) throws ParseException {
        Matcher m = EXIM_FAILURE_PATTERN.matcher(plainText);
        if (!m.find())
            return null;

        Matcher t = EXIM_TAIL1_PATTERN.matcher(m.group(2));
        if (t.find()) {
            EximFailure res = new EximFailure();
            LOG.debug(t.group(1) + " says <" + m.group(1) + ">: " + t.group(2));
            res.m_recipient = m.group(1);
            res.m_reportingHost = t.group(1);
            res.m_reason = t.group(2);
            res.m_statusCode = "4.0.0";
            return res;
        }

        t = EXIM_TAIL2_PATTERN.matcher(m.group(2));
        if (t.find()) {
            EximFailure res = new EximFailure();
            LOG.debug(t.group(1) + " says <" + m.group(1) + ">: " + t.group(2));
            res.m_recipient = m.group(1);
            res.m_reportingHost = t.group(1);
            res.m_reason = t.group(2);
            res.m_statusCode = t.group(3);
            return res;
        }

        return null;
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

    public String getStatusCode() {
        return m_statusCode;
    }
}

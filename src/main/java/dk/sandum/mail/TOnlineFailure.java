package dk.sandum.mail;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.internet.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TOnlineFailure implements Serializable {

    private String m_recipient;
    private String m_statusCode;

    private static final Logger LOG = LoggerFactory.getLogger(TOnlineFailure.class);

    private static final Pattern T_ONLINE_FAILURE_PATTERN
            = Pattern.compile("Failed addresses follow:.*?<([^>]+)>\\s+(\\d{3})\\s+([245]\\.[0-9]{1,3}\\.[0-9]{1,3})"
                    , Pattern.DOTALL | Pattern.MULTILINE);

    public static TOnlineFailure tryParse(String plainText) throws ParseException {
        Matcher m = T_ONLINE_FAILURE_PATTERN.matcher(plainText);
        if (!m.find())
            return null;

        TOnlineFailure res = new TOnlineFailure();
        LOG.debug("<{}> smtp={} status={}", m.group(1), m.group(2), m.group(3));
        res.m_recipient = m.group(1);
        res.m_statusCode = m.group(3);
        return res;
    }

    public String getRecipient() {
        return m_recipient;
    }

    public String getStatusCode() {
        return m_statusCode;
    }
}

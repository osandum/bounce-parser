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
    
    private final static Logger LOG = LoggerFactory.getLogger(EximFailure.class);

    private final static Pattern EXIM_FAILURE_PATTERN
            = Pattern.compile("This message was created automatically by mail delivery software\\..*" 
                     + "This is a permanent error\\. The following address\\(es\\) failed:\\s+(\\S+@\\S+).*"
//    (sa) Message rejected by abuse@mailme.dk
//    (sa) User profile spam level exceeded
//    (sa) Adjust if needed at http://www.mailme.dk            
                     + "Message rejected by (\\S*)\\s+([^\\n]+)"
                     , Pattern.DOTALL | Pattern.MULTILINE);

    public static EximFailure tryParse(String plainText) throws ParseException {
        Matcher m = EXIM_FAILURE_PATTERN.matcher(plainText);
        if (!m.find()) 
            return null;

        EximFailure res = new EximFailure();
        LOG.debug(m.group(2) + " says <" + m.group(1) + ">: " + m.group(3));
        res.m_recipient = m.group(1);
        res.m_reportingHost = m.group(2);
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

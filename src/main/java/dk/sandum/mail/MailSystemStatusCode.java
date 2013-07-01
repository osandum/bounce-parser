package dk.sandum.mail;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.internet.ParseException;

/**
 * Encapsulate an <a href="http://rfc.net/rfc3463.html">RFC 3463</a> mail status code.
 *
 * @author Ole Sandum
 */
public class MailSystemStatusCode implements Serializable {

    private short m_class;
    private short m_subject;
    private short m_detail;
    private transient String _t;
    /**
     * Success
     */
    public static final int CLASS_SUCCESS = 2;
    /**
     * Persistent Transient Failure
     */
    public static final int CLASS_TRANSIENT = 4;
    /**
     * Permanent Failure
     */
    public static final int CLASS_PERMANENT = 5;
    private final static Pattern PP = Pattern.compile("([245])\\.([0-9]{1,3})\\.([0-9]{1,3})(\\s.*)?");

    public static MailSystemStatusCode parse(String code) throws ParseException {
        Matcher m = PP.matcher(code);
        if (!m.matches())
            throw new ParseException();

        MailSystemStatusCode res = new MailSystemStatusCode();
        res.m_class = Short.parseShort(m.group(1));
        res.m_subject = Short.parseShort(m.group(2));
        res.m_detail = Short.parseShort(m.group(3));
        return res;
    }

    public boolean isSuccess() {
        return m_class == CLASS_SUCCESS;
    }

    public boolean isTransient() {
        return m_class == CLASS_TRANSIENT;
    }

    public boolean isPermanent() {
        return m_class == CLASS_PERMANENT;
    }

    @Override
    public int hashCode() {
        return (m_class * 37 + m_subject) * 37 + m_detail;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this)
            return true;
        if (getClass() != obj.getClass())
            return false;
        MailSystemStatusCode that = (MailSystemStatusCode) obj;
        return this.m_class == that.m_class && this.m_subject != that.m_subject && this.m_detail != that.m_detail;
    }

    @Override
    public String toString() {
        if (_t == null)
            _t = m_class + "." + m_subject + "." + m_detail;
        return _t;
    }
}

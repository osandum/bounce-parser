package dk.sandum.mail;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;

/**
 * @author Ole Sandum
 */
public class MailDeliveryStatus implements Serializable {

    private String m_messageId;
    private InternetHeaders m_ourHeaders = new InternetHeaders();
    private String m_statusSubject;
    private Date m_statusSentDate;
    private String m_statusDeliveredTo;
    private String m_plainTextPart;
    private MailDeliveryAction m_deliveryAction;
    private MailSystemStatusCode m_deliveryStatus;
    private InternetAddress m_finalRecipient;
    private InternetAddress m_originalRecipient;
    private String m_reportingMTA;

    public String getSubjectLine() {
        return m_statusSubject;
    }

    public void setSubject(String subj) {
        m_statusSubject = subj;
    }

    public Date getSentDate() {
        return m_statusSentDate;
    }

    public void setSentDate(Date date) {
        m_statusSentDate = date;
    }

    public String getDeliveredTo() {
        return m_statusDeliveredTo;
    }

    public void setDeliveredTo(String to) {
        m_statusDeliveredTo = to;
    }

    public String getPlainTextPart() {
        return m_plainTextPart;
    }

    public void setPlainTextPart(String text) {
        m_plainTextPart = text;
    }

    public MailDeliveryAction getDeliveryAction() {
        return m_deliveryAction;
    }

    public void setDeliveryAction(MailDeliveryAction action) {
        m_deliveryAction = action;
    }

    public MailSystemStatusCode getDeliveryStatus() {
        return m_deliveryStatus;
    }

    public void setDeliveryStatus(MailSystemStatusCode status) {
        m_deliveryStatus = status;
    }

    public String getMessageId() {
        return m_messageId;
    }

    void setMessageId(String id) {
        m_messageId = id;
    }

    void setReportingMTA(String mta) {
        m_reportingMTA = mta;
    }

    public InternetAddress getFinalRecipient() {
        return m_finalRecipient;
    }

    public void setFinalRecipient(InternetAddress recipient) {
        m_finalRecipient = recipient;
    }

    public InternetAddress getOriginalRecipient() {
        return m_originalRecipient;
    }

    public void setOriginalRecipient(InternetAddress recipient) {
        m_originalRecipient = recipient;
    }

    public void addOrignalHeader(String name, String value) {
        m_ourHeaders.addHeader(name, value);
    }

    public InternetAddress getRecipient() {
        if (m_originalRecipient != null)
            return m_originalRecipient;
        if (m_finalRecipient != null)
            return m_finalRecipient;
        String[] to = m_ourHeaders.getHeader("To");
        if (to != null && to.length > 0)
            try {
                return InternetAddress.parse(to[0])[0];
            }
            catch (AddressException ex) {
                throw new RuntimeException(ex);
            }
        return null;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0}, \"{1}\", {2}, {3}, {4}", new Object[]{getRecipient(), m_statusSubject, m_statusSentDate, m_deliveryAction, m_deliveryStatus});
    }
}

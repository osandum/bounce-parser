package dk.sandum.mail;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.*;
import javax.mail.internet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ole Sandum
 */
public class BounceParser {

    private final static Logger LOG = LoggerFactory.getLogger(BounceParser.class);

    public static MailDeliveryStatus parseMessage(Message msg)
            throws MessagingException, BounceParserException {
        LOG.debug(msg.getMessageNumber() + ": \"" + msg.getSubject() + "\"");

        String rp[] = msg.getHeader("Return-Path");
        if (rp != null && rp.length > 0 && !"<>".equals(rp[0]))
            throw new BounceParserException("Return-Path '" + rp[0] + "'. Not a bounce.");

        MailDeliveryStatus res = new MailDeliveryStatus();
        res.setSubject(msg.getSubject());
        res.setSentDate(msg.getSentDate());

        String id[] = msg.getHeader("Message-ID");
        if (id != null && id.length > 0)
            res.setMessageId(id[0]);

        String to[] = msg.getHeader("To");
        if (to != null && to.length > 0 && !"<>".equals(to[0]))
            res.setDeliveredTo(to[0]);

        try {
            parsePart(msg, res);
        }
        catch (IOException ex) {
            throw new BounceParserException("failed to parse MIME message [" + msg.getContentType() + "]: " + ex.getMessage());
        }
        catch (ParseException ex) {
            throw new BounceParserException("failed to parse MIME message [" + msg.getContentType() + "]: " + ex.getMessage());           
        }

        if (res.getDeliveryAction() == null)
            throw new BounceParserException("Unrecognized DSN format. No action code found.");
        if (res.getDeliveryStatus() == null) {
            if (res.getDeliveryAction().isFailed())
                res.setDeliveryStatus(MailSystemStatusCode.parse("4.0.0"));
            else
                throw new BounceParserException("Unrecognized DSN format. No status code found.");
        }

        return res;
    }

    private static void parsePart(Part p, MailDeliveryStatus res) throws MessagingException, IOException {
        String ct = p.getContentType().toLowerCase();

        if (LOG.isDebugEnabled()) {
            LOG.debug("##  ContentType: " + ct);
            Enumeration headers = p.getAllHeaders();
            while (headers.hasMoreElements()) {
                Header h = (Header) headers.nextElement();
                LOG.debug("##  " + h.getName() + "=" + h.getValue());
            }
        }

        if (ct.startsWith("multipart/")) {
            MimeMultipart c = (MimeMultipart) p.getContent();
            for (int i = 0; i < c.getCount(); i++) {
                BodyPart bp = c.getBodyPart(i);
                try {
                    parsePart(bp, res);
                }
                catch (IOException ex) {
                    LOG.warn("failed to parse MIME part ]" + bp.getContentType() + "]: " + ex.getMessage());
                }
            }
        }
        else if (ct.startsWith("text/plain")) {
            String plainText = p.getContent().toString();
            
            QmailFailure qf = QmailFailure.tryParse(plainText);
            if (qf != null) {
                res.setFinalRecipient(parseRecipient(qf.getRecipient()));
                res.setReportingAgent(qf.getReportingHost());
                res.setDeliveryAction(MailDeliveryAction.failed);
                res.setDeliveryStatus(MailSystemStatusCode.parse("4.0.0"));
            }
            else {
                EximFailure xf = EximFailure.tryParse(plainText);
                if (xf != null) {
                    res.setFinalRecipient(parseRecipient(xf.getRecipient()));
                    res.setReportingAgent(xf.getReportingHost());
                    res.setDeliveryAction(MailDeliveryAction.failed);
                    res.setDeliveryStatus(MailSystemStatusCode.parse("4.0.0"));                    
                }
            }
            // Analyze plainText - it will often contain a copy of our own mail
            // including all headers sent
            //        out.println(prefix + "(" + plainText.length() + " characters of plain text):");
            //        out.println(prefix + plainText);
            res.setPlainTextPart(plainText);
        }
        else if (ct.startsWith("message/delivery-status")) {
            // Niiiiice, we got an http://rfc.net/rfc3464.html DSN:
            InputStream s = new BufferedInputStream((InputStream) p.getContent());

            do {
                InternetHeaders dsnHeaders = new InternetHeaders(s);

                // Analyze these - they will tell the SMTP error code as well as
                // the recipient host and address:
                // http://www.faqs.org/rfcs/rfc3463.html

                Enumeration<Header> dsps = dsnHeaders.getAllHeaders();
                while (dsps.hasMoreElements()) {
                    Header e = dsps.nextElement();
                    String name = e.getName().toLowerCase();
                    String value = e.getValue();

                    if ("action".equals(name))
                        res.setDeliveryAction(MailDeliveryAction.parse(value));
                    if ("status".equals(name))
                        res.setDeliveryStatus(MailSystemStatusCode.parse(value));
                    if ("original-recipient".equals(name))
                        res.setOriginalRecipient(parseRecipient(value));
                    if ("final-recipient".equals(name))
                        res.setFinalRecipient(parseRecipient(value));
                    if ("reporting-mta".equals(name))
                        res.setReportingAgent(value);
                }
            } while (s.available() > 0);
            s.close();
        }
        else if (ct.startsWith("message/disposition-notification")) {
            // Hmmmm, we got an http://tools.ietf.org/html/rfc3798 MDN:
            InputStream s = new BufferedInputStream((InputStream) p.getContent());

            do {
                InternetHeaders dsnHeaders = new InternetHeaders(s);

                Enumeration<Header> dsps = dsnHeaders.getAllHeaders();
                while (dsps.hasMoreElements()) {
                    Header e = dsps.nextElement();
                    String name = e.getName().toLowerCase();
                    String value = e.getValue();

                    if ("disposition".equals(name)) {
                        MailDisposition mdn = MailDisposition.parse(value);
                        if (mdn.isAutomatedDeletion()) {
                            res.setDeliveryAction(MailDeliveryAction.failed);
                            res.setDeliveryStatus(MailSystemStatusCode.parse("4.0.0"));
                        }
                    }
                    if ("original-recipient".equals(name))
                        res.setOriginalRecipient(parseRecipient(value));
                    if ("final-recipient".equals(name))
                        res.setFinalRecipient(parseRecipient(value));
                    if ("reporting-ua".equals(name))
                        res.setReportingAgent(value);
                }
            } while (s.available() > 0);
        }
        else if (ct.startsWith("text/rfc822")) {
            InputStream s = new BufferedInputStream((InputStream) p.getContent());

            do {
                InternetHeaders originalHeaders = new InternetHeaders(s);

                Enumeration<Header> dsps = originalHeaders.getAllHeaders();
                while (dsps.hasMoreElements()) {
                    Header e = dsps.nextElement();
                    String name = e.getName();
                    String value = e.getValue();

                    LOG.debug(e.getName() + " = \"" + e.getValue() + "\"");
                    res.addOrignalHeader(name, value);
                }
            } while (s.available() > 0);

            s.close();
        }
        else if (ct.startsWith("message/rfc822")) {
            MimeMessage msg = (MimeMessage) p.getContent();
            // This will be the original message sent by us - sometimes
            // embedded in the bounce

            Enumeration headers = msg.getAllHeaders();
            while (headers.hasMoreElements()) {
                Header h = (Header) headers.nextElement();
                res.addOrignalHeader(h.getName(), h.getValue());
            }
        }
        else {
            LOG.debug("ContentType: " + ct);
            Enumeration headers = p.getAllHeaders();
            while (headers.hasMoreElements()) {
                Header h = (Header) headers.nextElement();
                LOG.debug(h.getName() + "=" + h.getValue());
            }
        }
    }

    private final static Pattern RECIPIENT_PATTERN = Pattern.compile("([^;]+;)?(@[-.a-z0-9]*[^.]:)?(.*[^.])[.]?", Pattern.MULTILINE | Pattern.DOTALL);

    private static InternetAddress parseRecipient(String value) throws ParseException {
        Matcher m = RECIPIENT_PATTERN.matcher(value);
        if (!m.matches()) {
            System.err.println("### \"" + value + "\":\n### - recipient line doesn't match " + RECIPIENT_PATTERN);
            throw new ParseException(value);
        }
        InternetAddress is[];
        try {
            is = InternetAddress.parse(m.group(3));
        }
        catch (AddressException ex) {
            throw new ParseException(value + " - " + ex.getMessage());
        }
        if (is.length != 1)
            throw new ParseException(value);

        return is[0];
    }
}

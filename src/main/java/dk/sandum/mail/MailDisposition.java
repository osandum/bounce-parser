package dk.sandum.mail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.internet.ParseException;

/**
 *
 * @author osa
 */
public class MailDisposition {
    public static enum ActionMode {
        MANUAL, AUTOMATIC
    };
    public static enum SendingMode {
        MANUAL, AUTOMATIC
    };
    public static enum DispositionType {
        DISPLAYED, DELETED
    };

    private ActionMode m_actionMode;
    private SendingMode m_sendingMode;
    private DispositionType m_dispositionType;
    private List<String> m_modifiers;

//      disposition-field =
//              "Disposition" ":" disposition-mode ";"
//              disposition-type
//              [ "/" disposition-modifier
//              *( "," disposition-modifier ) ]
    private final static Pattern PP = Pattern.compile("(manual|automatic)-action/MDN-sent-(manual|automatic)[aly]+;\\s*(displayed|deleted)(/(.+))?");

    public static MailDisposition parse(String disp) throws ParseException {
        Matcher m = PP.matcher(disp);
        if (!m.matches())
            throw new ParseException();

        MailDisposition res = new MailDisposition();
        res.m_actionMode = ActionMode.valueOf(m.group(1).toUpperCase());
        res.m_sendingMode = SendingMode.valueOf(m.group(2).toUpperCase());
        res.m_dispositionType = DispositionType.valueOf(m.group(3).toUpperCase());
        if (m.group(5) != null)
            res.m_modifiers = Collections.unmodifiableList(Arrays.asList(m.group(5).split(",\\s*")));

        return res;
    }

    public ActionMode getActionMode() {
        return m_actionMode;
    }

    public SendingMode getSendingMode() {
        return m_sendingMode;
    }

    public DispositionType getDispositionType() {
        return m_dispositionType;
    }

    public List<String> getModifiers() {
        return m_modifiers;
    }
    
    public boolean isAutomatedDeletion() {
        return m_actionMode == ActionMode.AUTOMATIC && m_sendingMode == SendingMode.AUTOMATIC && m_dispositionType == DispositionType.DELETED;
    }
    
}

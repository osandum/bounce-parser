package dk.sandum.mail;

/**
 * @author Ole Sandum
 */
public enum MailDeliveryAction {

    failed, delayed, delivered, relayed, expanded;

    public static MailDeliveryAction parse(String action) {
        return action == null ? null : valueOf(action.toLowerCase());
    }

    public boolean isFailed() {
        return equals(failed);
    }

    public boolean isDelayed() {
        return equals(delayed);
    }

    public boolean isDelivered() {
        return equals(delivered);
    }
}

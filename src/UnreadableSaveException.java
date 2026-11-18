/** The save file cannot be used at all: it does not exist, it is empty, or it is not UTF-8 text. */
public class UnreadableSaveException extends FarmException {
    public UnreadableSaveException(String message, Throwable cause) {
        super(message);
        if (cause != null) initCause(cause);
    }
}

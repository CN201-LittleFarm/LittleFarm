/**
 * One line of the save file is damaged. Carries the facts (which line, what it says) so the screen
 * can show them; the original problem (e.g. a NumberFormatException) is kept as the cause.
 *
 *   FarmException
 *     +-- CorruptSaveException      a line of the save is wrong        (this class)
 *     +-- UnreadableSaveException   the file as a whole cannot be used
 */
public class CorruptSaveException extends FarmException {
    private final int lineNumber;      // 1 = first line of the file; 0 = no single line to blame (an entry is missing)
    private final String lineText;

    public CorruptSaveException(int lineNumber, String lineText, String reason, Throwable cause) {
        super(lineNumber > 0 ? "line " + lineNumber + ": " + reason : reason);   // short enough for one line of the screen
        this.lineNumber = lineNumber;
        this.lineText = lineText;
        if (cause != null) initCause(cause);   // keep the original failure for the stack trace
    }

    public int getLineNumber() { return lineNumber; }
    public String getLineText() { return lineText; }
}

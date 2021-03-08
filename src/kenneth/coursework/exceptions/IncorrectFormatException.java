package kenneth.coursework.exceptions;

public class IncorrectFormatException extends Exception {
    public IncorrectFormatException() {
        super("The given file does not have the correct format.");
    }
}

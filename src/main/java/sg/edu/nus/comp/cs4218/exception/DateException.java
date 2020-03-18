package sg.edu.nus.comp.cs4218.exception;

public class DateException extends AbstractApplicationException {

    public static final String INVALID_FORMAT = "Invalid Format: ";
    public static final String ERR_WRITE_STREAM = "Could not write to output stream";
    private static final long serialVersionUID = -4071979116776975963L;

    public DateException(String message) {
        super("date: " + message);
    }

    public DateException(Exception exception, String message) {
        super("date: " + message);
    }

}

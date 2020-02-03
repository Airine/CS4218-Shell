package sg.edu.nus.comp.cs4218.exception;

public class FindException extends AbstractApplicationException {

    private static final long serialVersionUID = -4647741054058509116L;

    public FindException(String message) {
        super("find: " + message);
    }

    public FindException(Exception exception, String message) {
        super("find: " + message);
    }
}

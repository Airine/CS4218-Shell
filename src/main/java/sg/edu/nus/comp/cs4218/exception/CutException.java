package sg.edu.nus.comp.cs4218.exception;

public class CutException extends AbstractApplicationException {

    private static final long serialVersionUID = -4895418285669637846L;

    public CutException(String message) {
        super("cut: " + message);
    }
}

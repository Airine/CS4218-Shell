package sg.edu.nus.comp.cs4218.exception;

public class SedException extends AbstractApplicationException {

    private static final long serialVersionUID = -1617822939627658197L;

    public SedException(String message) {
        super("sed: " + message);
    }
}

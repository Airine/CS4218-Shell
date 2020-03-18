package sg.edu.nus.comp.cs4218.exception;

public class MkdirException extends AbstractApplicationException {

    private static final long serialVersionUID = 8010149108168272149L;

    public MkdirException(String message) {
        super("mkdir: " + message);
    }

}

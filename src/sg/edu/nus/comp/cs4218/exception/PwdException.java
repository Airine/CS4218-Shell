package sg.edu.nus.comp.cs4218.exception;

public class PwdException extends AbstractApplicationException {

    private static final long serialVersionUID = -3206758524312595175L;

    public PwdException(String message) {
        super("pwd: " + message);
    }

    public PwdException(Exception exception, String message) {
        super("pwd: " + message);
    }
}
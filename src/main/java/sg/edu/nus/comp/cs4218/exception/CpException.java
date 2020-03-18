package sg.edu.nus.comp.cs4218.exception;

public class CpException extends AbstractApplicationException {

    private static final long serialVersionUID = -4730922172179294678L;

    public CpException(String message) {
        super("cd: " + message);
    }
}
package sg.edu.nus.comp.cs4218.exception;

public class DiffException extends AbstractApplicationException {

    private static final long serialVersionUID = -6719426298893305621L;

    public DiffException(String message) {
        super("diff: " + message);
    }
}
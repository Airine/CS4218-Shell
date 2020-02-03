package sg.edu.nus.comp.cs4218.impl.exception;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class FindException extends AbstractApplicationException {

    private static final long serialVersionUID = -4647741054058509116L;

    public FindException(String message) {
        super("find: " + message);
    }

}

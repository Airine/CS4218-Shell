package sg.edu.nus.comp.cs4218.exception;

public class InvalidDirectoryException extends Exception {
    public InvalidDirectoryException(String directory) {
        super(String.format("ls: cannot access '%s': No such file or directory", directory));
    }
}

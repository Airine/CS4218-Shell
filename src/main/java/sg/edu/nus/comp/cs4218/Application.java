package sg.edu.nus.comp.cs4218;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

import java.io.InputStream;
import java.io.OutputStream;

public interface Application {

    /**
     * Runs application with specified input data and specified output stream.
     * @param args List of input arguments.
     * @param stdin The specified InputStream.
     * @param stdout The specified OutputStream.
     * @throws AbstractApplicationException The exception type of certain application.
     */
    void run(String[] args, InputStream stdin, OutputStream stdout)
            throws AbstractApplicationException;

}

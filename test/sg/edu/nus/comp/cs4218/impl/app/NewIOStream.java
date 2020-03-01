package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NewIOStream implements Closeable {

    public final InputStream inputStream;
    public final OutputStream outputStream;

    public NewIOStream(String fileIn, String fileOut) throws IOException {
        try {
            inputStream = IOUtils.openInputStream(fileIn);
            outputStream = IOUtils.openOutputStream(fileOut);
        } catch (ShellException e) {
            throw (IOException) new IOException(e.getMessage()).initCause(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            IOUtils.closeInputStream(inputStream);
            IOUtils.closeOutputStream(outputStream);
        } catch (ShellException e) {
            e.printStackTrace();
        }
    }
}

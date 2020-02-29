package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NewIOStream implements Closeable {

    public final InputStream in;
    public final OutputStream out;

    public NewIOStream(String fileIn, String fileOut) throws IOException {
        try {
            in = IOUtils.openInputStream(fileIn);
            out = IOUtils.openOutputStream(fileOut);
        } catch (ShellException e) {
            throw (IOException) new IOException(e.getMessage()).initCause(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            IOUtils.closeInputStream(in);
            IOUtils.closeOutputStream(out);
        } catch (ShellException e) {
            e.printStackTrace();
        }
    }
}

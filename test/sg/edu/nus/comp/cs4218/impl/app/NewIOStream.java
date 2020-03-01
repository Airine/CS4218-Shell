package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;

public class NewIOStream implements Closeable {

    public final InputStream inputStream;
    public final OutputStream outputStream;

    /**
     * Using two file to create new stand input stream and output stream
     *
     * @param fileIn  the file use to replace stand in
     * @param fileOut the file use to replace stand out
     * @throws IOException
     */
    public NewIOStream(String fileIn, String fileOut) throws IOException {
        try {
            inputStream = IOUtils.openInputStream(fileIn);
            outputStream = IOUtils.openOutputStream(fileOut);
        } catch (ShellException e) {
            throw (IOException) new IOException(e.getMessage()).initCause(e);
        }
    }

    /**
     * Using byteArrayOutputStream as new Stand out
     *
     * @param fileIn the file use to replace stand in
     * @throws IOException
     */
    public NewIOStream(String fileIn) throws IOException {
        try {
            inputStream = IOUtils.openInputStream(fileIn);
            outputStream = new ByteArrayOutputStream();
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

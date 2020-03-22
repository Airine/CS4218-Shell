package sg.edu.nus.comp.cs4218.impl.util;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_CLOSING_STREAMS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;

@SuppressWarnings("PMD.PreserveStackTrace")
public final class IOUtils {
    private IOUtils() {
    }

    /**
     * Open an inputStream based on the file name.
     *
     * @param fileName String containing file name.
     * @return InputStream of file opened.
     * @throws ShellException If file destination is inaccessible.
     */
    public static InputStream openInputStream(String fileName) throws ShellException {
        String resolvedFileName = resolveFilePath(fileName).toString();

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(new File(resolvedFileName));
        } catch (FileNotFoundException e) {
            throw new ShellException(ERR_FILE_NOT_FOUND);
        }

        return fileInputStream;
    }

    public static InputStream openInputStream(File file) throws ShellException {
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ShellException(ERR_FILE_NOT_FOUND);
        }
        return fileInputStream;
    }

    /**
     * Open an outputStream based on the file name.
     *
     * @param fileName String containing file name.
     * @return OutputStream of file opened.
     * @throws ShellException If file destination is inaccessible.
     */
    public static OutputStream openOutputStream(String fileName) throws ShellException {
        String resolvedFileName = resolveFilePath(fileName).toString();

        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = new FileOutputStream(new File(resolvedFileName));
        } catch (FileNotFoundException e) {
            throw new ShellException(ERR_FILE_NOT_FOUND);
        }

        return fileOutputStream;
    }

    /**
     * Close an inputStream. If inputStream provided is System.in or null, it will be ignored.
     *
     * @param inputStream InputStream to be closed.
     * @throws ShellException If inputStream cannot be closed successfully.
     */
    public static void closeInputStream(InputStream inputStream) throws ShellException {
        if (inputStream == System.in || inputStream == null) {
            return;
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new ShellException(ERR_CLOSING_STREAMS);
        }
    }

    /**
     * Close an outputStream. If outputStream provided is System.out or null, it will be ignored.
     *
     * @param outputStream OutputStream to be closed.
     * @throws ShellException If outputStream cannot be closed successfully.
     */
    public static void closeOutputStream(OutputStream outputStream) throws ShellException {
        if (outputStream == System.out || outputStream == null) {
            return;
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            throw new ShellException(ERR_CLOSING_STREAMS);
        }
    }

    public static Path resolveFilePath(String fileName) {
        Path currentDirectory = Paths.get(Environment.currentDirectory);
        return currentDirectory.resolve(fileName);
    }

    /**
     * Returns a list of lines based on the given InputStream.
     *
     * @param input InputStream containing arguments from System.in or FileInputStream
     * @throws Exception If something wrong with the InputStream input
     */
    public static List<String> getLinesFromInputStream(InputStream input) throws Exception {
        if (input == null) {
            return null;
        }
        List<String> output = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
        }
        return output;
    }
}

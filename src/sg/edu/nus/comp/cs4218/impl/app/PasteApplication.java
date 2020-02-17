package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.EnvironmentUtils;
import sg.edu.nus.comp.cs4218.app.PasteInterface;
import sg.edu.nus.comp.cs4218.exception.PasteException;

import java.io.*;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_TAB;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class PasteApplication implements PasteInterface {

    /**
     * Runs the sed application with the specified arguments.
     *
     * @param args   Array of arguments for the application.
     * @param stdin  An InputStream. The input for the command is read from this InputStream if no
     *               files are specified.
     * @param stdout An OutputStream. The output of the command is written to this OutputStream.
     * @throws PasteException
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws PasteException {
        // Format: paste [FILE]...
        if (args == null) {
            throw new PasteException(ERR_NULL_ARGS);
        }
        if (stdout == null) {
            throw new PasteException(ERR_NULL_STREAMS);
        }
        //TODO
    }

    /**
     * Returns string of line-wise concatenated (tab-separated) Stdin arguments. If only one Stdin
     * arg is specified, echo back the Stdin.
     *
     * @param stdin InputStream containing arguments from Stdin
     * @throws Exception
     */
    @Override
    public String mergeStdin(InputStream stdin) throws Exception {
        //TODO
        return null;
    }

    /**
     * Returns string of line-wise concatenated (tab-separated) files. If only one file is
     * specified, echo back the file content.
     *
     * @param fileName Array of file names to be read and merged
     * @throws Exception
     */
    @Override
    public String mergeFile(String... fileName) throws Exception {
        if (fileName == null || fileName.length == 0) {
            throw new PasteException(ERR_NULL_ARGS);
        }
        StringBuilder output = new StringBuilder();
        BufferedReader[] readers = new BufferedReader[fileName.length];
        try {
            for (int i = 0; i < fileName.length; i++) {
                String path = convertToAbsolutePath(fileName[i]);
                File file = new File(path);
                if (!file.exists()) {
                    throw new PasteException(ERR_FILE_NOT_FOUND);
                }
                if (file.isDirectory()) {
                    throw new PasteException(ERR_IS_DIR);
                }
                BufferedReader reader = new BufferedReader(new FileReader(path));
                readers[i] = reader;
            }
            String line;
            if (fileName.length == 1) { //echo back if only one file specified
                while ((line = readers[0].readLine()) != null) {
                    output.append(line).append(STRING_NEWLINE);
                }
                readers[0].close();
                return output.toString();
            }
            int unfinished;
            do {
                unfinished = fileName.length;
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < fileName.length; i++) {
                    line = readers[i].readLine();
                    if (line == null) {
                        builder.append(CHAR_TAB);
                        unfinished--;
                    } else {
                        builder.append(line).append(CHAR_TAB);
                    }
                }
                if (unfinished > 0) {
                    output.append(builder.toString()).append(STRING_NEWLINE);
                }
            } while (unfinished > 0);
            for (BufferedReader reader : readers) {
                reader.close();
            }
        }catch (IOException e) {
            throw new PasteException(ERR_IO_EXCEPTION);
        }
        return output.toString();
    }

    /**
     * Returns string of line-wise concatenated (tab-separated) files and Stdin arguments.
     *
     * @param stdin    InputStream containing arguments from Stdin
     * @param fileName Array of file names to be read and merged
     * @throws Exception
     */
    @Override
    public String mergeFileAndStdin(InputStream stdin, String... fileName) throws Exception {
        //TODO
        return null;
    }


    /* Duplicate methods, may need to move these methods to Util classes*/

    /**
     * Converts path provided by user into path recognised by the system
     *
     * @param path supplied by user
     * @return a String of the converted path
     */
    private String convertPathToSystemPath(String path) {
        String convertedPath = path;
        String pathIdentifier = "\\" + Character.toString(CHAR_FILE_SEP);
        convertedPath = convertedPath.replaceAll("(\\\\)+", pathIdentifier);
        convertedPath = convertedPath.replaceAll("/+", pathIdentifier);

        if (convertedPath.length() != 0 && convertedPath.charAt(convertedPath.length() - 1) == CHAR_FILE_SEP) {
            convertedPath = convertedPath.substring(0, convertedPath.length() - 1);
        }

        return convertedPath;
    }

    /**
     * Converts folderName to absolute path, if initially was relative path
     *
     * @param folderName supplied by user
     * @return a String of the absolute path of the folderName
     */
    private String convertToAbsolutePath(String folderName) {
        String home = System.getProperty("user.home").trim();
        String currentDir = EnvironmentUtils.currentDirectory.trim();
        String convertedPath = convertPathToSystemPath(folderName);

        String newPath;
        if (convertedPath.length() >= home.length() && convertedPath.substring(0, home.length()).trim().equals(home)) {
            newPath = convertedPath;
        } else {
            newPath = currentDir + CHAR_FILE_SEP + convertedPath;
        }
        return newPath;
    }
}

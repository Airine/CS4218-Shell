package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CutInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.app.args.CutArguments;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class CutApplication implements CutInterface {

    /**
     * Cuts out selected portions of each line
     *
     * @param isCharPo Boolean option to cut by character position
     * @param isBytePo Boolean option to cut by byte position
     * @param isRange  Boolean option to perform range-based cut
     * @param startIdx index to begin cut
     * @param endIdx   index to end cut
     * @param fileName Array of String of file names
     * @return The expected result
     * @throws Exception Throw exception caused by CutApplication
     */
    @Override
    public String cutFromFiles(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx, String... fileName) throws Exception {
        if (fileName == null) {
            throw new CutException(ERR_NULL_ARGS);
        }
        List<String> lines = new ArrayList<>();
        for (String file : fileName) {
            File node = IOUtils.resolveFilePath(file).toFile();
            if (!node.exists()) {
                throw new Exception(ERR_FILE_NOT_FOUND);
            }
            if (node.isDirectory()) {
                throw new Exception(ERR_IS_DIR);
            }
            if (!node.canRead()) {
                throw new Exception(ERR_NO_PERM);
            }
            try(InputStream input = IOUtils.openInputStream(file)) {
                lines.addAll(IOUtils.getLinesFromInputStream(input));
                IOUtils.closeInputStream(input);
            } catch (Exception e){
                throw new Exception(ERR_IO_EXCEPTION);
            }
        }
        cutInputString(isCharPo, isBytePo, isRange, startIdx, endIdx, lines);
        return String.join(STRING_NEWLINE, lines);
    }

    /**
     * Cuts out selected portions of each line
     *
     * @param isCharPo Boolean option to cut by character position
     * @param isBytePo Boolean option to cut by byte position
     * @param isRange  Boolean option to perform range-based cut
     * @param startIdx index to begin cut
     * @param endIdx   index to end cut
     * @param stdin    InputStream containing arguments from Stdin
     * @return The expected result
     * @throws Exception The exception from CutApplication
     */
    @Override
    public String cutFromStdin(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx, InputStream stdin) throws Exception {
        if (stdin == null) {
            throw new CutException(ERR_NULL_STREAMS);
        }
        List<String> lines;
        lines = IOUtils.getLinesFromInputStream(stdin);
        cutInputString(isCharPo, isBytePo, isRange, startIdx, endIdx, lines);
        return String.join(STRING_NEWLINE, lines);
    }

    /**
     * Runs application with specified input data and specified output stream.
     *
     * @param args List of input arguments
     * @param stdin The specified InputStream
     * @param stdout The specified OutputStream
     * @throws CutException The exception from CutApplication
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws CutException {
        // Format: cut [Option] [LIST] FILES...
        if (args == null) {
            throw new CutException(ERR_NULL_ARGS);
        }
        if (stdout == null) {
            throw new CutException(ERR_NULL_STREAMS);
        }
        CutArguments cutArgs = new CutArguments();
        try {
            cutArgs.parse(args);
        } catch (Exception e) {
            throw new CutException(e.getMessage());//NOPMD
        }
        StringBuilder output = new StringBuilder();
        try {
            if (cutArgs.getFiles().isEmpty()) {
                output.append(cutFromStdin(cutArgs.isCharPo(), cutArgs.isBytePo(),
                        cutArgs.isRange(), cutArgs.getStartIdx(), cutArgs.getEndIdx(), stdin))
                        .append(STRING_NEWLINE);
            } else {
                for (String file : cutArgs.getFiles()) {
                    if ("-".equals(file)) {
                        output.append(cutFromStdin(cutArgs.isCharPo(), cutArgs.isBytePo(),
                                cutArgs.isRange(), cutArgs.getStartIdx(), cutArgs.getEndIdx(), stdin))
                                .append(STRING_NEWLINE);
                        continue;
                    }
                    output.append(cutFromFiles(cutArgs.isCharPo(), cutArgs.isBytePo(),
                            cutArgs.isRange(), cutArgs.getStartIdx(), cutArgs.getEndIdx(), file))
                            .append(STRING_NEWLINE);
                }
            }
        } catch (Exception e) {
            throw new CutException(e.getMessage());//NOPMD
        }
        try {
            stdout.write(output.toString().getBytes());
        } catch (IOException e) {
            throw (CutException) new CutException(ERR_WRITE_STREAM).initCause(e);
        }
    }


    /**
     * @param isCharPo Boolean option to cut by character position
     * @param isBytePo Boolean option to cut by byte position
     * @param isRange  Boolean option to perform range-based cut
     * @param startIdx index to begin cut
     * @param endIdx   index to end cut
     * @param lines    lines to be cut
     * @throws CutException The exception from CutApplication.
     */
    private void cutInputString(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx, List<String> lines) throws Exception {
        if (startIdx == 0) {
            throw new Exception(ERR_OUT_RANGE);
        }
        if (isCharPo) {
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (startIdx > line.length() || endIdx > line.length()) {
                    throw new Exception(ERR_OUT_RANGE);
                }
                if (isRange) {
                    lines.set(i, line.substring(startIdx - 1, endIdx));
                    continue;
                }
                if (endIdx == 0) {
                    lines.set(i, line.substring(startIdx - 1, startIdx));
                    continue;
                }
                lines.set(i, line.substring(startIdx - 1, startIdx) + line.substring(endIdx - 1, endIdx));
            }
        } else if (isBytePo) {
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                byte[] byteArray = line.getBytes();
                if (startIdx > byteArray.length || endIdx > byteArray.length) {
                    throw new Exception(ERR_OUT_RANGE);
                }
                if (isRange) {
                    byte[] byteArrayNew = Arrays.copyOfRange(byteArray, startIdx - 1, endIdx);
                    lines.set(i, new String(byteArrayNew));
                    continue;
                }
                if (endIdx == 0) {
                    byte[] byteArrayNew = Arrays.copyOfRange(byteArray, startIdx - 1, startIdx);
                    lines.set(i, new String(byteArrayNew));
                    continue;
                }
                byte[] byteArrayNew1 = Arrays.copyOfRange(byteArray, startIdx - 1, startIdx);
                byte[] byteArrayNew2 = Arrays.copyOfRange(byteArray, endIdx - 1, endIdx);
                lines.set(i, new String(byteArrayNew1) + new String(byteArrayNew2));
            }
        }
    }
}

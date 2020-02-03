package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.SedInterface;
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.impl.app.args.SedArguments;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class SedApplication implements SedInterface {

    /**
     * Runs the sed application with the specified arguments.
     *
     * @param args   Array of arguments for the application.
     * @param stdin  An InputStream. The input for the command is read from this InputStream if no
     *               files are specified.
     * @param stdout An OutputStream. The output of the command is written to this OutputStream.
     * @throws SedException
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws SedException {
        // Format: sed REPLACEMENT [FILE]
        if (args == null) {
            throw new SedException(ERR_NULL_ARGS);
        }
        if (stdout == null) {
            throw new SedException(ERR_NULL_STREAMS);
        }
        SedArguments sedArgs = new SedArguments();
        try {
            sedArgs.parse(args);
        } catch (Exception e) {
            throw new SedException(e.getMessage());//NOPMD
        }
        StringBuilder output = new StringBuilder();
        try {
            if (sedArgs.getFiles().isEmpty()) {
                output.append(replaceSubstringInStdin(sedArgs.getRegex(), sedArgs.getReplacement(), sedArgs.getReplacementIndex(), stdin));
            } else {
                for (String file : sedArgs.getFiles()) {
                    output.append(replaceSubstringInFile(sedArgs.getRegex(), sedArgs.getReplacement(), sedArgs.getReplacementIndex(), file));
                }
            }
        } catch (Exception e) {
            throw new SedException(e.getMessage());//NOPMD
        }
        try {
            stdout.write(output.toString().getBytes());
        } catch (IOException e) {
            throw new SedException(ERR_WRITE_STREAM);//NOPMD
        }
    }

    /**
     * Returns string of the file content with the matched substring on each line replaced. For each
     * line, find the substring that matched the pattern and replace the substring in the specified
     * index of the matched substring list.
     *
     * @param regexp           String specifying a regular expression in JAVA format
     * @param replacement      String to replace the matched pattern
     * @param replacementIndex Integer specifying the index of the matched substring to be replaced
     *                         (default is 1)
     * @param fileName         String specifying name of the file
     * @throws Exception
     */
    @Override
    public String replaceSubstringInFile(String regexp, String replacement, int replacementIndex,
                                         String fileName) throws Exception {
        if (fileName == null) {
            throw new Exception(ERR_NULL_ARGS);
        }
        File node = IOUtils.resolveFilePath(fileName).toFile();
        if (!node.exists()) {
            throw new Exception(ERR_FILE_NOT_FOUND);
        }
        if (node.isDirectory()) {
            throw new Exception(ERR_IS_DIR);
        }
        if (!node.canRead()) {
            throw new Exception(ERR_NO_PERM);
        }
        InputStream input = IOUtils.openInputStream(fileName);
        String result = replaceSubstringInStdin(regexp, replacement, replacementIndex, input);
        IOUtils.closeInputStream(input);
        return result;
    }

    /**
     * Returns string of the Stdin arg content with the matched substring on each line replaced. For
     * each line, find the substring that matched the pattern and replace the substring in the
     * specified index of the matched substring list.
     *
     * @param regexp           String specifying a regular expression in JAVA format
     * @param replacement      String to replace the matched pattern
     * @param replacementIndex Integer specifying the index of the matched substring to be replaced
     *                         (default is 1)
     * @param stdin            InputStream containing arguments from Stdin
     * @throws Exception
     */
    @Override
    public String replaceSubstringInStdin(String regexp, String replacement, int replacementIndex,
                                          InputStream stdin) throws Exception {
        if (stdin == null) {
            throw new Exception(ERR_NULL_STREAMS);
        }
        SedArguments.validate(regexp, replacement, replacementIndex);

        List<String> input = IOUtils.getLinesFromInputStream(stdin);
        Pattern pattern = Pattern.compile(regexp);

        StringBuilder output = new StringBuilder();
        for (String line : input) {
            Matcher matcher = pattern.matcher(line);
            StringBuilder builder = new StringBuilder();
            int index = 0;
            while (matcher.find()) {
                if (index == replacementIndex) {
                    builder.append(line, index, matcher.start());
                    builder.append(replacement);
                    break;
                }
            }
            builder.append(line,index,line.length());
            output.append(builder.toString()).append(STRING_NEWLINE);
        }

        return output.toString();
    }
}

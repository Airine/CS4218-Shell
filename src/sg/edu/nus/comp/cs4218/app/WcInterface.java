package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;

import java.io.InputStream;

public interface WcInterface extends Application {
    /**
     * Returns string containing the number of lines, words, and bytes in input files
     *
     * @param isBytes  Boolean option to count the number of Bytes
     * @param isLines  Boolean option to count the number of lines
     * @param isWords  Boolean option to count the number of words
     * @param fileName Array of String of file names
     * @return  string containing the number of lines, words, and bytes in input files
     * @throws Exception
     */
    String countFromFiles(Boolean isBytes, Boolean isLines, Boolean isWords,
                          String... fileName) throws Exception;

    /**
     * Returns string containing the number of lines, words, and bytes in standard input
     *
     * @param isBytes Boolean option to count the number of Bytes
     * @param isLines Boolean option to count the number of lines
     * @param isWords Boolean option to count the number of words
     * @param stdin   InputStream containing arguments from Stdin
     * @return string containing the number of lines, words, and bytes in standard input
     * @throws Exception
     */
    String countFromStdin(Boolean isBytes, Boolean isLines, Boolean isWords,
                          InputStream stdin)
            throws Exception;

}

package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;

import java.io.InputStream;


public interface GrepInterface extends Application {
    /**
     * Returns string containing lines which match the specified pattern in the given files
     *
     * @param pattern            String specifying a regular expression in JAVA format
     * @param isCaseInsensitive  Boolean option to perform case insensitive matching
     * @param isCountLines Boolean option to only write out a count of matched lines
     * @param fileNames          Array of file names
     * @throws Exception
     */
    String grepFromFiles(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, String... fileNames)
            throws Exception;

    /**
     * Returns string containing lines which match the specified pattern in Stdin
     *
     * @param pattern            String specifying a regular expression in JAVA format
     * @param isCaseInsensitive  Boolean option to perform case insensitive matching
     * @param isCountLines Boolean option to only write out a count of matched lines
     * @param stdin              InputStream containing arguments from Stdin
     * @throws Exception
     */
    String grepFromStdin(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, InputStream stdin)
            throws Exception;
}

package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;

import java.io.InputStream;

public interface SedInterface extends Application {
    /**
     * Returns string of the file content with the matched substring on each line replaced. For each
     * line, find the substring that matched the pattern and replace the substring in the specified
     * index of the matched substring list.
     *
     * @param pattern          String specifying a regular expression in JAVA format
     * @param replacement      String to replace the matched pattern
     * @param replacementIndex Integer specifying the index of the matched substring to be replaced
     *                         (default is 0)
     * @param fileName         String specifying name of the file
     * @throws Exception
     */
    String replaceSubstringInFile(String pattern, String replacement, int replacementIndex,
                                  String fileName) throws Exception;

    /**
     * Returns string of the Stdin arg content with the matched substring on each line replaced. For
     * each line, find the substring that matched the pattern and replace the substring in the
     * specified index of the matched substring list.
     *
     * @param pattern          String specifying a regular expression in JAVA format
     * @param replacement      String to replace the matched pattern
     * @param replacementIndex Integer specifying the index of the matched substring to be replaced
     *                         (default is 0)
     * @param stdin            InputStream containing arguments from Stdin
     * @throws Exception
     */
    String replaceSubstringInStdin(String pattern, String replacement, int replacementIndex,
                                   InputStream stdin) throws Exception;
}

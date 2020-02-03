package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;

public interface CpInterface extends Application {
    /**
     * copy content of source file to destination file
     *
     * @param srcFile  of path to source file
     * @param destFile of path to destination file
     * @throws Exception
     */
    String cpSrcFileToDestFile(String srcFile, String destFile) throws Exception;

    /**
     * copy files to destination folder
     *
     * @param destFolder of path to destination folder
     * @param fileName   Array of String of file names
     * @throws Exception
     */
    String cpFilesToFolder(String destFolder, String... fileName) throws Exception;
}

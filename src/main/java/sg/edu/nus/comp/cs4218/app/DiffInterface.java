package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;

import java.io.InputStream;

public interface DiffInterface extends Application {
    /**
     * Returns a string of files diff. The diff report contains a list of lines unique to the first
     * file and lines unique to the second file. Begin the former with '<' for each line. Begin the
     * latter with '>' for each line. Returns and empty string if there are no difference.
     *
     * @param fileNameA  String of file name of the first file to be diff
     * @param fileNameB  String of file name of the second file to be diff
     * @param isShowSame Boolean option to print 'Files [file_names] identical' if the files are
     *                   identical
     * @param isNoBlank  Boolean option to ignore changes with blank lines
     * @param isSimple   Boolean option to only print 'Files [file_names] differ' if the files are
     *                   different
     * @throws Exception
     */
    String diffTwoFiles(String fileNameA, String fileNameB, Boolean isShowSame,
                        Boolean isNoBlank, Boolean isSimple) throws Exception;

    /**
     * Returns a string of folder diff. Non-recursively enter each folders and perform diff on each
     * files alphabetically. Report which files differ, common directories, and which files are
     * unique to which folder. For the files in the directory, the report contains a list of lines
     * unique to the first file and lines unique to the second file. Begin the former with '<' for
     * each line. Begin the latter with '>' for each line. Returns and empty string if there are no
     * difference.
     *
     * @param folderA    of path to first directory to diff
     * @param folderB    of path to second directory to diff
     * @param isShowSame Boolean option to print 'Files [file_names] identical' if the files are
     *                   identical
     * @param isNoBlank  Boolean option to ignore changes with blank lines
     * @param isSimple   Boolean option to only print 'Files [file_names] differ' if the files are
     *                   different
     * @throws Exception
     */
    String diffTwoDir(String folderA, String folderB, Boolean isShowSame, Boolean isNoBlank,
                      Boolean isSimple) throws Exception;

    /**
     * Returns a string of file and Stdin diff. The diff report contains a list of lines unique to
     * the file and lines unique to the Stdin. Begin the former with '<' for each line. Begin the
     * latter with '>' for each line. Returns and empty string if there are no difference.
     *
     * @param fileName   String of file name of the file to be diff
     * @param stdin      InputStream of Stdin arg to diff
     * @param isShowSame Boolean option to print 'Inputs identical' if the files are identical
     * @param isNoBlank  Boolean option to ignore changes with blank lines
     * @param isSimple   Boolean option to only print 'Inputs differ' if the files are different
     * @throws Exception
     */
    String diffFileAndStdin(String fileName, InputStream stdin, Boolean isShowSame,
                            Boolean isNoBlank, Boolean isSimple) throws Exception;
}

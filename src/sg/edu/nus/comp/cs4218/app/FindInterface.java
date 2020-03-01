package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.exception.FindException;

public interface FindInterface extends Application {
    /**
     * Return the string listing the names of the matched file/folder in the specified folder.
     *
     * @param fileName   String of a regular expression of the file name
     * @param folderName Array of String of given folder/folders' name
     * @throws FindException The exception caused by FindException
     */
    String findFolderContent(String fileName, String... folderName) throws FindException;
}

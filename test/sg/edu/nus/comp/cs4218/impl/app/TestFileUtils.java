package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.File;

final public class TestFileUtils {

    private static String currTestDir = FileSystemUtils.getAbsolutePathName(FileSystemUtils.joinPath("test", "temp"));
    public static String tempFileName1 = FileSystemUtils.getAbsolutePathName(FileSystemUtils.joinPath(currTestDir, "test1.txt"));
    public static String tempFileName2 = FileSystemUtils.getAbsolutePathName(FileSystemUtils.joinPath(currTestDir, "test2.txt"));
    public static String tempFolderName = FileSystemUtils.getAbsolutePathName(FileSystemUtils.joinPath(currTestDir, "test-folder"));
    public static String tempFileInFolder = FileSystemUtils.getAbsolutePathName(FileSystemUtils.joinPath(currTestDir, "test-folder/test.cc"));
    public static String emptyFolderName = FileSystemUtils.getAbsolutePathName(FileSystemUtils.joinPath(currTestDir, "emptyFolder"));
    public static String notExistFile = FileSystemUtils.getAbsolutePathName(FileSystemUtils.joinPath(currTestDir, "not-exist"));
    private TestFileUtils(){}

    public static void createSomeFiles() throws Exception {
        File testFolder = new File(FileSystemUtils.getAbsolutePathName(currTestDir));
        if (!testFolder.exists()) {
            testFolder.mkdirs();
        }
        FileSystemUtils.createFile(tempFileName1);
        FileSystemUtils.createFile(tempFileName2);
        File emptyDir = new File(emptyFolderName);
        if (!emptyDir.exists()) {
            emptyDir.mkdirs();
        }

        File folder = new File(tempFolderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        FileSystemUtils.createFile(tempFileInFolder);
    }


    public static void rmCreatedFiles() {
        FileSystemUtils.deleteFileRecursive(new File(FileSystemUtils.getAbsolutePathName(currTestDir)));
    }

}

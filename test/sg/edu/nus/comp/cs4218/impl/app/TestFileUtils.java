package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.File;

final public class TestFileUtils {

    private static String currTestDir = FileSystemUtils.getAbsolutePathName(FileSystemUtils.joinPath("test", "temp"));
    public static String tempFileName1 = FileSystemUtils.joinPath(currTestDir, "test1.txt");
    public static String tempFileName2 = FileSystemUtils.joinPath(currTestDir, "test2.txt");

    public static String tempFolderName = FileSystemUtils.joinPath(currTestDir, "test-folder");
    public static String tempFileInFolder = FileSystemUtils.joinPath(currTestDir, "test-folder/test.cc");

    public static String tempBackFolderName = FileSystemUtils.joinPath(currTestDir, "test-folder2");
    public static String tempFileInBackFolder = FileSystemUtils.joinPath(currTestDir, "test-folder2/test2.cc");

    public static String emptyFolderName = FileSystemUtils.joinPath(currTestDir, "emptyFolder");
    public static String notExistFile = FileSystemUtils.joinPath(currTestDir, "not-exist");

    private TestFileUtils() {
    }

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

        File folder2 = new File(tempBackFolderName);
        if (!folder2.exists()) {
            folder2.mkdirs();
        }
        FileSystemUtils.createFile(tempFileInBackFolder);
    }


    public static void rmCreatedFiles() {
        FileSystemUtils.deleteFileRecursive(new File(FileSystemUtils.getAbsolutePathName(currTestDir)));
    }

}

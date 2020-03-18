package tdd.util;

import java.io.File;
import java.io.IOException;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.LongVariable"})
public class RmTestUtil {
    public static final String RM_TEST_DIR = "rm_test_env" + CHAR_FILE_SEP;
    public static final String EMPTY_DIR = "empty_directory";
    public static final String NONEMPTY_DIR = "nonempty_directory";
    public static final String NESTED_NONEMPTY_DIR = "nested_directory";
    public static final String INVALID_DIR = "invalid_directory";
    public static final String INVALID_FILE = "invalid_file.txt";
    public static final String FILE_ONE = "file1.txt";
    public static final String FILE_TWO = "file2.txt";
    public static final String NONEMPTY_DIR_FILE_ONE = "dir_file1.txt";
    public static final String NONEMPTY_DIR_FILE_TWO = "dir_file2.txt";
    public static final String NESTED_DIR_FILE = "nested_file.txt";

    public static final String RELATIVE_EMPTY_DIR_PATH = RM_TEST_DIR + EMPTY_DIR;
    public static final String RELATIVE_NONEMPTY_DIR_PATH = RM_TEST_DIR + NONEMPTY_DIR;
    public static final String RELATIVE_INVALID_DIR_PATH = RM_TEST_DIR + INVALID_DIR;
    public static final String RELATIVE_INVALID_FILE_PATH = RM_TEST_DIR + INVALID_FILE;
    public static final String RELATIVE_FILE_ONE_PATH = RM_TEST_DIR + FILE_ONE;
    public static final String RELATIVE_FILE_TWO_PATH = RM_TEST_DIR + FILE_TWO;

    public static final String ABSOLUTE_RM_TEST_PATH = System.getProperty("user.dir") + CHAR_FILE_SEP + RM_TEST_DIR;
    public static final String ABSOLUTE_EMPTY_DIR_PATH = ABSOLUTE_RM_TEST_PATH + EMPTY_DIR + CHAR_FILE_SEP;
    public static final String ABSOLUTE_INVALID_DIR_PATH = ABSOLUTE_RM_TEST_PATH + INVALID_DIR + CHAR_FILE_SEP;
    public static final String ABSOLUTE_NONEMPTY_DIR_PATH = ABSOLUTE_RM_TEST_PATH + NONEMPTY_DIR + CHAR_FILE_SEP;
    public static final String ABSOLUTE_NESTED_NONEMPTY_DIR_PATH = ABSOLUTE_NONEMPTY_DIR_PATH + NESTED_NONEMPTY_DIR + CHAR_FILE_SEP;
    public static final String ABSOLUTE_INVALID_FILE_PATH = ABSOLUTE_RM_TEST_PATH + INVALID_FILE;
    public static final String ABSOLUTE_FILE_ONE_PATH = ABSOLUTE_RM_TEST_PATH + FILE_ONE;
    public static final String ABSOLUTE_FILE_TWO_PATH = ABSOLUTE_RM_TEST_PATH + FILE_TWO;
    public static final String ABSOLUTE_NONEMPTY_DIR_FILE_ONE_PATH = ABSOLUTE_NONEMPTY_DIR_PATH + NONEMPTY_DIR_FILE_ONE;
    public static final String ABSOLUTE_NONEMPTY_DIR_FILE_TWO_PATH = ABSOLUTE_NONEMPTY_DIR_PATH + NONEMPTY_DIR_FILE_TWO;
    public static final String ABSOLUTE_NESTED_DIR_FILE_PATH = ABSOLUTE_NESTED_NONEMPTY_DIR_PATH + NESTED_DIR_FILE;

    public File testDir;
    public File emptyDir;
    public File nonEmptyDir;
    public File nestedNonEmptyDir;
    public File fileOne;
    public File fileTwo;
    public File dirFileOne;
    public File dirFileTwo;
    public File nestedDirFile;

    public RmTestUtil() {
        testDir = new File(ABSOLUTE_RM_TEST_PATH);
        emptyDir = new File(ABSOLUTE_EMPTY_DIR_PATH);
        nonEmptyDir = new File(ABSOLUTE_NONEMPTY_DIR_PATH);
        nestedNonEmptyDir = new File(ABSOLUTE_NESTED_NONEMPTY_DIR_PATH);
        fileOne = new File(ABSOLUTE_FILE_ONE_PATH);
        fileTwo = new File(ABSOLUTE_FILE_TWO_PATH);
        dirFileOne = new File(ABSOLUTE_NONEMPTY_DIR_FILE_ONE_PATH);
        dirFileTwo = new File(ABSOLUTE_NONEMPTY_DIR_FILE_TWO_PATH);
        nestedDirFile = new File(ABSOLUTE_NESTED_DIR_FILE_PATH);
    }

    public void createTestEnv() {
        testDir.mkdir();
        emptyDir.mkdir();
        nonEmptyDir.mkdir();
        nestedNonEmptyDir.mkdir();

        try {
            fileOne.createNewFile();
            fileTwo.createNewFile();
            dirFileOne.createNewFile();
            dirFileTwo.createNewFile();
            nestedDirFile.createNewFile();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void removeTestEnv() {
        nestedDirFile.delete();
        dirFileOne.delete();
        dirFileTwo.delete();
        fileOne.delete();
        fileTwo.delete();

        emptyDir.delete();
        nestedNonEmptyDir.delete();
        nonEmptyDir.delete();
        testDir.delete();
    }
}

package tdd.bf;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.MvException;
import sg.edu.nus.comp.cs4218.exception.RmException;
import sg.edu.nus.comp.cs4218.impl.app.RmApplication;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;
import tdd.util.FilePermissionTestUtil;
import tdd.util.RmTestUtil;

import java.io.*;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.parser.ArgsParser.ILLEGAL_FLAG_MSG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.LongVariable"})
public class RmApplicationTest {
    private RmApplication rmApplication;
    private RmTestUtil rmTestUtil;
    private FilePermissionTestUtil filePermissionTestUtil;

    private static final String INVALID_OPTION = "-e";
    private static final String EMPTY_FOLDER_OPTION = "-d";
    private static final String RECURSIVE_OPTION = "-r";
    private static final String EMPTY_FOLDER_RECURSIVE_OPTION = "-rd";
    public static final String CURRENT_DIR = ".";
    private static final boolean IS_EMPTY_DIR = true;
    private static final boolean IS_RECURSIVE = true;
    private static final String EXCEPTION_MESSAGE_HEADER = "rm: ";
    private InputStream inputStream;
    private OutputStream outputStream;
    private OutputStream checkingOutputStream;
    private PrintStream checkingPrintStream;//NOPMD
    private String[] expected;
    private String expectedMsg;
    private String[] actual;

    @BeforeEach
    public void setUp() {
        rmApplication = new RmApplication();
        inputStream = null;
        outputStream = null;
        checkingOutputStream = new ByteArrayOutputStream();
        checkingPrintStream = new PrintStream(checkingOutputStream);
        System.setOut(checkingPrintStream);
        rmTestUtil = new RmTestUtil();
        rmTestUtil.createTestEnv();
        filePermissionTestUtil = new FilePermissionTestUtil();
        filePermissionTestUtil.createTestEnv();
    }

    @AfterEach
    public void tearDown() {
        System.setOut(System.out);
        rmTestUtil.removeTestEnv();
        filePermissionTestUtil.removeTestEnv();
    }

    @Test
    public void testRemove_nullIsEmptyFolder_shouldThrowException() {
        Exception exception = assertThrows(Exception.class, () -> rmApplication.remove(null, !IS_RECURSIVE));
        assertEquals(ERR_NULL_ARGS, exception.getMessage());
    }

    @Test
    public void testRemove_nullIsRecursive_shouldThrowException() {
        Exception exception = assertThrows(Exception.class, () -> rmApplication.remove(!IS_EMPTY_DIR, null));
        assertEquals(ERR_NULL_ARGS, exception.getMessage());
    }

    @Test
    public void testRemove_nullFileName_shouldThrowException() {
        String[] nullFileName = null;
        Exception exception = assertThrows(Exception.class, () -> rmApplication.remove(!IS_EMPTY_DIR, !IS_RECURSIVE, nullFileName));
        assertEquals(ERR_NULL_ARGS, exception.getMessage());
    }

    @Test
    public void testRemove_emptyArgument_shouldThrowException() {
        Exception exception = assertThrows(Exception.class, () -> rmApplication.remove(!IS_EMPTY_DIR, !IS_RECURSIVE));
        assertEquals(ERR_MISSING_ARG, exception.getMessage());
    }

    @Test
    public void testRemove_noOption_singleFile_shouldRemove() {
        expected = new String[]{RmTestUtil.EMPTY_DIR, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR};

        assertDoesNotThrow(() -> rmApplication.remove(!IS_EMPTY_DIR, !IS_RECURSIVE, RmTestUtil.RELATIVE_FILE_ONE_PATH));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRemove_noOption_twoFiles_shouldRemoveBothFiles() {
        expected = new String[]{RmTestUtil.EMPTY_DIR, RmTestUtil.NONEMPTY_DIR};

        assertDoesNotThrow(() -> rmApplication.remove(
                !IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RELATIVE_FILE_ONE_PATH,
                RmTestUtil.RELATIVE_FILE_TWO_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRemove_noOption_singleFile_singleEmptyDir_shouldRemoveFileAndPrintErrorMsg() {
        expectedMsg = EXCEPTION_MESSAGE_HEADER
                + RmTestUtil.RELATIVE_EMPTY_DIR_PATH
                + ": "
                + ERR_IS_DIR
                + StringUtils.STRING_NEWLINE;
        expected = new String[]{RmTestUtil.EMPTY_DIR, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR};

        assertThrows(RmException.class, () -> rmApplication.remove(
                !IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RELATIVE_FILE_ONE_PATH,
                RmTestUtil.RELATIVE_EMPTY_DIR_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRemove_invalidDir_shouldPrintErrorMsg() {
        expectedMsg = EXCEPTION_MESSAGE_HEADER +
                RmTestUtil.RELATIVE_INVALID_DIR_PATH
                + ": "
                + ERR_FILE_NOT_FOUND
                + StringUtils.STRING_NEWLINE;

        assertThrows(RmException.class, () -> rmApplication.remove(
                IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RELATIVE_INVALID_DIR_PATH
        ));
    }

    @Test
    public void testRemove_invalidDir_emptyDir_shouldRemoveEmptyDirAndPrintErrorMsg() {
        expectedMsg = EXCEPTION_MESSAGE_HEADER
                + RmTestUtil.RELATIVE_INVALID_DIR_PATH
                + ": "
                + ERR_FILE_NOT_FOUND
                + StringUtils.STRING_NEWLINE;
        expected = new String[]{RmTestUtil.EMPTY_DIR, RmTestUtil.FILE_ONE, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR};

        assertThrows(RmException.class, () -> rmApplication.remove(
                IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RELATIVE_INVALID_DIR_PATH,
                RmTestUtil.RELATIVE_EMPTY_DIR_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRemove_invalidFile_shouldPrintErrorMsg() {
        expectedMsg = EXCEPTION_MESSAGE_HEADER
                + RmTestUtil.RELATIVE_INVALID_FILE_PATH
                + ": "
                + ERR_FILE_NOT_FOUND
                + StringUtils.STRING_NEWLINE;

        assertThrows(RmException.class, () -> rmApplication.remove(
                !IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RELATIVE_INVALID_FILE_PATH
        ));
    }

    @Test
    @Disabled
    @DisplayName("in our assumption, the behaviour depends on the remove file argument order")
    public void testRemove_invalidFile_validFile_shouldRemoveValidFileAndPrintErrorMsg() {
        expectedMsg = EXCEPTION_MESSAGE_HEADER
                + RmTestUtil.RELATIVE_INVALID_FILE_PATH
                + ": "
                + ERR_FILE_NOT_FOUND
                + StringUtils.STRING_NEWLINE;
        expected = new String[]{RmTestUtil.EMPTY_DIR, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR};

        assertDoesNotThrow(() -> rmApplication.remove(
                !IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RELATIVE_INVALID_FILE_PATH,
                RmTestUtil.RELATIVE_FILE_ONE_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
        assertEquals(expectedMsg, checkingOutputStream.toString());
    }

    @Test
    public void testRemove_recursiveOption_validFile_shouldRemoveFile() {
        expected = new String[]{RmTestUtil.EMPTY_DIR, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR};

        assertDoesNotThrow(() -> rmApplication.remove(
                !IS_EMPTY_DIR,
                IS_RECURSIVE,
                RmTestUtil.RELATIVE_FILE_ONE_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRemove_recursiveOption_emptyDir_shouldRemoveDir() {
        expected = new String[]{RmTestUtil.FILE_ONE, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR};

        assertDoesNotThrow(() -> rmApplication.remove(
                !IS_EMPTY_DIR,
                IS_RECURSIVE,
                RmTestUtil.RELATIVE_EMPTY_DIR_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRemove_recursiveOption_nonEmptyDir_shouldRemoveDirAndAllFilesInDir() {
        expected = new String[]{RmTestUtil.EMPTY_DIR, RmTestUtil.FILE_ONE, RmTestUtil.FILE_TWO};

        assertDoesNotThrow(() -> rmApplication.remove(
                !IS_EMPTY_DIR,
                IS_RECURSIVE,
                RmTestUtil.RELATIVE_NONEMPTY_DIR_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRemove_emptyFolderOption_validFile_shouldRemoveFile() {
        expected = new String[]{RmTestUtil.EMPTY_DIR, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR};

        assertDoesNotThrow(() -> rmApplication.remove(
                IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RELATIVE_FILE_ONE_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRemove_emptyFolderOption_emptyDir_shouldRemoveDir() {
        expected = new String[]{RmTestUtil.FILE_ONE, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR};

        assertDoesNotThrow(() -> rmApplication.remove(
                IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RELATIVE_EMPTY_DIR_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRemove_emptyFolderOption_nonEmptyDir_shouldPrintErrorMsg() {
        expectedMsg = EXCEPTION_MESSAGE_HEADER
                + RmTestUtil.RELATIVE_NONEMPTY_DIR_PATH
                + ": "
                + ERR_DIR_NOT_EMPTY
                + StringUtils.STRING_NEWLINE;
        expected = new String[]{
                RmTestUtil.EMPTY_DIR,
                RmTestUtil.FILE_ONE,
                RmTestUtil.FILE_TWO,
                RmTestUtil.NONEMPTY_DIR
        };

        assertThrows(RmException.class, () -> rmApplication.remove(
                IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RELATIVE_NONEMPTY_DIR_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
//        assertEquals(expectedMsg, checkingOutputStream.toString());
    }

    @Test
    public void testRemove_emptyFolderOption_currDir_shouldPrintErrorMsg() {
        expectedMsg = EXCEPTION_MESSAGE_HEADER + ERR_CURR_DIR + StringUtils.STRING_NEWLINE;
        expected = new String[]{
                RmTestUtil.EMPTY_DIR,
                RmTestUtil.FILE_ONE,
                RmTestUtil.FILE_TWO,
                RmTestUtil.NONEMPTY_DIR
        };

        assertThrows(RmException.class, () -> rmApplication.remove(
                IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.RM_TEST_DIR + CURRENT_DIR
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
//        assertEquals(expectedMsg, checkingOutputStream.toString());
    }

    @Test
    public void testRemove_recursiveAndEmptyFolderOption_validFile_shouldRemoveFile() {
        expected = new String[]{RmTestUtil.EMPTY_DIR, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR};

        assertDoesNotThrow(() -> rmApplication.remove(
                IS_EMPTY_DIR,
                IS_RECURSIVE,
                RmTestUtil.RELATIVE_FILE_ONE_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRemove_recursiveAndEmptyFolderOption_emptyDir_shouldRemoveDir() {
        expected = new String[]{RmTestUtil.FILE_ONE, RmTestUtil.FILE_TWO, RmTestUtil.NONEMPTY_DIR};

        assertDoesNotThrow(() -> rmApplication.remove(
                IS_EMPTY_DIR,
                IS_RECURSIVE,
                RmTestUtil.RELATIVE_EMPTY_DIR_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRemove_recursiveAndEmptyFolderOption_nonEmptyDir_shouldRemoveDirAndAllFilesInDir() {
        expected = new String[]{RmTestUtil.EMPTY_DIR, RmTestUtil.FILE_ONE, RmTestUtil.FILE_TWO};

        assertDoesNotThrow(() -> rmApplication.remove(
                !IS_EMPTY_DIR,
                IS_RECURSIVE,
                RmTestUtil.RELATIVE_NONEMPTY_DIR_PATH
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    @Disabled
    @DisplayName("we accept both argument at the same time")
    public void testRemove_recursiveAndEmptyFolderOption_currDir_shouldThrowException() {
        expectedMsg = EXCEPTION_MESSAGE_HEADER + ERR_CURR_DIR + StringUtils.STRING_NEWLINE;
        expected = new String[]{
                RmTestUtil.EMPTY_DIR,
                RmTestUtil.FILE_ONE,
                RmTestUtil.FILE_TWO,
                RmTestUtil.NONEMPTY_DIR
        };

        assertDoesNotThrow(() -> rmApplication.remove(
                IS_EMPTY_DIR,
                IS_RECURSIVE,
                RmTestUtil.RM_TEST_DIR + CURRENT_DIR
        ));
        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
        assertEquals(expectedMsg, checkingOutputStream.toString());
    }

    @Test
    public void testRemove_absoluteFilePath_shouldRemove() {
        assertDoesNotThrow(() -> rmApplication.remove(
                !IS_EMPTY_DIR,
                !IS_RECURSIVE,
                RmTestUtil.ABSOLUTE_NESTED_DIR_FILE_PATH
        ));
        actual = rmTestUtil.nestedNonEmptyDir.list();
        Arrays.sort(actual);

        assertEquals(0, actual.length);
    }

    @Test
    public void testRemove_onReadOnlyDir_shouldPrintErrorMsg() {
        expectedMsg = EXCEPTION_MESSAGE_HEADER
                + FilePermissionTestUtil.READ_ONLY_DIR_PATH
                + ": "
                + ERR_NO_PERM
                + StringUtils.STRING_NEWLINE;

        assertThrows(Exception.class, () -> rmApplication.remove(
                IS_EMPTY_DIR,
                IS_RECURSIVE,
                FilePermissionTestUtil.READ_ONLY_DIR_PATH
        ));
//        assertEquals(expectedMsg, checkingOutputStream.toString());
    }

    @Test
    public void testRemove_onExecuteOnlyDir_shouldPrintErrorMsg() {
        expectedMsg = EXCEPTION_MESSAGE_HEADER
                + FilePermissionTestUtil.EXECUTE_ONLY_DIR_PATH
                + ": "
                + ERR_NO_PERM
                + StringUtils.STRING_NEWLINE;

        assertThrows(Exception.class, () -> rmApplication.remove(
                IS_EMPTY_DIR,
                IS_RECURSIVE,
                FilePermissionTestUtil.EXECUTE_ONLY_DIR_PATH
        ));
//        assertEquals(expectedMsg, checkingOutputStream.toString());
    }

    @Test
    @Disabled
    @DisplayName("we do not have this assumption")
    public void testRemove_onWriteOnlyDir_shouldRemove() {
        expected = new String[]{
                FilePermissionTestUtil.EXECUTE_ONLY_FILE,
                FilePermissionTestUtil.EXECUTE_ONLY_DIR,
                FilePermissionTestUtil.NO_PERMISSION_FILE,
                FilePermissionTestUtil.NO_PERMISSION_DIR,
                FilePermissionTestUtil.NO_WRITE_FILE,
                FilePermissionTestUtil.NO_WRITE_DIR,
                FilePermissionTestUtil.READ_ONLY_FILE,
                FilePermissionTestUtil.READ_ONLY_DIR,
                FilePermissionTestUtil.WRITE_ONLY_FILE
        };

        assertDoesNotThrow(() -> rmApplication.remove(
                IS_EMPTY_DIR,
                IS_RECURSIVE,
                FilePermissionTestUtil.WRITE_ONLY_DIR_PATH
        ));
        actual = filePermissionTestUtil.resourceDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    @Disabled
    @DisplayName("I do not know what the this test means")
    public void testRemove_onWriteOnlyDir_noRecursiveOption_shouldPrintErrorMsg() {
        expectedMsg = EXCEPTION_MESSAGE_HEADER
                + FilePermissionTestUtil.WRITE_ONLY_DIR_PATH
                + ": "
                + ERR_NO_PERM
                + StringUtils.STRING_NEWLINE;

        assertThrows(RmException.class, () -> rmApplication.remove(
                IS_EMPTY_DIR,
                !IS_RECURSIVE,
                FilePermissionTestUtil.WRITE_ONLY_DIR_PATH
        ));
    }

    @Test
    public void testRemove_onReadOnlyFile_shouldPrintErrorMsg() {
        expectedMsg = EXCEPTION_MESSAGE_HEADER
                + FilePermissionTestUtil.READ_ONLY_FILE_PATH
                + ": "
                + ERR_NO_PERM
                + StringUtils.STRING_NEWLINE;

        assertThrows(RmException.class, () -> rmApplication.remove(
                IS_EMPTY_DIR,
                IS_RECURSIVE,
                FilePermissionTestUtil.READ_ONLY_FILE_PATH
        ));
    }

    @Test
    public void testRemove_onExecuteOnlyFile_shouldPrintErrorMsg() {
        expectedMsg = EXCEPTION_MESSAGE_HEADER
                + FilePermissionTestUtil.EXECUTE_ONLY_FILE_PATH
                + ": "
                + ERR_NO_PERM
                + StringUtils.STRING_NEWLINE;

        assertThrows(Exception.class, () -> rmApplication.remove(
                IS_EMPTY_DIR,
                IS_RECURSIVE,
                FilePermissionTestUtil.EXECUTE_ONLY_FILE_PATH
        ));
//        assertEquals(expectedMsg, checkingOutputStream.toString());
    }

    @Test
    public void testRemove_onWriteOnlyFile_shouldRemove() {
        expected = new String[]{
                FilePermissionTestUtil.EXECUTE_ONLY_FILE,
                FilePermissionTestUtil.EXECUTE_ONLY_DIR,
                FilePermissionTestUtil.NO_PERMISSION_FILE,
                FilePermissionTestUtil.NO_PERMISSION_DIR,
                FilePermissionTestUtil.NO_WRITE_FILE,
                FilePermissionTestUtil.NO_WRITE_DIR,
                FilePermissionTestUtil.READ_ONLY_FILE,
                FilePermissionTestUtil.READ_ONLY_DIR,
                FilePermissionTestUtil.WRITE_ONLY_DIR,
        };

        assertDoesNotThrow(() -> rmApplication.remove(
                IS_EMPTY_DIR,
                IS_RECURSIVE,
                FilePermissionTestUtil.WRITE_ONLY_FILE_PATH
        ));
        actual = filePermissionTestUtil.resourceDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRun_nullArgs_shouldThrowException() {
        Exception exception = assertThrows(RmException.class, () -> rmApplication.run(null, inputStream, outputStream));
//        assertEquals(EXCEPTION_MESSAGE_HEADER + ERR_NULL_ARGS, exception.getMessage());
    }

    @Test
    public void testRun_noOptionAndArgument_shouldThrowException() {
        Exception exception = assertThrows(RmException.class, () -> rmApplication.run(new String[0], inputStream, outputStream));
//        assertEquals(EXCEPTION_MESSAGE_HEADER + ERR_NO_ARGS, exception.getMessage());
    }

    @Test
    public void testRun_invalidOptions_shouldThrowException() {
        String[] args = {INVALID_OPTION};

        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> rmApplication.run(args, inputStream, outputStream));
        assertEquals(ILLEGAL_FLAG_MSG + "e", outputStream.toString());
    }

    @Test
    public void testRun_separatedEmptyFolderAndRecursiveOption_emptyDir_shouldRemove() {
        expected = new String[]{
                RmTestUtil.EMPTY_DIR,
                RmTestUtil.FILE_ONE,
                RmTestUtil.FILE_TWO,
        };
        String[] args = {EMPTY_FOLDER_OPTION, RECURSIVE_OPTION, RmTestUtil.RELATIVE_NONEMPTY_DIR_PATH};

        assertDoesNotThrow(() -> rmApplication.run(args, inputStream, outputStream));

        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRun_joinedEmptyFolderAndRecursiveOption_emptyDir_shouldRemove() {
        expected = new String[]{
                RmTestUtil.EMPTY_DIR,
                RmTestUtil.FILE_ONE,
                RmTestUtil.FILE_TWO,
        };
        String[] args = {EMPTY_FOLDER_RECURSIVE_OPTION, RmTestUtil.RELATIVE_NONEMPTY_DIR_PATH};

        assertDoesNotThrow(() -> rmApplication.run(args, inputStream, outputStream));

        actual = rmTestUtil.testDir.list();
        Arrays.sort(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRun_invalidRemoveArguments_shouldThrowException() {
        String[] args = {EMPTY_FOLDER_RECURSIVE_OPTION};

        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> rmApplication.run(args, inputStream, outputStream));
        assertEquals(ERR_MISSING_ARG, outputStream.toString());
    }
}
package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.FindInterface;
import sg.edu.nus.comp.cs4218.exception.FindException;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.app.FindApplication.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class FindApplicationTest {

    private static String folderName = "asset" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "common";
    private static String fileNameA = "A.txt";
    private static String fileNameB = "B.txt";
    private static String fileNameEmpty1 = "empty1.txt";
    private static String fileNameEmpty2 = "empty2.txt";
    private static String subFolderName = "subDir";
    private static String fileNameNotExist = "notExist.txt";
    private static String folderNotExist = "notExist";
    private static String fileNameEmpty3 = "empty3.txt";
    private static String findPrefix = "find: ";
    private static String suffix = "-name";
    private final FindInterface app = new FindApplication();
    private OutputStream outputStream = null;

    @Test
    void testSimpleRun() {
        String[] args = {folderName, suffix, fileNameB};
        String expectResult = folderName + CHAR_FILE_SEP + fileNameB + STRING_NEWLINE;
        outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            app.run(args, System.in, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }

    @Test
    void testFindSingleMatchedFile() {
        String expectResult = folderName + CHAR_FILE_SEP + fileNameA;
        assertDoesNotThrow(() -> {
            String realResult = app.findFolderContent(fileNameA, folderName);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testFindSingleMatchedFolder() {
        String expectResult = folderName + CHAR_FILE_SEP + subFolderName;
        assertDoesNotThrow(() -> {
            String realResult = app.findFolderContent(subFolderName, folderName);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testFindMultipleMatchedFiles() {
        String expectResult = folderName + CHAR_FILE_SEP + subFolderName + CHAR_FILE_SEP + fileNameEmpty3 + STRING_NEWLINE +
                folderName + CHAR_FILE_SEP + fileNameEmpty1 + STRING_NEWLINE +
                folderName + CHAR_FILE_SEP + fileNameEmpty2;
        assertDoesNotThrow(() -> {
            String realResult = app.findFolderContent("empty*", folderName);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testFindNoMatchedFile() {
        String expectResult = "";
        assertDoesNotThrow(() -> {
            String realResult = app.findFolderContent(fileNameNotExist, folderName);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testFindInNotExistFolder() {
        String expectResult = findPrefix + folderNotExist + ": " + ERR_FILE_NOT_FOUND;
        assertDoesNotThrow(() -> {
            String realResult = app.findFolderContent(fileNameA, folderNotExist);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testFindNullFileName() {
        Throwable thrown = assertThrows(FindException.class, () -> {
            app.findFolderContent(null, folderName);
        });
        assertEquals(thrown.getMessage(), findPrefix + NO_FILE);
    }

    @Test
    void testFindEmptyFileName() {
        Throwable thrown = assertThrows(FindException.class, () -> {
            app.findFolderContent("", folderName);
        });
        assertEquals(thrown.getMessage(), findPrefix + NO_FILE);
    }

    @Test
    void testFindNullFolderName() {
        Throwable thrown = assertThrows(FindException.class, () -> {
            app.findFolderContent(fileNameA, null);
        });
        assertEquals(thrown.getMessage(), findPrefix + NO_FOLDER);
    }

    @Test
    void testFindEmptyFolderName() {
        String[] foldNames = {};
        Throwable thrown = assertThrows(FindException.class, () -> {
            app.findFolderContent(fileNameA, foldNames);
        });
        assertEquals(thrown.getMessage(), findPrefix + NO_FOLDER);
    }

    @Test
    void testRunWithNullArgs() {
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(FindException.class, () -> {
            app.run(null, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), findPrefix + NULL_POINTER);
    }

    @Test
    void testRunWithEmptyArgs() {
        String[] args = {""};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(FindException.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), findPrefix + EMPTY_ARG);
    }

    @Test
    void testRunWithNoFileName() {
        String[] args = {folderName, suffix};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(FindException.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), findPrefix + NO_FILE);
    }

    @Test
    void testRunWithNoFolderName() {
        String[] args = {suffix, fileNameA};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(FindException.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), findPrefix + NO_FOLDER);
    }

    @Test
    void testRunWithNullOStream() {
        String[] args = {folderName, suffix, fileNameB};
        Throwable thrown = assertThrows(FindException.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), findPrefix + ERR_NO_OSTREAM);
    }

    @Test
    void testRunWithWrongSuffix() {
        String[] args = {folderName, "-NAME", fileNameA};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(FindException.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), findPrefix + WRONG_FLAG_SUFFIX);
    }

    @Test
    void testRunWithMultipleFiles() {
        String[] args = {folderName, suffix, fileNameA, fileNameB};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(FindException.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), findPrefix + MULTIPLE_FILES);
    }

}
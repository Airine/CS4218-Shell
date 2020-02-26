package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.FindInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.FindException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.app.FindApplication.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class FindApplicationTest {

    private final FindInterface app = new FindApplication();
    private OutputStream outputStream = null;
    private static String folderName = "asset";
    private static String fileNameA = "A.txt";
    private static String fileNameB = "B.txt";
    private static String fileNameEmpty1 = "empty1.txt";
    private static String fileNameEmpty2 = "empty2.txt";
    private static String subFolderName = "subDir";
    private static String fileNameNotExist = "notExist.txt";
    private static String folderNotExist = "notExist";
    private static String fileNameEmpty3 = "empty3.txt";
    private static String findPrefix = "find: ";
    private static String suffix = "find: ";

    @Test
    void run() throws AbstractApplicationException {
        String[] args = {folderName, suffix, fileNameB};
        String expectResult = folderName+"\\"+fileNameB+STRING_NEWLINE;
        outputStream = new ByteArrayOutputStream();
        app.run(args, System.in, outputStream);
        assertEquals(expectResult, outputStream.toString());
    }

    @Test
    void testFindSingleMatchedFile() throws Exception {
        String expectResult = folderName+"\\"+fileNameA;
        String realResult = app.findFolderContent(fileNameA, folderName);
        assertEquals(expectResult, realResult);
    }

    @Test
    void testFindSingleMatchedFolder() throws Exception {
        String expectResult = folderName+"\\"+subFolderName;
        String realResult = app.findFolderContent(subFolderName, folderName);
        assertEquals(expectResult, realResult);
    }

    @Test
    void testFindMultipleMatchedFiles() throws Exception {
        String expectResult = folderName+"\\"+subFolderName+"\\"+fileNameEmpty3+STRING_NEWLINE+
                folderName+"\\"+fileNameEmpty1+STRING_NEWLINE+
                folderName+"\\"+fileNameEmpty2;
        String realResult = app.findFolderContent("empty*", folderName);
        assertEquals(expectResult, realResult);
    }

    @Test
    void testFindNoMatchedFile() throws Exception {
        String expectResult = "";
        String realResult = app.findFolderContent(fileNameNotExist, folderName);
        assertEquals(expectResult, realResult);
    }

    @Test
    void testFindInNotExistFolder() throws Exception {
        String expectResult = findPrefix + folderNotExist + ": " + ERR_FILE_NOT_FOUND;
        String realResult = app.findFolderContent(fileNameA, folderNotExist);
        assertEquals(expectResult, realResult);
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
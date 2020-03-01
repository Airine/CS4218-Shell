package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.SortInterface;
import sg.edu.nus.comp.cs4218.exception.SortException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class SortApplicationTest {

    private static String folderName = "asset" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "common";
    private static String fileNameD = "D.txt";
    private static String fileNameE = "E.txt";
    private static String subDirName = "subDir";
    private static String fileNameEmpty1 = "empty1.txt";
    private static String fileNameNotExist = "notExist.txt";
    private static String sortPrefix = "sort: ";
    private final SortInterface app = new SortApplication();
    private OutputStream outputStream = null;

    @Test
    void testRunWithFiles() {
        String[] args = {folderName + CHAR_FILE_SEP + fileNameD, folderName + CHAR_FILE_SEP + fileNameE};
        String expectResult = "1" + STRING_NEWLINE + "10" + STRING_NEWLINE + "2"
                + STRING_NEWLINE + "A" + STRING_NEWLINE + "b" + STRING_NEWLINE;
        outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            app.run(args, System.in, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }

    @Test
    void testRunWithWrongSuffix() {
        String[] args = {"-a", "-r", "-f"};
        String original = "A" + STRING_NEWLINE + "b" + STRING_NEWLINE + "c" + STRING_NEWLINE + "D";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        String expectResult = "D" + STRING_NEWLINE + "c" + STRING_NEWLINE + "b" + STRING_NEWLINE + "A" + STRING_NEWLINE;
        outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            app.run(args, stdin, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }

    @Test
    void testRunWithEmptyArg() {
        String[] args = {"", "-r", "-f"};
        String original = "A" + STRING_NEWLINE + "b" + STRING_NEWLINE + "c" + STRING_NEWLINE + "D";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        String expectResult = "D" + STRING_NEWLINE + "c" + STRING_NEWLINE + "b" + STRING_NEWLINE + "A" + STRING_NEWLINE;
        outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            app.run(args, stdin, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }

    @Test
    void testRunWithNoFiles() {
        String[] args = {"-nr", "-f"};
        String original = "A" + STRING_NEWLINE + "b" + STRING_NEWLINE + "c" + STRING_NEWLINE + "D";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        String expectResult = "D" + STRING_NEWLINE + "c" + STRING_NEWLINE + "b" + STRING_NEWLINE + "A" + STRING_NEWLINE;
        outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            app.run(args, stdin, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }

    @Test
    void testSortFromEmptyFile() {
        String expectResult = "";
        assertDoesNotThrow(() -> {
            String realResult = app.sortFromFiles(false, false, false, folderName + CHAR_FILE_SEP + fileNameEmpty1);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testSortNumberByCharacterFromFile() {
        String expectResult = "1" + STRING_NEWLINE + "10" + STRING_NEWLINE + "2";
        assertDoesNotThrow(() -> {
            String realResult = app.sortFromFiles(false, false, false, folderName + CHAR_FILE_SEP + fileNameD);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testSortNumberByWordFromFile() {
        String expectResult = "1" + STRING_NEWLINE + "2" + STRING_NEWLINE + "10";
        assertDoesNotThrow(() -> {
            String realResult = app.sortFromFiles(true, false, false, folderName + CHAR_FILE_SEP + fileNameD);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testSortNumbersInReverseOrderFromFile() {
        String expectResult = "2" + STRING_NEWLINE + "10" + STRING_NEWLINE + "1";
        assertDoesNotThrow(() -> {
            String realResult = app.sortFromFiles(false, true, false, folderName + CHAR_FILE_SEP + fileNameD);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testSortLettersFromStdin() {
        String original = "A" + STRING_NEWLINE + "b" + STRING_NEWLINE + "c" + STRING_NEWLINE + "D";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        String expectResult = "A" + STRING_NEWLINE + "D" + STRING_NEWLINE + "b" + STRING_NEWLINE + "c";
        assertDoesNotThrow(() -> {
            String realResult = app.sortFromStdin(false, false, false, stdin);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testSortSameLettersFromStdin() {
        String original = "A1" + STRING_NEWLINE + "A" + STRING_NEWLINE + "A" + STRING_NEWLINE + "A";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        String expectResult = "A" + STRING_NEWLINE + "A" + STRING_NEWLINE + "A" + STRING_NEWLINE + "A1";
        assertDoesNotThrow(() -> {
            String realResult = app.sortFromStdin(true, false, false, stdin);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testSortCaseIndependentLettersFromStdin() {
        String original = "A" + STRING_NEWLINE + "b" + STRING_NEWLINE + "c" + STRING_NEWLINE + "D";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        String expectResult = "A" + STRING_NEWLINE + "b" + STRING_NEWLINE + "c" + STRING_NEWLINE + "D";
        assertDoesNotThrow(() -> {
            String realResult = app.sortFromStdin(false, false, true, stdin);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testSortWithNullIStream() {
        Throwable thrown = assertThrows(Exception.class, () -> {
            app.sortFromStdin(false, false, false, null);
        });
        assertEquals(thrown.getMessage(), ERR_NULL_STREAMS);
    }

    @Test
    void testSortWithNotExistFileName() {
        Throwable thrown = assertThrows(Exception.class, () -> {
            app.sortFromFiles(false, false, false, folderName + CHAR_FILE_SEP + fileNameNotExist);
        });
        assertEquals(thrown.getMessage(), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testSortWithNullFileName() {
        Throwable thrown = assertThrows(Exception.class, () -> {
            app.sortFromFiles(false, false, false, null);
        });
        assertEquals(thrown.getMessage(), ERR_NULL_ARGS);
    }

    @Test
    void testSortWithDirectoryName() {
        Throwable thrown = assertThrows(Exception.class, () -> {
            app.sortFromFiles(false, false, false, folderName + CHAR_FILE_SEP + subDirName);
        });
        assertEquals(thrown.getMessage(), ERR_IS_DIR);
    }

    @Test
    void testRunWithDirectoryFileName() {
        String[] args = {folderName + CHAR_FILE_SEP + subDirName};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(SortException.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), sortPrefix + ERR_IS_DIR);
    }

    @Test
    void testRunWithNullArg() {
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(SortException.class, () -> {
            app.run(null, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), sortPrefix + ERR_NULL_ARGS);
    }

    @Test
    void testRunWithNullOStream() {
        String[] args = {folderName + CHAR_FILE_SEP + fileNameD, folderName + CHAR_FILE_SEP + fileNameE};
        Throwable thrown = assertThrows(SortException.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), sortPrefix + ERR_NULL_STREAMS);
    }

    @Test
    void testRunWithClosedOutputStream() {
        String[] args = {folderName + CHAR_FILE_SEP + fileNameD};
        assertThrows(SortException.class, () -> {
            outputStream = new FileOutputStream(new File(folderName + CHAR_FILE_SEP + fileNameEmpty1));
            IOUtils.closeOutputStream(outputStream);
            app.run(args, System.in, outputStream);
        });
    }
}
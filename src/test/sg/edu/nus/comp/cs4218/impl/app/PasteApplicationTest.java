package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.PasteInterface;
import sg.edu.nus.comp.cs4218.exception.PasteException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;

class PasteApplicationTest {

    private static String folderName = "asset" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "common";
    private static String fileNameA = "A.txt";
    private static String fileNameB = "B.txt";
    private static String fileNameC = "C.txt";
    private static String fileNameEmpty1 = "empty1.txt";
    private static String fileNameEmpty2 = "empty2.txt";
    private static String subDirName = "subDir";
    private static String fileNameNotExist = "notExist.txt";
    private static String pastePrefix = "paste: ";
    private final PasteInterface app = new PasteApplication();
    private OutputStream outputStream = null;

    @Test
    void testMergeStdin() {
        String original = "1" + STRING_NEWLINE + "2" + STRING_NEWLINE + "3" + STRING_NEWLINE + "4";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        String expectResult = "1" + STRING_NEWLINE + "2" + STRING_NEWLINE + "3" + STRING_NEWLINE + "4";
        assertDoesNotThrow(() -> {
            String realResult = app.mergeStdin(stdin);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testMergeTwoEmptyFiles() {
        String expectResult = "";
        assertDoesNotThrow(() -> {
            String realResult = app.mergeFile(folderName + CHAR_FILE_SEP + fileNameEmpty1, folderName + CHAR_FILE_SEP + fileNameEmpty2);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testMergeEmptyFileWithNonEmptyFile() {
        String expectResult = CHAR_TAB + "A" + STRING_NEWLINE +
                CHAR_TAB + "B" + STRING_NEWLINE +
                CHAR_TAB + "C" + STRING_NEWLINE +
                CHAR_TAB + "D";
        assertDoesNotThrow(() -> {
            String realResult = app.mergeFile(folderName + CHAR_FILE_SEP + fileNameEmpty1, folderName + CHAR_FILE_SEP + fileNameA);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testMergeNonEmptyFileWithEmptyFile() {
        String expectResult = "A" + CHAR_TAB + STRING_NEWLINE +
                "B" + CHAR_TAB + STRING_NEWLINE +
                "C" + CHAR_TAB + STRING_NEWLINE +
                "D" + CHAR_TAB;
        assertDoesNotThrow(() -> {
            String realResult = app.mergeFile(folderName + CHAR_FILE_SEP + fileNameA, folderName + CHAR_FILE_SEP + fileNameEmpty2);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testMergeOneFile() {
        String expectResult = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D";
        assertDoesNotThrow(() -> {
            String realResult = app.mergeFile(folderName + CHAR_FILE_SEP + fileNameA);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testMergeTwoFilesWithSameLineNumber() {
        String expectResult = "A" + CHAR_TAB + "1" + STRING_NEWLINE +
                "B" + CHAR_TAB + "2" + STRING_NEWLINE +
                "C" + CHAR_TAB + "3" + STRING_NEWLINE +
                "D" + CHAR_TAB + "4";
        assertDoesNotThrow(() -> {
            String realResult = app.mergeFile(folderName + CHAR_FILE_SEP + fileNameA, folderName + CHAR_FILE_SEP + fileNameB);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testMergeTwoFilesWithDifferentLineNumber() {
        String expectResult = "A" + CHAR_TAB + "1" + STRING_NEWLINE +
                "B" + CHAR_TAB + "3" + STRING_NEWLINE +
                "C" + CHAR_TAB + "5" + STRING_NEWLINE +
                "D" + CHAR_TAB + "7" + STRING_NEWLINE +
                CHAR_TAB + "9";
        assertDoesNotThrow(() -> {
            String realResult = app.mergeFile(folderName + CHAR_FILE_SEP + fileNameA, folderName + CHAR_FILE_SEP + fileNameC);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testMergeThreeFilesWithDifferentLineNumber() {
        String expectResult = "A" + CHAR_TAB + "1" + CHAR_TAB + "1" + STRING_NEWLINE +
                "B" + CHAR_TAB + "2" + CHAR_TAB + "3" + STRING_NEWLINE +
                "C" + CHAR_TAB + "3" + CHAR_TAB + "5" + STRING_NEWLINE +
                "D" + CHAR_TAB + "4" + CHAR_TAB + "7" + STRING_NEWLINE +
                CHAR_TAB + CHAR_TAB + "9";
        assertDoesNotThrow(() -> {
            String realResult = app.mergeFile(folderName + CHAR_FILE_SEP + fileNameA, folderName + CHAR_FILE_SEP + fileNameB,
                    folderName + CHAR_FILE_SEP + fileNameC);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testMergeStdinWithFiles() {
        String original = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        String expectResult = "A" + CHAR_TAB + "1" + CHAR_TAB + "1" + STRING_NEWLINE +
                "B" + CHAR_TAB + "2" + CHAR_TAB + "3" + STRING_NEWLINE +
                "C" + CHAR_TAB + "3" + CHAR_TAB + "5" + STRING_NEWLINE +
                "D" + CHAR_TAB + "4" + CHAR_TAB + "7" + STRING_NEWLINE +
                CHAR_TAB + CHAR_TAB + "9";
        assertDoesNotThrow(() -> {
            String realResult = app.mergeFileAndStdin(stdin, "-", folderName + CHAR_FILE_SEP + fileNameB,
                    folderName + CHAR_FILE_SEP + fileNameC);
            assertEquals(expectResult, realResult);
        });
    }

    @Test
    void testRunWithOnlyStdin() {
        String[] args = {"-"};
        String original = "1" + STRING_NEWLINE + "2" + STRING_NEWLINE + "3" + STRING_NEWLINE + "4";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        String expectResult = "1" + STRING_NEWLINE + "2" + STRING_NEWLINE + "3" + STRING_NEWLINE + "4";
        outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            app.run(args, stdin, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }

    @Test
    void testRunWithOnlyFiles() {
        String[] args = {folderName + CHAR_FILE_SEP + fileNameA, folderName + CHAR_FILE_SEP + fileNameB};
        String expectResult = "A" + CHAR_TAB + "1" + STRING_NEWLINE +
                "B" + CHAR_TAB + "2" + STRING_NEWLINE +
                "C" + CHAR_TAB + "3" + STRING_NEWLINE +
                "D" + CHAR_TAB + "4";
        outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            app.run(args, System.in, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }

    @Test
    void testRunWithStdinAndFiles() {
        String[] args = {"-", folderName + CHAR_FILE_SEP + fileNameB, folderName + CHAR_FILE_SEP + fileNameC};
        String original = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        String expectResult = "A" + CHAR_TAB + "1" + CHAR_TAB + "1" + STRING_NEWLINE +
                "B" + CHAR_TAB + "2" + CHAR_TAB + "3" + STRING_NEWLINE +
                "C" + CHAR_TAB + "3" + CHAR_TAB + "5" + STRING_NEWLINE +
                "D" + CHAR_TAB + "4" + CHAR_TAB + "7" + STRING_NEWLINE +
                CHAR_TAB + CHAR_TAB + "9";
        outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            app.run(args, stdin, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }

    @Test
    void testRunWithTwoStdinsAndOneFile() {
        String[] args = {"-", folderName + CHAR_FILE_SEP + fileNameB, "-"};
        String original = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        String expectResult = "A" + CHAR_TAB + "1" + CHAR_TAB + "B" + STRING_NEWLINE +
                "C" + CHAR_TAB + "2" + CHAR_TAB + "D" + STRING_NEWLINE +
                CHAR_TAB + "3" + CHAR_TAB + STRING_NEWLINE +
                CHAR_TAB + "4" + CHAR_TAB;
        outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            app.run(args, stdin, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }

    @Test
    void testRunWithDirectoryFileName() {
        String[] args = {folderName + CHAR_FILE_SEP + subDirName, folderName + CHAR_FILE_SEP + fileNameA};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(PasteException.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertTrue(thrown.getMessage().contains(ERR_IS_DIR));
    }

    @Test
    void testRunWithNotExistFileName() {
        String[] args = {folderName + CHAR_FILE_SEP + fileNameNotExist, folderName + CHAR_FILE_SEP + fileNameA};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(PasteException.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), pastePrefix +
                folderName + CHAR_FILE_SEP + fileNameNotExist + " " + ERR_FILE_NOT_FOUND);
    }

    @Test
    void testRunWithClosedOutputStream() {
        String[] args = {folderName + CHAR_FILE_SEP + fileNameA};
        Throwable thrown = assertThrows(PasteException.class, () -> {
            outputStream = new FileOutputStream(new File(folderName + CHAR_FILE_SEP + fileNameEmpty1));
            IOUtils.closeOutputStream(outputStream);
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), pastePrefix + ERR_WRITE_STREAM);
    }

    @Test
    void testRunWithNullArgs() {
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(PasteException.class, () -> {
            app.run(null, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), pastePrefix + ERR_NULL_ARGS);
    }

    @Test
    void testRunWithNullOStream() {
        String[] args = {"a", "b"};
        Throwable thrown = assertThrows(PasteException.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), pastePrefix + ERR_NO_OSTREAM);
    }

    @Test
    void testRunWithNullIStream() {
        String[] args = {"-", "b"};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(PasteException.class, () -> {
            app.run(args, null, outputStream);
        });
        assertEquals(thrown.getMessage(), pastePrefix + ERR_NO_ISTREAM);
    }

    @Test
    void testMergeWithNullFileName() {
        Throwable thrown = assertThrows(PasteException.class, () -> {
            app.mergeFile(null, folderName + CHAR_FILE_SEP + fileNameA);
        });
        assertEquals(thrown.getMessage(), pastePrefix + ERR_NULL_ARGS);
    }

    @Test
    void testMergeWithNullIStream() {
        Throwable thrown = assertThrows(PasteException.class, () -> {
            app.mergeStdin(null);
        });
        assertEquals(thrown.getMessage(), pastePrefix + ERR_NO_ISTREAM);
    }

    @Test
    void testMergeFileWithNullArgs() {
        Throwable thrown = assertThrows(PasteException.class, () -> {
            app.mergeFile(null);
        });
        assertEquals(thrown.getMessage(), pastePrefix + ERR_NULL_ARGS);
    }

}
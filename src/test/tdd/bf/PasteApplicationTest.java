package tdd.bf;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.PasteException;
import sg.edu.nus.comp.cs4218.impl.app.PasteApplication;
import tdd.util.StdOutStubIOExceptionOnWrite;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.AvoidDuplicateLiterals"})
class PasteApplicationTest {
    private static PasteApplication pasteApp;
    private static final Path DIRECTORY = Paths.get("src", "test", "tdd", "util", "dummyTestFolder",
            "PasteTestFolder");
    private static final String ABSOLUTE_PATH = DIRECTORY.toFile().getAbsolutePath();
    private static final String DASH = "-";
    private static final String PASTE = "paste: ";
    private static final String FILE_1 = ABSOLUTE_PATH + "/file1.txt";
    private static final String FILE_2 = ABSOLUTE_PATH + "/file2.txt";
    private static final String FILE_3 = ABSOLUTE_PATH + "/file3.txt";
    private static final String FILE_1_1 = ABSOLUTE_PATH + "/file1_1.txt";
    private static final String FILE_1_1_1 = ABSOLUTE_PATH + "/file1_1_1.txt";
    private static final String FILE_1_2 = ABSOLUTE_PATH + "/file1_2.txt";
    private static final String FILE_1_2_3 = ABSOLUTE_PATH + "/file1_2_3.txt";
    private static final String FILE_2_1 = ABSOLUTE_PATH + "/file2_1.txt";
    private static final String FILE_SINGLE = ABSOLUTE_PATH + "/fileSingle.txt";
    private static final String FILE_DOUBLE = ABSOLUTE_PATH + "/fileDouble.txt";
    private static final String FILE_TRIPLE = ABSOLUTE_PATH + "/fileTriple.txt";
    private static final String FILE_SIN_DOU = ABSOLUTE_PATH + "/fileSingle_Double.txt";
    private static final String FILE_DOU_SIN = ABSOLUTE_PATH + "/fileDouble_Single.txt";
    private static final String FILE_DOU_SIN_TRI = ABSOLUTE_PATH + "/fileDouble_Single_Triple.txt";
    private static final String FILE_MULTIPLE = ABSOLUTE_PATH + "/fileMultiple.txt";
    private static final String FILE_NEW_PATH = ABSOLUTE_PATH + "/fileNewLine.txt";
    private static final String FILE_NEW_TRI_2 = ABSOLUTE_PATH + "/fileNewLine_Triple_2.txt";
    private static final String FILE_NEW_3 = ABSOLUTE_PATH + "/fileNewLine_3.txt";
    private static final String FILE_MS_1 = ABSOLUTE_PATH + "/fileMultipleStdin_1.txt";
    private static final String FILE_MS_2 = ABSOLUTE_PATH + "/fileMultipleStdin_2.txt";

    @BeforeAll
    public static void setUp() {
        pasteApp = new PasteApplication();
    }

    private String getExpectedResult(String filePath) throws Exception {
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        }
        return String.join(STRING_NEWLINE, result);
    }

    @Test
    public void testMergeStdin_nullStdin_throwPasteException() {
        String expected = PASTE + ERR_NO_ISTREAM;
        String thrown = assertThrows(PasteException.class, () -> pasteApp.mergeStdin(null)).getMessage();
        assertEquals(expected, thrown);
    }

    @Test
    public void testMergeStdin_singleLine_noTabAdded() throws Exception {
        try (InputStream inStream = new FileInputStream(FILE_SINGLE)) {
            String expected = getExpectedResult(FILE_SINGLE);
            assertEquals(expected, pasteApp.mergeStdin(inStream));
        }
    }

    @Test
    public void testMergeStdin_multipleLine() throws Exception {
        // Double lines
        try (InputStream inStream = new FileInputStream(FILE_DOUBLE)) {
            String expected = "Double"+STRING_NEWLINE+"double";
            assertEquals(expected, pasteApp.mergeStdin(inStream));
        }

        // Triple lines
        try (InputStream inStream = new FileInputStream(FILE_TRIPLE)) {
            String expected = "Triple"+STRING_NEWLINE+"triple"+STRING_NEWLINE+"3ple";
            assertEquals(expected, pasteApp.mergeStdin(inStream));
        }

        // Multiple lines with line that have space
        try (InputStream inStream = new FileInputStream(FILE_MULTIPLE)) {
            String expected = "qaz"+STRING_NEWLINE+"wsx"+STRING_NEWLINE+"edc"+STRING_NEWLINE+
                    "rfv"+STRING_NEWLINE+"tgb"+STRING_NEWLINE+"yhn"+STRING_NEWLINE+"ujm"+STRING_NEWLINE+
                    "ik  ol p"+STRING_NEWLINE+"tab";
            assertEquals(expected, pasteApp.mergeStdin(inStream));
        }

        // Newline
        try (InputStream inStream = new FileInputStream(FILE_NEW_PATH)) {
            String expected = STRING_NEWLINE+STRING_NEWLINE+STRING_NEWLINE;
            assertEquals(expected, pasteApp.mergeStdin(inStream));
        }
    }

    @Test
    public void testMergeFile_nullFile_throwException() {
        String expected = PASTE + ERR_NULL_ARGS;
        String thrown = assertThrows(Exception.class, () -> pasteApp.mergeFile(null)).getMessage();
        assertEquals(expected, thrown);
    }

    @Test
    public void testMergeFile_singleFile_noTabAdded() throws Exception {
        // Single line
        String expected = getExpectedResult(FILE_SINGLE);
        assertEquals(expected, pasteApp.mergeFile(FILE_SINGLE));

        // Double lines
        expected = getExpectedResult(FILE_DOUBLE);
        assertEquals(expected, pasteApp.mergeFile(FILE_DOUBLE));

        // Triple lines
        expected = getExpectedResult(FILE_TRIPLE);
        assertEquals(expected, pasteApp.mergeFile(FILE_TRIPLE));

        // Multiple lines
        expected = getExpectedResult(FILE_MULTIPLE);
        assertEquals(expected, pasteApp.mergeFile(FILE_MULTIPLE));

        // Newline only
        expected = getExpectedResult(FILE_NEW_PATH);
        assertEquals(expected, pasteApp.mergeFile(FILE_NEW_PATH));
    }

    @Test
    public void testMergeFile_multipleFiles_sameFile() throws Exception {
        // Double files
        String expected = getExpectedResult(FILE_1_1);
        assertEquals(expected, pasteApp.mergeFile(FILE_1, FILE_1));

        // Triple files
        expected = getExpectedResult(FILE_1_1_1);
        assertEquals(expected, pasteApp.mergeFile(FILE_1, FILE_1, FILE_1));

        // Newline
        expected = "\t" + System.lineSeparator() + "\t" + System.lineSeparator() + "\t" + System.lineSeparator() + "\t";
        assertEquals(expected, pasteApp.mergeFile(FILE_NEW_PATH, FILE_NEW_PATH));
    }

    @Test
    public void testMergeFile_multipleFiles_diffFilesSameNumOfLines() throws Exception {
        // Double files
        String expected = getExpectedResult(FILE_1_2);
        assertEquals(expected, pasteApp.mergeFile(FILE_1, FILE_2));

        // Triple files
        expected = getExpectedResult(FILE_1_2_3);
        assertEquals(expected, pasteApp.mergeFile(FILE_1, FILE_2, FILE_3));

        // Double files with newline
        expected = getExpectedResult(FILE_NEW_3);
        assertEquals(expected, pasteApp.mergeFile(FILE_NEW_PATH, FILE_3));
    }

    @Test
    public void testMergeFile_multipleFiles_diffFilesDiffNumOfLines() throws Exception {
        // Double files
        String expected = getExpectedResult(FILE_SIN_DOU);
        assertEquals(expected, pasteApp.mergeFile(FILE_SINGLE, FILE_DOUBLE));

        // Triple files
        expected = getExpectedResult(FILE_DOU_SIN_TRI);
        assertEquals(expected, pasteApp.mergeFile(FILE_DOUBLE, FILE_SINGLE, FILE_TRIPLE));

        // Triple files with newline
        expected = getExpectedResult(FILE_NEW_TRI_2);
        assertEquals(expected, pasteApp.mergeFile(FILE_NEW_PATH, FILE_TRIPLE, FILE_2));
    }

    @Test
    public void testMergeFile_fileErrorMsg_fileNotFound() {
        // Single file not found
        String expected = PASTE + "fileNotFound.txt " + ERR_FILE_NOT_FOUND;
        String thrown = assertThrows(PasteException.class,
                () -> pasteApp.mergeFile("fileNotFound.txt")).getMessage();
        assertEquals(expected, thrown);

        // Single file not found with other files
        thrown = assertThrows(PasteException.class,
                () -> pasteApp.mergeFile("fileNotFound.txt", FILE_2)).getMessage();
        assertEquals(expected, thrown);

        // Multiple files not found
        expected = PASTE + "fileNotFound1.txt " + ERR_FILE_NOT_FOUND;
        thrown = assertThrows(PasteException.class,
                () -> pasteApp.mergeFile("fileNotFound1.txt", "fileNotFound2.txt",
                        "fileNotFound3.txt", "fileNotFound4.txt")).getMessage();
        assertEquals(expected, thrown);

        // Multiple files not found with other files
        thrown = assertThrows(PasteException.class,
                () -> pasteApp.mergeFile("fileNotFound1.txt", FILE_2, "fileNotFound2.txt",
                        "fileNotFound3.txt", FILE_SINGLE, "fileNotFound4.txt")).getMessage();
        assertEquals(expected, thrown);
    }

    @Test
    public void testMergeFile_fileErrorMsg_isDirectory() {
        // Single directory
        String expected = PASTE + ABSOLUTE_PATH + " " + ERR_IS_DIR;
        String thrown = assertThrows(PasteException.class,
                () -> pasteApp.mergeFile(ABSOLUTE_PATH)).getMessage();
        assertEquals(expected, thrown);

        // Single directory with other files
        thrown = assertThrows(PasteException.class,
                () -> pasteApp.mergeFile(ABSOLUTE_PATH, FILE_2)).getMessage();
        assertEquals(expected, thrown);

        // Multiple directories
        expected = PASTE + ABSOLUTE_PATH + " " + ERR_IS_DIR;
        thrown = assertThrows(PasteException.class,
                () -> pasteApp.mergeFile(ABSOLUTE_PATH, ABSOLUTE_PATH,
                        ABSOLUTE_PATH, ABSOLUTE_PATH)).getMessage();
        assertEquals(expected, thrown);

        // Multiple directories with other files
        thrown = assertThrows(PasteException.class,
                () ->  pasteApp.mergeFile(ABSOLUTE_PATH, FILE_NEW_PATH, ABSOLUTE_PATH,
                        ABSOLUTE_PATH, FILE_3, ABSOLUTE_PATH, FILE_2)).getMessage();
        assertEquals(expected, thrown);
    }

    @Test
    public void testMergeFileAndStdin_nullStdin_throwPasteException() {
        String expected = PASTE + ERR_NO_ISTREAM;
        String thrown = assertThrows(PasteException.class,
                () -> pasteApp.mergeFileAndStdin(null, "")).getMessage();
        assertEquals(expected, thrown);
    }

    @Test
    public void testMergeFileAndStdin_nullFile_throwException() {
        String original = "abcd";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        String thrown = assertThrows(Exception.class,
                () -> pasteApp.mergeFileAndStdin(stdin, null)).getMessage();
        assertEquals(PASTE + ERR_NULL_ARGS, thrown);
    }

    @Test
    public void testMergeFileAndStdin_nullStdinWithNullFile_throwPasteException() {
        String expected = PASTE + ERR_NO_ISTREAM;
        String thrown = assertThrows(PasteException.class,
                () -> pasteApp.mergeFileAndStdin(null, null)).getMessage();
        assertEquals(expected, thrown);
    }

    @Test
    public void testMergeFileAndStdin_stdinOnly_singleLine_noTabAdded() throws Exception {
        try (InputStream inStream = new FileInputStream(FILE_SINGLE)) {
            String expected = getExpectedResult(FILE_SINGLE);
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream, DASH));
        }
    }

    @Test
    public void testMergeFileAndStdin_stdinOnly_multipleLine() throws Exception {
        // Double lines
        try (InputStream inStream = new FileInputStream(FILE_DOUBLE)) {
            String expected = "Double"+STRING_NEWLINE+"double";
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream, DASH));
        }

        // Triple lines
        try (InputStream inStream = new FileInputStream(FILE_TRIPLE)) {
            String expected = "Triple"+STRING_NEWLINE+"triple"+STRING_NEWLINE+"3ple";
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream, DASH));
        }

        // Multiple lines with line that have space
        try (InputStream inStream = new FileInputStream(FILE_MULTIPLE)) {
            String expected = "qaz"+STRING_NEWLINE+"wsx"+STRING_NEWLINE+"edc"+STRING_NEWLINE+
                    "rfv"+STRING_NEWLINE+"tgb"+STRING_NEWLINE+"yhn"+STRING_NEWLINE+"ujm"+STRING_NEWLINE+
                    "ik  ol p"+STRING_NEWLINE+"tab";
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream, DASH));
        }

        // Newline
        try (InputStream inStream = new FileInputStream(FILE_NEW_PATH)) {
            String expected = STRING_NEWLINE+STRING_NEWLINE+STRING_NEWLINE;
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream, DASH));
        }
    }


    @Test
    public void testMergeFileAndStdin_stdinWithSingleFile_sameFile() throws Exception {
        // Stdin Index at 0
        try (InputStream inStream = new FileInputStream(FILE_1)) {
            String expected = getExpectedResult(FILE_1_1);
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream, DASH, FILE_1));
        }

        // Stdin Index at 1
        try (InputStream inStream = new FileInputStream(FILE_1)) {
            String expected = getExpectedResult(FILE_1_1);
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream, FILE_1, DASH));
        }
    }

    @Test
    public void testMergeFileAndStdin_stdinWithSingleFile_diffFileSameNumOfLines() throws Exception {
        // Stdin Index at 0
        try (InputStream inStream = new FileInputStream(FILE_1)) {
            String expected = getExpectedResult(FILE_1_2);
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream, DASH, FILE_2));
        }

        // Stdin Index at 1
        try (InputStream inStream = new FileInputStream(FILE_1)) {
            String expected = getExpectedResult(FILE_2_1);
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream, FILE_2, DASH));
        }
    }

    @Test
    public void testMergeFileAndStdin_stdinWithSingleFile_diffFileDiffNumOfLines() throws Exception {
        // Stdin Index at 0
        try (InputStream inStream = new FileInputStream(FILE_SINGLE)) {
            String expected = getExpectedResult(FILE_SIN_DOU);
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream, DASH, FILE_DOUBLE));
        }

        // Stdin Index at 1
        try (InputStream inStream = new FileInputStream(FILE_SINGLE)) {
            String expected = getExpectedResult(FILE_DOU_SIN) + "\t";
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream,FILE_DOUBLE, DASH));
        }
    }

    @Test
    public void testMergeFileAndStdin_stdinWithMultipleFile_sameFile() throws Exception {
        // Double files, stdin Index at 0
        try (InputStream inStream = new FileInputStream(FILE_1)) {
            String expected = getExpectedResult(FILE_1_1_1);
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream,DASH, FILE_1, FILE_1));
        }

        // Double files, stdin Index at 1
        try (InputStream inStream = new FileInputStream(FILE_1)) {
            String expected = getExpectedResult(FILE_1_1_1);
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream,FILE_1, DASH, FILE_1));
        }

        // Double files, stdin Index at 2
        try (InputStream inStream = new FileInputStream(FILE_1)) {
            String expected = getExpectedResult(FILE_1_1_1);
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream,FILE_1, FILE_1, DASH));
        }
    }

    @Test
    public void testMergeFileAndStdin_stdinWithMultipleFile_diffFileSameNumOfLines() throws Exception {
        // Double files, stdin Index at 0
        try (InputStream inStream = new FileInputStream(FILE_1)) {
            String expected = getExpectedResult(FILE_1_2_3);
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream,DASH, FILE_2, FILE_3));
        }

        // Double files, stdin Index at 1
        try (InputStream inStream = new FileInputStream(FILE_2)) {
            String expected = getExpectedResult(FILE_1_2_3);
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream,FILE_1, DASH, FILE_3));
        }

        // Double files, stdin Index at 2
        try (InputStream inStream = new FileInputStream(FILE_3)) {
            String expected = getExpectedResult(FILE_1_2_3);
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream,FILE_1, FILE_2, DASH));
        }
    }

    @Test
    public void testMergeFileAndStdin_stdinWithMultipleFile_diffFileDiffNumOfLines() throws Exception {
        // Double files, stdin Index at 0
        try (InputStream inStream = new FileInputStream(FILE_DOUBLE)) {
            String expected = getExpectedResult(FILE_DOU_SIN_TRI);
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream, DASH, FILE_SINGLE, FILE_TRIPLE));
        }

        // Double files, stdin Index at 1
        try (InputStream inStream = new FileInputStream(FILE_SINGLE)) {
            String expected = getExpectedResult(FILE_DOU_SIN_TRI);
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream, FILE_DOUBLE, DASH, FILE_TRIPLE));
        }

        // Double files, stdin Index at 2
        try (InputStream inStream = new FileInputStream(FILE_2)) {
            String expected = getExpectedResult(FILE_NEW_TRI_2);
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream, FILE_NEW_PATH, FILE_TRIPLE, DASH));
        }
    }

    @Test
    public void testMergeFileAndStdin_multipleStdinWithNoFile() throws Exception {
        try (InputStream inStream = new FileInputStream(FILE_1)) {
            String expected = getExpectedResult(FILE_MS_1);
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream, DASH, DASH));
        }
    }

    @Test
    public void testMergeFileAndStdin_multipleStdinWithSingleFile() throws Exception {
        try (InputStream inStream = new FileInputStream(FILE_SINGLE)) {
            String expected = "Single\tSingle\t";
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream, DASH, FILE_SINGLE, DASH));
        }

        try (InputStream inStream = new FileInputStream(FILE_DOUBLE)) {
            String expected = "Double\tSingle\tdouble";
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream, DASH, FILE_SINGLE, DASH));
        }
    }

    @Test
    public void testMergeFileAndStdin_multipleStdinWithMultipleFile() throws Exception {
        try (InputStream inStream = new FileInputStream(FILE_1)) {
            String expected = getExpectedResult(FILE_MS_2);
            assertEquals(expected, pasteApp.mergeFileAndStdin(inStream, DASH, FILE_SINGLE, DASH, FILE_TRIPLE));
        }
    }

    @Test
    public void testMergeFileAndStdin_fileErrorMsg_fileNotFound() {
        String original = "abcd";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        // Single file not found
        String expected = PASTE + "fileNotFound.txt " + ERR_FILE_NOT_FOUND;
        String thrown = assertThrows(PasteException.class,
                () -> pasteApp.mergeFileAndStdin(stdin,"fileNotFound.txt", DASH)).getMessage();
        assertEquals(expected, thrown);

        // Single file not found with other files
        thrown = assertThrows(PasteException.class,
                () ->  pasteApp.mergeFileAndStdin(stdin,DASH, "fileNotFound.txt", FILE_2)).getMessage();
        assertEquals(expected, thrown);

        // Multiple files not found
        expected = PASTE + "fileNotFound1.txt " + ERR_FILE_NOT_FOUND;
        thrown = assertThrows(PasteException.class,
                () ->  pasteApp.mergeFileAndStdin(stdin,"fileNotFound1.txt",
                        "fileNotFound2.txt", "fileNotFound3.txt", "fileNotFound4.txt", DASH)).getMessage();
        assertEquals(expected, thrown);

        // Multiple files not found with other files
        thrown = assertThrows(PasteException.class,
                () ->  pasteApp.mergeFileAndStdin(stdin,"fileNotFound1.txt",
                        FILE_2, "fileNotFound2.txt", DASH, "fileNotFound3.txt",
                        FILE_SINGLE, "fileNotFound4.txt")).getMessage();
        assertEquals(expected, thrown);
    }

    @Test
    public void testMergeFileAndStdin_fileErrorMsg_isDirectory(){
        String original = "abcd";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        // Single directory
        String expected = PASTE + ABSOLUTE_PATH + " " + ERR_IS_DIR;
        String thrown = assertThrows(PasteException.class,
                () -> pasteApp.mergeFileAndStdin(stdin, ABSOLUTE_PATH, DASH)).getMessage();
        assertEquals(expected, thrown);

        // Single directory with other files
        thrown = assertThrows(PasteException.class,
                () -> pasteApp.mergeFileAndStdin(stdin, ABSOLUTE_PATH, FILE_2, DASH)).getMessage();
        assertEquals(expected, thrown);

        // Multiple directories
        expected = PASTE + ABSOLUTE_PATH + " " + ERR_IS_DIR;
        thrown = assertThrows(PasteException.class,
                () -> pasteApp.mergeFileAndStdin(stdin,ABSOLUTE_PATH,
                        ABSOLUTE_PATH, DASH, ABSOLUTE_PATH, ABSOLUTE_PATH)).getMessage();
        assertEquals(expected, thrown);

        // Multiple directories with other files
        thrown = assertThrows(PasteException.class,
                () -> pasteApp.mergeFileAndStdin(stdin, ABSOLUTE_PATH,
                        FILE_NEW_PATH, ABSOLUTE_PATH, ABSOLUTE_PATH, FILE_3,
                        DASH, ABSOLUTE_PATH, FILE_2)).getMessage();
        assertEquals(expected, thrown);
    }

    @Test
    public void testRun_nullStdinWithEmptyArgs_throwPasteException() {
        String expected = PASTE + ERR_NULL_ARGS;
        String thrown = assertThrows(PasteException.class, () -> pasteApp.run(new String[]{}, null, System.out))
                .getMessage();
        assertEquals(expected, thrown);

        thrown = assertThrows(PasteException.class, () -> pasteApp.run(new String[]{null}, null, System.out))
                .getMessage();
        assertEquals(expected, thrown);
    }

    @Test
    public void testRun_nullStdinWithNonEmptyArgs_noPasteException() {
        assertDoesNotThrow(() -> pasteApp.run(new String[]{FILE_1}, null, System.out));
    }

    @Test
    public void testRun_nullStdinWithSingleDash_throwPasteException() {
        String expected = PASTE + ERR_NO_ISTREAM;
        String thrown = assertThrows(PasteException.class,
                () -> pasteApp.run(new String[]{"-"}, null, System.out)).getMessage();
        assertEquals(expected, thrown);
    }

    @Test
    public void testRun_nullStdinWithSingleDashAndFiles_throwPasteException() {
        String expected = PASTE + ERR_NO_ISTREAM;
        String thrown = assertThrows(PasteException.class,
                () -> pasteApp.run(new String[]{"-", "randomFile", "test"}, null, System.out)).getMessage();
        assertEquals(expected, thrown);
    }

    @Test
    public void testRun_nullStdout_throwPasteException() {
        String expected = PASTE + ERR_NO_OSTREAM;
        String thrown = assertThrows(PasteException.class, () -> pasteApp.run(new String[]{}, System.in, null))
                .getMessage();
        assertEquals(expected, thrown);
    }

    @Test
    public void testRun_nullStdoutWithNullStdin_throwPasteException() {
        String expected = PASTE + ERR_NO_OSTREAM;
        String thrown = assertThrows(PasteException.class, () -> pasteApp.run(new String[]{}, null, null))
                .getMessage();
        assertEquals(expected, thrown);
    }

    @Test
    public void testRun_stdoutError_throwPasteException() throws Exception {
        try (InputStream inStream = new FileInputStream(FILE_SINGLE)) {
            String expected = PASTE + ERR_WRITE_STREAM;
            String thrown = assertThrows(PasteException.class, () -> pasteApp.run(new String[]{"-"}, inStream,
                    new StdOutStubIOExceptionOnWrite()))
                    .getMessage();
            assertEquals(expected, thrown);
        }
    }

    /*
    @Test
    public void testMergeFile_fileErrorMsg_noPerm() throws Exception {
        // Single no permission
        String expected = "paste: " + FILE_NO_PERM + " " + ERR_NO_PERM;
        String actual = pasteApp.mergeFile(FILE_NO_PERM);
        assertEquals(expected, pasteApp.mergeFile(FILE_NO_PERM));

        // Single no permission with other files
        assertEquals(expected, pasteApp.mergeFile(FILE_NO_PERM, FILE_2));

        // Multiple no permission
        expected = "paste: " + FILE_NO_PERM + " " + ERR_NO_PERM + System.lineSeparator() +
                "paste: " + FILE_NO_PERM + " " + ERR_NO_PERM + System.lineSeparator() +
                "paste: " + FILE_NO_PERM + " " + ERR_NO_PERM + System.lineSeparator() +
                "paste: " + FILE_NO_PERM + " " + ERR_NO_PERM;
        assertEquals(expected, pasteApp.mergeFile(FILE_NO_PERM, FILE_NO_PERM,
                FILE_NO_PERM, FILE_NO_PERM));

        // Multiple no permission with other files
        assertEquals(expected, pasteApp.mergeFile(FILE_NO_PERM, FILE_NO_PERM, FILE_1,
                FILE_NO_PERM, FILE_3, FILE_NO_PERM, FILE_2));
    }

    @Test
    public void testMergeFile_fileErrorMsg_mixedErr() throws Exception {
        // Mixed error
        String expected = "paste: " + fileNoPermPath + " " + ERR_NO_PERM + System.lineSeparator() +
                "paste: " + absolutePath + " " + ERR_IS_DIR + System.lineSeparator() +
                "paste: fileNotFound.txt " + ERR_FILE_NOT_FOUND;
        assertEquals(expected, pasteApp.mergeFile(fileNoPermPath, absolutePath, "fileNotFound.txt"));

        // Multiple error with normal files
        assertEquals(expected, pasteApp.mergeFile(fileDoublePath, fileNoPermPath, fileTriplePath,
                absolutePath, "fileNotFound.txt", file3Path));
    }
     */

}

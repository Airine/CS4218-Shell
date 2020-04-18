package tdd.ef1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.app.GrepApplication;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.LongVariable"})
public class GrepApplicationTest {
    private GrepApplication app;

    private InputStream inputStream;
    private OutputStream outputStream;

    private String results;

    private static Path fileOnePath;
    private static final String FILE_ONE_CONTENT = "FirstLine"
            + STRING_NEWLINE
            + "SecondLine";

    private static final String MULTIPLE_LINES_MATCHES = "jin ying loves to eat"
            + STRING_NEWLINE +
            "jin ying loves to sleep" + STRING_NEWLINE +
            "jin ying loves to play" + STRING_NEWLINE +
            "jin ying loves to code" + STRING_NEWLINE +
            "jin ying loves to repeat" + STRING_NEWLINE;

    private static final String MULTIPLE_LINES_CASE_INSENSITIVE_MATCHES = "Jin Ying loves to Eat"
            + STRING_NEWLINE +
            "Jin ying loves to Sleep" + STRING_NEWLINE +
            "Jin ying loves to Play" + STRING_NEWLINE +
            "Jin ying loves to Code" + STRING_NEWLINE +
            "Jin ying loves to Repeat" + STRING_NEWLINE;

    private static final String MULTIPLE_FILES_CASE_SENSITIVE_MATCHES_COUNT =
            "src" + StringUtils.CHAR_FILE_SEP + "test" + StringUtils.CHAR_FILE_SEP + "tdd" + StringUtils.CHAR_FILE_SEP +
            "util" + StringUtils.CHAR_FILE_SEP + "dummyTestFolder"//NOPMD
                    + StringUtils.CHAR_FILE_SEP
                    + "GrepTestFolder"//NOPMD
                    + StringUtils.CHAR_FILE_SEP
                    + "file_uppercase_multiplelines.txt: 5"
                    + STRING_NEWLINE +
            "src" + StringUtils.CHAR_FILE_SEP + "test" + StringUtils.CHAR_FILE_SEP + "tdd" + StringUtils.CHAR_FILE_SEP
                    + "util" + StringUtils.CHAR_FILE_SEP + "dummyTestFolder"
                    + StringUtils.CHAR_FILE_SEP
                    + "GrepTestFolder"
                    + StringUtils.CHAR_FILE_SEP
                    + "file_jinying_multiplelines.txt: 5"
                    + STRING_NEWLINE;

    private static final String IS_A_DIR = ": This is a directory" + STRING_NEWLINE;
    private static final String NO_SUCH_FILE_OR_DIR = " : No such file or directory"  + STRING_NEWLINE;
    private static final String NULL_POINTER_EXCEPTION = "grep: Null Pointer Exception";
    private static final String INVALID_REGEX = "grep: Invalid regular expression supplied";
    private static final String GREP_NOTHING = "";
    private static final String NO_READ_PERMISSION = ": Permission denied" + STRING_NEWLINE;
    private static final String NO_INPUTSTREAM_NO_FILENAMES = "grep: No InputStream and no filenames";
    private static final String REGEX_CANNOT_BE_EMPTY = "grep: Regular expression cannot be empty";
    private Path fileTwoPath;

    private static final String FILE_ABCABC = Paths.get("src","test", "tdd", "util","dummyTestFolder","GrepTestFolder","file_abcabc.txt").toString();
    private static final String FILE_JINYING_MULTIPLELINES = Paths.get("src","test", "tdd", "util","dummyTestFolder","GrepTestFolder","file_jinying_multiplelines.txt").toString();
    private static final String FILE_UPPERCASE_MULTIPLELINES = Paths.get("src","test", "tdd", "util","dummyTestFolder","GrepTestFolder","file_uppercase_multiplelines.txt").toString();
    private static final String FILE_TWO_PATH_STRING = Paths.get("src","test", "tdd", "util","dummyTestFolder","GrepTestFolder","file_noread_permission.txt").toString();


    @BeforeEach
    public void setUp() throws IOException {
        app = new GrepApplication();
        fileOnePath = Files.createTempFile("file1", ".txt");
        OutputStream os1 = new FileOutputStream(fileOnePath.toFile());//NOPMD
        fileTwoPath = IOUtils.resolveFilePath(FILE_TWO_PATH_STRING);
        os1.write(FILE_ONE_CONTENT.getBytes());
        os1.close();
        outputStream = new ByteArrayOutputStream();
        fileTwoPath.toFile().setReadable(false);
    }

    @AfterEach
    public void tearDown() {
        fileOnePath.toFile().deleteOnExit();
        fileTwoPath.toFile().setReadable(true);
        results = "";
    }

    @Test
    public void testGrepFromFiles_nullPattern_shouldThrowException() {
        String[] fileName = {fileOnePath.toString()};
        String pattern = null;
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        Exception exception = assertThrows(Exception.class, () -> {
            app.grepFromFiles(pattern, isCaseInsensitive, isCountLines, fileName);
        });
        assertEquals(NULL_POINTER_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testGrepFromFiles_emptyStringPattern_shouldGrep() throws Exception {
        String[] fileName = {FILE_ABCABC};
        String pattern = "";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        results = app.grepFromFiles(pattern, isCaseInsensitive, isCountLines, fileName);
        assertEquals("abcabc" + STRING_NEWLINE, results);
    }

    @Test
    public void testGrepFromFiles_invalidPattern_shouldThrowException() {
        String[] fileName = {FILE_ABCABC};
        Exception exception = assertThrows(Exception.class, () -> {
            app.grepFromFiles("[", false, false,  fileName);
        });
        assertEquals(INVALID_REGEX, exception.getMessage());
    }

    @Test
    public void testGrepFromFiles_validPattern_noMatches_shouldGrepNothing() throws Exception {
        String[] fileName = {FILE_ABCABC};
        String pattern = "efg";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        results = app.grepFromFiles(pattern, isCaseInsensitive, isCountLines, fileName);
        assertEquals(GREP_NOTHING, results);
    }

    @Test
    public void testGrepFromFiles_validPattern_multipleMatches_shouldGrepMultiple() throws Exception {
        String[] fileName = {FILE_JINYING_MULTIPLELINES};
        String pattern = "j";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        results = app.grepFromFiles(pattern, isCaseInsensitive, isCountLines, fileName);
        assertEquals(MULTIPLE_LINES_MATCHES, results);
    }

    @Test
    public void testGrepFromFiles_isCaseInsensitive_true_shouldGrep() throws Exception {
        String[] fileName = {FILE_UPPERCASE_MULTIPLELINES};
        String pattern = "j";
        Boolean isCaseInsensitive = true;
        Boolean isCountLines = false;
        results = app.grepFromFiles(pattern, isCaseInsensitive, isCountLines, fileName);
        assertEquals(MULTIPLE_LINES_CASE_INSENSITIVE_MATCHES, results);
    }

    @Test
    public void testGrepFromFiles_isCaseInsensitive_false_shouldGrep() throws Exception {
        String[] fileName = {FILE_UPPERCASE_MULTIPLELINES};
        String pattern = "j";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        results = app.grepFromFiles(pattern, isCaseInsensitive, isCountLines, fileName);
        assertEquals(GREP_NOTHING, results);
    }

    @Test
    public void testGrepFromFiles_isCaseInsensitive_null_shouldThrowException() {
        String[] fileName = {FILE_UPPERCASE_MULTIPLELINES};
        String pattern = "j";
        Boolean isCaseInsensitive = null;
        Boolean isCountLines = false;
        Exception exception = assertThrows(Exception.class, () -> {
            app.grepFromFiles(pattern, isCaseInsensitive, isCountLines, fileName);
        });

        assertEquals(NULL_POINTER_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testGrepFromFiles_isCountLines_true_shouldShowMatchingLineCount() throws Exception {
        String[] fileName = {FILE_JINYING_MULTIPLELINES};
        String pattern = "j";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = true;
        results = app.grepFromFiles(pattern, isCaseInsensitive, isCountLines, fileName);
        assertEquals(5 + STRING_NEWLINE, results);
    }

    @Test
    public void testGrepFromFiles_isCountLines_null_shouldThrowException() {
        String[] fileName = {FILE_UPPERCASE_MULTIPLELINES};
        String pattern = "j";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = null;
        Exception exception = assertThrows(Exception.class, () -> {
            app.grepFromFiles(pattern, isCaseInsensitive, isCountLines, fileName);
        });

        assertEquals(NULL_POINTER_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testGrepFromFiles_zeroFileNames_shouldThrowException() {
        String[] fileName = {};
        String pattern = "j";
        Boolean isCaseInsensitive = null;
        Boolean isCountLines = false;
        Exception exception = assertThrows(Exception.class, () -> {
            app.grepFromFiles(pattern, isCaseInsensitive, isCountLines, fileName);
        });

        assertEquals(NULL_POINTER_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testGrepFromFiles_multipleFileNames_shouldGrep() throws Exception {
        String[] fileName = {
                FILE_UPPERCASE_MULTIPLELINES,
                FILE_JINYING_MULTIPLELINES};
        String pattern = "j";
        Boolean isCaseInsensitive = true;
        Boolean isCountLines = true;
        results = app.grepFromFiles(pattern, isCaseInsensitive, isCountLines, fileName);
        assertEquals(MULTIPLE_FILES_CASE_SENSITIVE_MATCHES_COUNT, results);
    }

    @Test
    public void testGrepFromFiles_emptyStringFileNames_shouldThrowExceptions() throws Exception {
        String[] fileName = {""};
        String pattern = "j";
        Boolean isCaseInsensitive = true;
        Boolean isCountLines = true;
        results = app.grepFromFiles(pattern, isCaseInsensitive, isCountLines, fileName);
        assertEquals(": " + ERR_FILE_NOT_FOUND + STRING_NEWLINE, results);
    }

    @Test
    public void testGrepFromFiles_invalidFileNames_shouldDisplayNoSuchFileOrDir() throws Exception {
        String[] fileName = {" "};
        String pattern = "j";
        Boolean isCaseInsensitive = true;
        Boolean isCountLines = true;
        results = app.grepFromFiles(pattern, isCaseInsensitive, isCountLines, fileName);
        assertEquals(NO_SUCH_FILE_OR_DIR, results);
    }

    @Disabled
    @Test
    public void testGrepFromFiles_noReadPermission_shouldDisplayNoPermission() throws Exception {
        String[] fileName = {fileTwoPath.toString()};
        String pattern = "j";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        results = app.grepFromFiles(pattern, isCaseInsensitive, isCountLines, fileName);
        assertEquals( fileTwoPath.toString() + NO_READ_PERMISSION, results);
    }

    @Test
    public void testGrepFromStdin_nullPattern_shouldThrowException() throws FileNotFoundException {
        inputStream = new FileInputStream(fileOnePath.toString());
        String pattern = null;
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        Exception exception = assertThrows(Exception.class, () -> {
            app.grepFromStdin(pattern, isCaseInsensitive, isCountLines, inputStream);
        });
        assertEquals(NULL_POINTER_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testGrepFromStdin_emptyStringPattern_shouldGrep() throws Exception {
        inputStream = new FileInputStream(fileOnePath.toString());
        String pattern = "";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        results = app.grepFromStdin(pattern, isCaseInsensitive, isCountLines, inputStream);
        assertEquals(FILE_ONE_CONTENT + STRING_NEWLINE, results);
    }

    @Test
    public void testGrepFromStdin_invalidPattern_shouldThrowException() throws FileNotFoundException {
        inputStream = new FileInputStream(fileOnePath.toString());
        String pattern = "[";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        Exception exception = assertThrows(Exception.class, () -> {
            app.grepFromStdin(pattern, isCaseInsensitive, isCountLines, inputStream);
        });
        assertEquals(INVALID_REGEX, exception.getMessage());
    }

    @Test
    public void testGrepFromStdin_validPattern_noMatches_shouldGrepNothing() throws Exception {
        inputStream = new FileInputStream(fileOnePath.toString());
        String pattern = "kk";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        results =  app.grepFromStdin(pattern, isCaseInsensitive, isCountLines, inputStream);
        assertEquals(GREP_NOTHING, results);
    }

    @Test
    public void testGrepFromStdin_validPattern_multipleMatches_shouldGrepMultiple() throws Exception {
        inputStream = new FileInputStream(fileOnePath.toString());
        String pattern = "L";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        results =  app.grepFromStdin(pattern, isCaseInsensitive, isCountLines, inputStream);
        assertEquals(FILE_ONE_CONTENT + STRING_NEWLINE, results);
    }

    @Test
    public void testGrepFromStdin_isCaseSensitive_true_shouldGrep() throws Exception {
        inputStream = new FileInputStream(fileOnePath.toString());
        String pattern = "l";
        Boolean isCaseInsensitive = true;
        Boolean isCountLines = false;
        results =  app.grepFromStdin(pattern, isCaseInsensitive, isCountLines, inputStream);
        assertEquals(FILE_ONE_CONTENT + STRING_NEWLINE, results);
    }

    @Test
    public void testGrepFromStdin_isCaseSensitive_null_shouldThrowException() throws FileNotFoundException {
        inputStream = new FileInputStream(fileOnePath.toString());
        String pattern = "L";
        Boolean isCaseInsensitive = null;
        Boolean isCountLines = false;
        Exception exception = assertThrows(Exception.class, () -> {
            results =  app.grepFromStdin(pattern, isCaseInsensitive, isCountLines, inputStream);
        });
        assertEquals(NULL_POINTER_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testGrepFromStdin_isCountLines_true_shouldShowMatchingLineCount() throws Exception {
        inputStream = new FileInputStream(fileOnePath.toString());
        String pattern = "L";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = true;
        results =  app.grepFromStdin(pattern, isCaseInsensitive, isCountLines, inputStream);
        assertEquals(2 + STRING_NEWLINE, results);
    }

    @Test
    public void testGrepFromStdin_isCountLines_null_shouldThrowException() throws Exception {
        inputStream = new FileInputStream(fileOnePath.toString());
        String pattern = "L";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = null;
        Exception exception = assertThrows(Exception.class, () -> {
            results =  app.grepFromStdin(pattern, isCaseInsensitive, isCountLines, inputStream);
        });
        assertEquals(NULL_POINTER_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testGrepFromStdin_nullInputStream_shouldThrowException() {
        inputStream = null;
        String pattern = "L";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        Exception exception = assertThrows(Exception.class, () -> {
            results =  app.grepFromStdin(pattern, isCaseInsensitive, isCountLines, inputStream);
        });
        assertEquals(NULL_POINTER_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testGrepFromStdin_byteArrayInputStream_shouldGrep() throws Exception {
        inputStream = new ByteArrayInputStream(FILE_ONE_CONTENT.getBytes());
        String pattern = "Lin";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        results =  app.grepFromStdin(pattern, isCaseInsensitive, isCountLines, inputStream);
        assertEquals(FILE_ONE_CONTENT + STRING_NEWLINE, results);
    }

    @Test
    public void testGrepFromStdin_fileInputStream_shouldGrep() throws Exception {
        inputStream = new FileInputStream(fileOnePath.toString());
        String pattern = "Lin";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        results =  app.grepFromStdin(pattern, isCaseInsensitive, isCountLines, inputStream);
        assertEquals(FILE_ONE_CONTENT + STRING_NEWLINE, results);
    }

    @Test
    public void testGrepFromStdin_bufferedInputStream_shouldGrep() throws Exception {
        inputStream = new BufferedInputStream(new FileInputStream(fileOnePath.toString()));
        String pattern = "Lin";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        results =  app.grepFromStdin(pattern, isCaseInsensitive, isCountLines, inputStream);
        assertEquals(FILE_ONE_CONTENT + STRING_NEWLINE, results);
    }

    @Test
    public void testRun_nullInputStreamAndNoInputFiles_shouldThrowException() {
        String[] args = {""};
        inputStream = null;
        Exception exception = assertThrows(Exception.class, () -> {
            app.run(args, inputStream, outputStream);
        });
        assertEquals(NO_INPUTSTREAM_NO_FILENAMES, exception.getMessage());
    }

    @Test
    public void testRun_nullPattern_shouldThrowException() throws FileNotFoundException {
        String[] args = {"["};
        inputStream = new FileInputStream(fileOnePath.toString());
        Exception exception = assertThrows(Exception.class, () -> {
            app.run(args, inputStream, outputStream);
        });
        assertEquals(INVALID_REGEX, exception.getMessage());
    }
}


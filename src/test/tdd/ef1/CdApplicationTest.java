package tdd.ef1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.impl.app.CdApplication;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.LongVariable"})
class CdApplicationTest {

    private CdApplication app;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String results;//NOPMD

    private static final String ABSOLUTE_PATH_PATH_EXISTS = System.getProperty("user.dir") +  StringUtils.CHAR_FILE_SEP + "src";//NOPMD
    private static final String ABSOLUTE_PATH_PATH_DONT_EXISTS = System.getProperty("user.dir") + StringUtils.CHAR_FILE_SEP + "abcdef";

    private static final String RELATIVE_PATH_PATH_EXISTS = "src";
    private static final String RELATIVE_PATH_PATH_DONT_EXISTS = "abcdef";
    private static final String RELATIVE_PATH_NOT_DIR = "README.md";

    private static final String FILE_NOT_FOUND = ": No such file or directory";
    private static final String IS_NOT_DIR = ": Not a directory";
    private static final String NO_READ_PERM = ": Permission denied";
    private static final String NO_ARGS = ": Insufficient arguments";
    private static final String NULL_POINTER_EXCEPTION = ": Null Pointer Exception";
    private static final String NULL_ARGS = ": Null arguments";
    private static final String TOO_MANY_ARGS = ": Too many arguments";

    private static final String CD_PATH = System.getProperty("user.dir") + StringUtils.CHAR_FILE_SEP + "cd_test" + StringUtils.CHAR_FILE_SEP;
    private File testDir;

    @BeforeEach
    public void setUp() throws IOException {
        app = new CdApplication();
        outputStream = new ByteArrayOutputStream();
        testDir = new File(CD_PATH);
        testDir.mkdir();
        testDir.setExecutable(false);
    }

    @AfterEach
    public void tearDown() throws CdException {
        testDir.delete();
        results = "";
        app.changeToDirectory(System.getProperty("user.dir"));
    }

    @Test
    public void testChangeToDirectory_absolutePath_pathExists_shouldCd() throws CdException {
        app.changeToDirectory(ABSOLUTE_PATH_PATH_EXISTS);
        assertEquals(ABSOLUTE_PATH_PATH_EXISTS, Environment.currentDirectory);
    }

    @Test
    public void testChangeToDirectory_absolutePath_pathDontExists_shouldThrowException() throws CdException {
        Exception exception = assertThrows(Exception.class, () -> {
            app.changeToDirectory(ABSOLUTE_PATH_PATH_DONT_EXISTS);
        });
        assertEquals("cd: " + ABSOLUTE_PATH_PATH_DONT_EXISTS + FILE_NOT_FOUND, exception.getMessage());//NOPMD
    }

    @Test
    public void testChangeToDirectory_relativePath_pathExists_shouldCd() throws CdException {
        app.changeToDirectory(RELATIVE_PATH_PATH_EXISTS);
        assertEquals(System.getProperty("user.dir") + StringUtils.CHAR_FILE_SEP + RELATIVE_PATH_PATH_EXISTS, Environment.currentDirectory);
    }

    @Test
    public void testChangeToDirectory_relativePath_pathDontExists_shouldThrowException() {
        Exception exception = assertThrows(Exception.class, () -> {
            app.changeToDirectory(RELATIVE_PATH_PATH_DONT_EXISTS);
        });
        assertEquals("cd: " + RELATIVE_PATH_PATH_DONT_EXISTS + FILE_NOT_FOUND, exception.getMessage());
    }

    @Test
    public void testChangeToDirectory_isNotADirectory() {
        Exception exception = assertThrows(Exception.class, () -> {
            app.changeToDirectory(RELATIVE_PATH_NOT_DIR);
        });
        assertEquals("cd: " + RELATIVE_PATH_NOT_DIR + IS_NOT_DIR, exception.getMessage());
    }

    @Test
    public void testChangeToDirectory_noReadPermission() {
        Exception exception = assertThrows(Exception.class, () -> {
            app.changeToDirectory(CD_PATH);
        });
        assertEquals("cd: " + CD_PATH + NO_READ_PERM, exception.getMessage());
    }

    @Test
    public void testChangeToDirectory_emptyPathString() {
        Exception exception = assertThrows(Exception.class, () -> {
            app.changeToDirectory("");
        });
        assertEquals("cd" + NO_ARGS, exception.getMessage());
    }

    @Test
    public void testRun_nullArgs_shouldThrowException() {
        String[] args = null;
        inputStream = new ByteArrayInputStream("abc".getBytes());
        Exception exception = assertThrows(Exception.class, () -> {
            app.run(args, inputStream, outputStream);
        });
        assertEquals("cd" + NULL_ARGS, exception.getMessage());
    }

    @Test
    public void testRun_nullInputStream() {
        String[] args = {RELATIVE_PATH_PATH_EXISTS};
        inputStream = null;
        Exception exception = assertThrows(Exception.class, () -> {
            app.run(args, inputStream, outputStream);
        });
        assertEquals("cd" + NULL_POINTER_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testRun_nullOutputStream() {
        String[] args = {RELATIVE_PATH_PATH_EXISTS};
        inputStream = new ByteArrayInputStream("abc".getBytes());
        Exception exception = assertThrows(Exception.class, () -> {
            app.run(args, inputStream, null);
        });
        assertEquals("cd" + NULL_POINTER_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testRun_tooManyArgs() {
        String[] args = {RELATIVE_PATH_PATH_EXISTS, ABSOLUTE_PATH_PATH_EXISTS};
        inputStream = new ByteArrayInputStream("abc".getBytes());
        Exception exception = assertThrows(Exception.class, () -> {
            app.run(args, inputStream, outputStream);
        });
        assertEquals("cd" + TOO_MANY_ARGS, exception.getMessage());
    }
}

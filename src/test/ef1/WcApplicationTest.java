package ef1;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.app.WcApplication;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class WcApplicationTest {
    private final static String WC_FOLDER = "asset" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "wc" + CHAR_FILE_SEP;
    private final static String TEST_FILE = WC_FOLDER + "test.txt";
    private final static String TEST_FILE_1 = WC_FOLDER + "test1.txt";
    private final static String TEST_FILE_2 = WC_FOLDER + "test2.txt";
    private final static String NONE_FILE = WC_FOLDER + "none.txt";
    private final static String SUB_DIR = WC_FOLDER + "subDir";
    private final static String NO_PERMISSION = SUB_DIR + CHAR_FILE_SEP + "NO_PERMISSION.txt";
    private final static String WC_ERR_HEADER = "wc: ";
    private final WcApplication wcApplication = new WcApplication();
    private InputStream inputStream = System.in;
    ;
    private OutputStream outputStream = new ByteArrayOutputStream();

    @Test
    void runWithNullOutputStream() {
        String[] args = {};
        Throwable throwable = assertThrows(WcException.class, () -> {
            wcApplication.run(args, System.in, null);
        });
        assertEquals(WC_ERR_HEADER + ERR_NULL_STREAMS, throwable.getMessage());
    }

    @Test
    void runWithOneFile() {
        String[] args = {TEST_FILE};
        assertDoesNotThrow(() -> {
            wcApplication.run(args, inputStream, outputStream);
            assertEquals("      21     306    2079 asset/app/wc/test.txt" + STRING_NEWLINE, outputStream.toString());
        });
    }

    @Test
    void runWithFiles() {
        String[] args = {TEST_FILE, TEST_FILE_1, TEST_FILE_2};
        assertDoesNotThrow(() -> {
            wcApplication.run(args, inputStream, outputStream);
            assertEquals("      21     306    2079 asset/app/wc/test.txt" + STRING_NEWLINE +
                    "       0       0       0 asset/app/wc/test1.txt" + STRING_NEWLINE +
                    "      20     642    4028 asset/app/wc/test2.txt" + STRING_NEWLINE +
                    "      41     948    6107 total" + STRING_NEWLINE, outputStream.toString());
        });
    }

    @Test
    void runWithStdIn() {
        String[] args = {"-c", "-l"};
        assertDoesNotThrow(() -> {
            inputStream = new FileInputStream(new File(TEST_FILE));
            wcApplication.run(args, inputStream, outputStream);
            assertEquals("      21    2079" + STRING_NEWLINE, outputStream.toString());
        });
    }

    @Test
    void runWithNUllInputStream() {
        String[] args = {};
        assertThrows(Exception.class, () -> {
            wcApplication.run(args, null, outputStream);
        });
    }

    @Test
    void runWithClosedOutputStream() {
        String[] args = {};
        assertThrows(WcException.class, () -> {
            inputStream = new FileInputStream(new File(TEST_FILE));
            outputStream = new FileOutputStream(new File(TEST_FILE_1));
            IOUtils.closeOutputStream(outputStream);
            wcApplication.run(args, inputStream, outputStream);
            IOUtils.closeInputStream(inputStream);
        });
    }

    @Test
    void countFromNullFile() {
        assertThrows(Exception.class, () -> {
            wcApplication.countFromFiles(true, true, true, null);
        });
    }

    @Test
    void countFromNonExistFile() {
        String result = WC_ERR_HEADER + ERR_FILE_NOT_FOUND;
        assertDoesNotThrow(() -> {
            assertEquals(result, wcApplication.countFromFiles(true, true, true, NONE_FILE));
        });
    }

    @Test
    void countFromDirectory() {
        String result = WC_ERR_HEADER + ERR_IS_DIR;
        assertDoesNotThrow(() -> {
            assertEquals(result, wcApplication.countFromFiles(true, true, true, SUB_DIR));
        });
    }

    @Test
    void countFromNoPermission() {
        String result = WC_ERR_HEADER + ERR_NO_PERM;
        File noPermFile = new File(NO_PERMISSION);
        if (noPermFile.setReadable(false)) {
            assertDoesNotThrow(() -> {
                assertEquals(result, wcApplication.countFromFiles(true, true, true, NO_PERMISSION));
            });
            assertTrue(noPermFile.setReadable(true));
        }
    }

    @Test
    void countFromNullStdIn() {
        assertThrows(WcException.class, () -> {
            wcApplication.countFromStdin(true, true, true, null);
        });
    }

    @Test
    void countFromClosedInputStream() {
        assertThrows(WcException.class, () -> {
            inputStream = new FileInputStream(new File(TEST_FILE));
            inputStream.close();
            wcApplication.countFromStdin(true, true, true, inputStream);
        });
    }

    @Test
    void getCountReportFromNullInputStream() {
        Throwable throwable = assertThrows(Exception.class, () -> {
            wcApplication.getCountReport(null);
        });
    }

}
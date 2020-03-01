package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.WcInterface;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

class WcApplicationTest {

    private final WcApplication wcApplication = new WcApplication();
    private final static String wcFolder = "asset" + CHAR_FILE_SEP+ "app" +CHAR_FILE_SEP + "wc" + CHAR_FILE_SEP;
    private final static String TEST_FILE = wcFolder + "test.txt";
    private final static String TEST_FILE_1 = wcFolder + "test1.txt";
    private final static String TEST_FILE_2 = wcFolder + "test2.txt";
    private final static String NONE_FILE = wcFolder + "none.txt";
    private final static String SUB_DIR = wcFolder + "subDir";
    private final static String NO_PERMISSION = SUB_DIR + CHAR_FILE_SEP + "NO_PERMISSION.txt";
    private InputStream inputStream = System.in;;
    private OutputStream outputStream = new ByteArrayOutputStream();

    @Test
    void runWithNullOutputStream() {
        String[] args = {};
        Throwable throwable = assertThrows(WcException.class, ()->{
            wcApplication.run(args, System.in, null);
        });
        assertEquals("wc: " + ERR_NULL_STREAMS, throwable.getMessage());
    }

    @Test
    void runWithOneFile() {
        String[] args = {TEST_FILE};
        assertDoesNotThrow(()->{
            wcApplication.run(args, inputStream, outputStream);
            System.out.println(outputStream.toString()); //TODO: change to assertEquals
        });
    }

    @Test
    void runWithFiles() {
        String[] args = {TEST_FILE, TEST_FILE_1, TEST_FILE_2};
        assertDoesNotThrow(()->{
            wcApplication.run(args, inputStream, outputStream);
            System.out.println(outputStream.toString()); //TODO: change to assertEquals
        });
    }

    @Test
    void runWithStdIn() {
        String[] args = {"-c", "-l"};
        assertDoesNotThrow(()->{
            inputStream = new FileInputStream(new File(TEST_FILE));
            wcApplication.run(args, inputStream, outputStream);
            System.out.println(outputStream.toString()); //TODO: change to assertEquals
        });
    }

    @Test
    void runWithNUllInputStream() {
        String[] args = {};
        assertThrows(Exception.class, ()->{
            wcApplication.run(args, null, outputStream);
        });
    }

    @Test
    void runWithClosedOutputStream() {
        String[] args = {};
        assertThrows(WcException.class, ()->{
            inputStream = new FileInputStream(new File(TEST_FILE));
            outputStream = new FileOutputStream(new File(TEST_FILE_1));
            IOUtils.closeOutputStream(outputStream);
            wcApplication.run(args, inputStream, outputStream);
            IOUtils.closeInputStream(inputStream);
        });
    }

    @Test
    void countFromNullFile() {
        assertThrows(Exception.class, ()->{
           wcApplication.countFromFiles(true, true, true, null);
        });
    }

    @Test
    void countFromNonExistFile() {
        String result = "wc: " + ERR_FILE_NOT_FOUND;
        assertDoesNotThrow(()->{
            assertEquals(result, wcApplication.countFromFiles(true, true, true, NONE_FILE));
        });
    }

    @Test
    void countFromDirectory() {
        String result = "wc: " + ERR_IS_DIR;
        assertDoesNotThrow(()->{
            assertEquals(result, wcApplication.countFromFiles(true, true, true, SUB_DIR));
        });
    }

    @Test
    void countFromNoPermission() {
        String result = "wc: " + ERR_NO_PERM;
        File no_perm_file = new File(NO_PERMISSION);
        if(no_perm_file.setReadable(false)){
            assertDoesNotThrow(()->{
                assertEquals(result, wcApplication.countFromFiles(true, true, true, NO_PERMISSION));
            });
            assertTrue(no_perm_file.setReadable(true));
        }
    }

    @Test
    void countFromNullStdIn() {
        assertThrows(WcException.class, ()->{
           wcApplication.countFromStdin(true, true, true, null);
        });
    }

    @Test
    void countFromClosedInputStream() {
        assertThrows(WcException.class, ()->{
            inputStream = new FileInputStream(new File(TEST_FILE));
            inputStream.close();
            wcApplication.countFromStdin(true, true, true, inputStream);
        });
    }

    @Test
    void getCountReportFromNullInputStream() {
        Throwable throwable = assertThrows(Exception.class, ()->{
            wcApplication.getCountReport(null);
        });
    }

}
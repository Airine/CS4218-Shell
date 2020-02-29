package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.WcInterface;
import sg.edu.nus.comp.cs4218.exception.WcException;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

class WcApplicationTest {

    private final WcApplication wcApplication = new WcApplication();
    private final static String wcFolder = "asset" + CHAR_FILE_SEP + "WcApplication" + CHAR_FILE_SEP;
    private final static String TEST_FILE = wcFolder + "test.txt";
    private final static String TEST_FILE_1 = wcFolder + "test1.txt";
    private final static String TEST_FILE_2 = wcFolder + "test2.txt";
    private InputStream inputStream = System.in;;
    private OutputStream outputStream = new ByteArrayOutputStream();

    @Test
    void runWithNullOutputStream() {
        String[] args = {};
        Throwable throwable = assertThrows(WcException.class, ()->{
            wcApplication.run(args, System.in, null);
        });
        assertEquals("wc: Null Pointer Exception", throwable.getMessage());
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
        assertThrows(IOException.class, ()->{
            inputStream = new FileInputStream(new File(TEST_FILE));
            outputStream.close();
            wcApplication.run(args, inputStream, outputStream);
        });
    }

    @Test
    void countFromFiles() {
    }

    @Test
    void countFromStdin() {
    }

    @Test
    void getCountReport() {
    }
}
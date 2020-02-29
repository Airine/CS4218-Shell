package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.WcInterface;
import sg.edu.nus.comp.cs4218.exception.WcException;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class WcApplicationTest {

    private final WcApplication wcApplication = new WcApplication();
    private InputStream inputStream;
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
        String[] args = {"asset/test.txt"};
        assertDoesNotThrow(()->{
            wcApplication.run(args, System.in, outputStream);
            System.out.println(outputStream.toString()); //TODO: change to assertEquals
        });
    }

    @Test
    void runWithFiles() {
        String[] args = {"asset/A.txt", "asset/B.txt", "asset/C.txt", "asset/test.txt"};
        assertDoesNotThrow(()->{
            wcApplication.run(args, System.in, outputStream);
            System.out.println(outputStream.toString()); //TODO: change to assertEquals
        });
    }

    @Test
    void runWithStdIn() {
        String[] args = {"-c", "-l"};
        assertDoesNotThrow(()->{
            inputStream = new FileInputStream(new File("asset/test.txt"));
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
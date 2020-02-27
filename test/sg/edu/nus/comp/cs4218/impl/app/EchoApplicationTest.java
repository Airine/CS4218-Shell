package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.EchoInterface;
import sg.edu.nus.comp.cs4218.exception.EchoException;

import java.io.*;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;



import static org.junit.jupiter.api.Assertions.*;

class EchoApplicationTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final EchoInterface echoApp = new EchoApplication();


    @Test
    void testConstructResultCharInput() {
        String[] args = {"abcdefghijklmnopqrstuvwxyz"};
        assertDoesNotThrow(()->{
            String result = echoApp.constructResult(args);
            assertEquals("abcdefghijklmnopqrstuvwxyz", result);
        });

    }

    @Test
    void testConstructResultNumInput() {
        String[] args = {"1234567890"};
        assertDoesNotThrow(()->{
            String result = echoApp.constructResult(args);
            assertEquals("1234567890", result);
        });
    }

    @Test
    void testConstructResultSpaceInput() {
        String[] args = {"   "};
        assertDoesNotThrow(()->{
            String result = echoApp.constructResult(args);
            assertEquals("   ", result);
        });
    }

    @Test
    void testConstructResultMultipleInput() {
        String[] args = {"abc", "123", "  "};
        assertDoesNotThrow(()->{
            String result = echoApp.constructResult(args);
            assertEquals("abc 123   ", result);
        });
    }

    @Test
    void testConstructResultEmptyInput() {
        String[] args = {};
        assertDoesNotThrow(()->{
            String result = echoApp.constructResult(args);
            assertEquals(STRING_NEWLINE, result);
        });
    }

    @Test
    void testConstructResultSymbolInput() {
        String[] args = {"!@#$%^&*()_+-={}[]:\";'|\\<>?,./~`"};
        assertDoesNotThrow(()->{
            String result = echoApp.constructResult(args);
            assertEquals("!@#$%^&*()_+-={}[]:\";'|\\<>?,./~`", result);
        });
    }

    @Test
    void testConstructResultNullInput() {
        String[] args = null;
        assertDoesNotThrow(()->{
            Throwable thrown = assertThrows(EchoException.class, () -> echoApp.constructResult(args));
            assertEquals("echo: " + ERR_NULL_ARGS, thrown.getMessage());
        });
    }

    @Test
    void testRunNoOutStream(){
        String[] args = {"aaa"};
        Throwable thrown = assertThrows(EchoException.class, () -> echoApp.run(args, System.in, null));
        assertEquals("echo: " + ERR_NO_OSTREAM, thrown.getMessage());
    }

    @Test
    void testRunIOException() throws IOException {
        String[] args = {"aaa"};
        OutputStream outputStream = new PipedOutputStream();//NOPMD
        outputStream.close();
        Throwable thrown = assertThrows(EchoException.class, () -> echoApp.run(args, System.in, outputStream));
        assertEquals("echo: " + ERR_IO_EXCEPTION, thrown.getMessage());
    }
}
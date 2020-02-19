package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testCharInput() throws Exception {
        String[] args = {"abcdefghijklmnopqrstuvwxyz"};
        echoApp.run(args, System.in, System.out);
        assertEquals("abcdefghijklmnopqrstuvwxyz", outContent.toString());
    }

    @Test
    void testNumInput() throws Exception {
        String[] args = {"1234567890"};
        echoApp.run(args, System.in, System.out);
        assertEquals("1234567890", outContent.toString());
    }

    @Test
    void testSpaceInput() throws Exception {
        String[] args = {"   "};
        echoApp.run(args, System.in, System.out);
        assertEquals("   ", outContent.toString());
    }

    @Test
    void testMultipleInput() throws Exception {
        String[] args = {"abc", "123", "  "};
        echoApp.run(args, System.in, System.out);
        assertEquals("abc 123   ", outContent.toString());
    }

    @Test
    void testEmptyInput() throws Exception {
        String[] args = {};
        echoApp.run(args, System.in, System.out);
        assertEquals(STRING_NEWLINE, outContent.toString());
    }

    @Test
    void testNullInput() throws Exception {
        String[] args = null;
        Throwable thrown = assertThrows(EchoException.class, () -> echoApp.run(args, System.in, System.out));
        assertEquals("echo: " + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    void testSymbolInput() throws Exception {
        String[] args = {"!@#$%^&*()_+-={}[]:\";'|\\<>?,./~`"};
        echoApp.run(args, System.in, System.out);
        assertEquals("!@#$%^&*()_+-={}[]:\";'|\\<>?,./~`", outContent.toString());
    }

    @Test
    void testNoOutStream(){
        String[] args = {"aaa"};
        Throwable thrown = assertThrows(EchoException.class, () -> echoApp.run(args, System.in, null));
        assertEquals("echo: " + ERR_NO_OSTREAM, thrown.getMessage());
    }

    @Test
    void testIOException() throws IOException {
        String[] args = {"aaa"};
        OutputStream outputStream = new PipedOutputStream();
        outputStream.close();
        Throwable thrown = assertThrows(EchoException.class, () -> echoApp.run(args, System.in, outputStream));
        assertEquals("echo: " + ERR_IO_EXCEPTION, thrown.getMessage());
    }
}
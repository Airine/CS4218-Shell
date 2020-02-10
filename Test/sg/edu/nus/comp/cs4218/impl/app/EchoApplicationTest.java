package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.EchoInterface;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
    void testNullInput() throws Exception {
        String[] args = {};
        echoApp.run(args, System.in, System.out);
        assertEquals(STRING_NEWLINE, outContent.toString());
    }

    @Test
    void testSymbolInput() throws Exception {
        String[] args = {"!@#$%^&*()_+-={}[]:\";'|\\<>?,./~`"};
        echoApp.run(args, System.in, System.out);
        assertEquals("!@#$%^&*()_+-={}[]:\";'|\\<>?,./~`", outContent.toString());
    }
}
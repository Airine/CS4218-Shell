package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.app.TestFileUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IORedirectionHandlerTest {

    private static final String TEST_STRING = "Hello World";
    private IORedirectionHandler handler;

    @BeforeEach
    void setUp() {
        try {
            TestFileUtils.rmCreatedFiles();
            TestFileUtils.createSomeFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() throws ShellException {
        TestFileUtils.rmCreatedFiles();
        handler.close();
    }

    @Test
    void testGetNoRedirArgsList() {
        List<String> args = Arrays.asList("a", "b");
        ArgumentResolver resolver = new ArgumentResolver();
        handler = new IORedirectionHandler(args, System.in, System.out, resolver);
        assertDoesNotThrow(() -> {
            handler.extractRedirOptions();
            assertEquals(Arrays.asList("a", "b"), handler.getNoRedirArgsList());
        });
    }

    @Test
    void testGetInputStream() {
        List<String> args = Arrays.asList("c", "d");
        ArgumentResolver resolver = new ArgumentResolver();
        handler = new IORedirectionHandler(args, System.in, System.out, resolver);
        assertEquals(System.in, handler.getInputStream());
    }

    @Test
    void testGetOutputStream() {
        List<String> args = Arrays.asList("e", "f");
        ArgumentResolver resolver = new ArgumentResolver();
        handler = new IORedirectionHandler(args, System.in, System.out, resolver);
        assertEquals(System.out, handler.getOutputStream());
    }

    @Test
    void testNullArgsList() {
        List<String> args = null;
        ArgumentResolver resolver = new ArgumentResolver();
        handler = new IORedirectionHandler(args, System.in, System.out, resolver);
        Throwable thrown = assertThrows(ShellException.class, () -> {
            handler.extractRedirOptions();
        });
        assertEquals("shell: Invalid syntax", thrown.getMessage());
    }

    @Test
    void testEmptyArgsList() {
        List<String> args = Arrays.asList();
        ArgumentResolver resolver = new ArgumentResolver();
        handler = new IORedirectionHandler(args, System.in, System.out, resolver);
        Throwable thrown = assertThrows(ShellException.class, () -> {
            handler.extractRedirOptions();
        });
        assertEquals("shell: Invalid syntax", thrown.getMessage());
    }


    @Test
    void testFirstNotRedir() {
        List<String> args = Arrays.asList("g", "<", TestFileUtils.tempFileName1);
        ArgumentResolver resolver = new ArgumentResolver();
        handler = new IORedirectionHandler(args, System.in, System.out, resolver);
        assertDoesNotThrow(() -> {
            handler.extractRedirOptions();
            assertEquals(Arrays.asList("g"), handler.getNoRedirArgsList());

            OutputStream outstream = new FileOutputStream(TestFileUtils.tempFileName1);
            outstream.write(TEST_STRING.getBytes());
            byte[] returnString = new byte[TEST_STRING.getBytes().length];
            handler.getInputStream().read(returnString);
            outstream.close();
            assertTrue(Arrays.equals(TEST_STRING.getBytes(), returnString));
            assertEquals(System.out, handler.getOutputStream());
        });
    }

    @Test
    void testRedirInput() {
        List<String> args = Arrays.asList("<", TestFileUtils.tempFileName1);
        ArgumentResolver resolver = new ArgumentResolver();
        handler = new IORedirectionHandler(args, System.in, System.out, resolver);
        assertDoesNotThrow(() -> {
            handler.extractRedirOptions();
            OutputStream outstream = new FileOutputStream(TestFileUtils.tempFileName1);
            outstream.write(TEST_STRING.getBytes());
            byte[] returnString = new byte[TEST_STRING.getBytes().length];
            handler.getInputStream().read(returnString);
            outstream.close();
            assertTrue(Arrays.equals(TEST_STRING.getBytes(), returnString));
            assertEquals(System.out, handler.getOutputStream());
        });
    }

    @Test
    void testRedirOutput() {
        List<String> args = Arrays.asList(">", TestFileUtils.tempFileName1);
        ArgumentResolver resolver = new ArgumentResolver();
        handler = new IORedirectionHandler(args, System.in, System.out, resolver);
        assertDoesNotThrow(() -> {
            handler.extractRedirOptions();
            handler.getOutputStream().write(TEST_STRING.getBytes());
            InputStream instream = new FileInputStream(TestFileUtils.tempFileName1);
            byte[] returnString = new byte[TEST_STRING.getBytes().length];
            instream.read(returnString);
            instream.close();
            assertTrue(Arrays.equals(TEST_STRING.getBytes(), returnString));
            assertEquals(System.in, handler.getInputStream());
        });
    }

    @Test
    void testMultipleInputStream() {
        List<String> args = Arrays.asList("<", TestFileUtils.tempFileName1, "<", TestFileUtils.tempFileName2);
        ArgumentResolver resolver = new ArgumentResolver();
        handler = new IORedirectionHandler(args, System.in, System.out, resolver);
        Throwable thrown = assertThrows(ShellException.class, () -> {
            handler.extractRedirOptions();
        });
        assertEquals("shell: Multiple streams provided", thrown.getMessage());
    }

    @Test
    void testMultipleOutputStream() {
        List<String> args = Arrays.asList(">", TestFileUtils.tempFileName1, ">", TestFileUtils.tempFileName2);
        ArgumentResolver resolver = new ArgumentResolver();
        handler = new IORedirectionHandler(args, System.in, System.out, resolver);
        Throwable thrown = assertThrows(ShellException.class, () -> {
            handler.extractRedirOptions();
        });
        assertEquals("shell: Multiple streams provided", thrown.getMessage());
    }
}
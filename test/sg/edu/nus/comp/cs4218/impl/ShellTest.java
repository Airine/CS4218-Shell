package sg.edu.nus.comp.cs4218.impl;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.impl.app.NewIOStream;
import sg.edu.nus.comp.cs4218.impl.app.TestFileUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class ShellTest {

    private final Shell shell = new ShellImpl();

    private InputStream inputStream;
    private PrintStream outputStream;
    private NewIOStream ioStream;

    @BeforeAll
    static void setUp() {
        try {
            TestFileUtils.createSomeFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void tearDown() {
        TestFileUtils.rmCreatedFiles();
    }

    @BeforeEach
    void setStdInAndOut() {
        inputStream = System.in;
        outputStream = System.out;
        try {
            ioStream = new NewIOStream(TestFileUtils.tempFileName1);
            System.setIn(ioStream.inputStream);
            System.setOut(new PrintStream(ioStream.outputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @AfterEach
    void resumeStdInAndOut() {
        System.setIn(inputStream);
        System.setOut(outputStream);
        try {
            ioStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testPipeOperation() {
        String command = "echo \"hello world\" | wc";
        assertDoesNotThrow(() -> shell.parseAndEvaluate(command, ioStream.outputStream));
        assertTrue(ioStream.outputStream.toString().contains(String.valueOf(
                ("hello world" + StringUtils.STRING_NEWLINE).length()
                )
        ));
    }
}
package tdd.ef2.cmd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class CommandSubstitutionTest {

    private static ApplicationRunner appRunner;
    private static ArgumentResolver argsResolver;
    private static OutputStream outputStream;
    private static InputStream inputStream;

    @BeforeEach
    void setUp() {
        appRunner = mock(ApplicationRunner.class);
        argsResolver = new ArgumentResolver();
        outputStream = mock(OutputStream.class);
        inputStream = mock(InputStream.class);
    }

    // Command Substitution is done in ArgsResolver and is evaluated in it's private method called evaluateSubCommand
    // The private method can't be mock using mockito. Hence, the tests here will use echo command for all command
    // substitution assuming echo application is bug free.
    // The tests will check if all the sub commands after substituted correctly

    @Test
    void testCmdSubstitutionWithOneCmdBackQuotedOnly() {
        // command : `echo hi`
        // Expected : ""
        try {
            List<String> argsList = Arrays.asList("`echo hi`");
            CallCommand cmd = new CallCommand(argsList, appRunner, argsResolver);

            doAnswer(invocation -> {
                String[] parsedArgsList = invocation.getArgument(1);
                String actual = String.join(" ", parsedArgsList);

                String expected = "";
                assertEquals(expected, actual);
                return null;
            }).when(appRunner).runApp(any(), any(), any(), any());

            cmd.evaluate(inputStream, outputStream);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testCmdSubstitutionWithOneCmdAppendedOnAnotherCmd() {
        // command : echo `echo hi`
        // Expected : echo hi
        try {
            List<String> argsList = Arrays.asList("echo", "`echo hi`");
            CallCommand cmd = new CallCommand(argsList, appRunner, argsResolver);

            doAnswer(invocation -> {
                String app = invocation.getArgument(0);
                String[] parsedArgsList = invocation.getArgument(1);
                String actual = app + " " + String.join(" ", parsedArgsList);

                String expected = "echo hi";
                assertEquals(expected, actual);
                return null;
            }).when(appRunner).runApp(any(), any(), any(), any());

            cmd.evaluate(inputStream, outputStream);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testCmdSubstitutionWithOneCmdAppendedOnAnotherCmdNotBackQuoted() {
        // command : echo echo hi
        // Expected : echo echo hi
        try {
            List<String> argsList = Arrays.asList("echo", "echo", "hi");
            CallCommand cmd = new CallCommand(argsList, appRunner, argsResolver);

            doAnswer(invocation -> {
                String app = invocation.getArgument(0);
                String[] parsedArgsList = invocation.getArgument(1);
                String actual = app + " " + String.join(" ", parsedArgsList);

                String expected = "echo echo hi";
                assertEquals(expected, actual);
                return null;
            }).when(appRunner).runApp(any(), any(), any(), any());

            cmd.evaluate(inputStream, outputStream);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testCmdSubstitutionWithMultipleCmdAppendedOnAnotherCmd() {
        // command : cut `echo -c` `echo 1-2` `echo <` `echo hello.txt`
        // Expected : cut -c 1-2 < hello.txt
        try {
            List<String> argsList = Arrays.asList(
                "cut",
                "`echo -c`",
                "`echo 1-2`",
                "`echo '<'`",
                "`echo hello.txt`"
            );
            CallCommand cmd = new CallCommand(argsList, appRunner, argsResolver);

            doAnswer(invocation -> {
                String app = invocation.getArgument(0);
                String[] parsedArgsList = invocation.getArgument(1);
                String actual = app + " " + String.join(" ", parsedArgsList);

                String expected = "cut -c 1-2 < hello.txt";
                assertEquals(expected, actual);
                return null;
            }).when(appRunner).runApp(any(), any(), any(), any());

            cmd.evaluate(inputStream, outputStream);
        } catch (Exception e) {
            fail();
        }
    }
}

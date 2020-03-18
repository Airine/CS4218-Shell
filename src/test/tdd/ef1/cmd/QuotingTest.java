package tdd.ef1.cmd;

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

@SuppressWarnings({"PMD.LongVariable", "PMD.AvoidDuplicateLiterals"})
public class QuotingTest {

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

    @Test
    void testQuotingSingleQuoteWithNoSpecialChars() {
        // Cmd: echo 'hello'
        // Expected: echo hello
        try {
            List<String> argsList = Arrays.asList("echo", "\'hello\'");
            CallCommand cmd = new CallCommand(argsList, appRunner, argsResolver);

            doAnswer(invocation -> {
                String app = invocation.getArgument(0);
                String[] parsedArgsList = invocation.getArgument(1); // parsedArgsList (Globbing is done here)
                String actual = app + " " + String.join(" ", parsedArgsList);

                String expected = "echo hello";
                assertEquals(expected, actual);

                return null;
            }).when(appRunner).runApp(any(), any(), any(), any());

            cmd.evaluate(inputStream, outputStream);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testQuotingDoubleQuoteWithNoSpecialChars() {
        // Cmd: echo "hello"
        // Expected: echo hello
        try {
            List<String> argsList = Arrays.asList("echo", "\"hello\"");
            CallCommand cmd = new CallCommand(argsList, appRunner, argsResolver);

            doAnswer(invocation -> {
                String app = invocation.getArgument(0);
                String[] parsedArgsList = invocation.getArgument(1); // parsedArgsList (Globbing is done here)
                String actual = app + " " + String.join(" ", parsedArgsList);

                String expected = "echo hello";
                assertEquals(expected, actual);

                return null;
            }).when(appRunner).runApp(any(), any(), any(), any());

            cmd.evaluate(inputStream, outputStream);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testQuotingBackQuoteWithNoSpecialChars() {
        // Assumes Command Substitution and Echo Application are bug free
        //
        // Cmd: echo `echo hello`
        // Expected: echo hello
        try {
            List<String> argsList = Arrays.asList("echo", "`echo hello`");
            CallCommand cmd = new CallCommand(argsList, appRunner, argsResolver);

            doAnswer(invocation -> {
                String app = invocation.getArgument(0);
                String[] parsedArgsList = invocation.getArgument(1); // parsedArgsList (Globbing is done here)
                String actual = app + " " + String.join(" ", parsedArgsList);

                String expected = "echo hello";
                assertEquals(expected, actual);

                return null;
            }).when(appRunner).runApp(any(), any(), any(), any());

            cmd.evaluate(inputStream, outputStream);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testQuotingSingleQuoteDisablesAllSpecialChars() {
        // Cmd: echo '"\t*| <>; `echo hello`'
        // Expected: echo "\t*| <>; `echo hello`
        try {
            List<String> argsList = Arrays.asList("echo", "\'\"\\t*| <>; `echo hello`\'");
            CallCommand cmd = new CallCommand(argsList, appRunner, argsResolver);

            doAnswer(invocation -> {
                String app = invocation.getArgument(0);
                String[] parsedArgsList = invocation.getArgument(1); // parsedArgsList (Globbing is done here)
                String actual = app + " " + String.join(" ", parsedArgsList);

                String expected = "echo \"\\t*| <>; `echo hello`";
                assertEquals(expected, actual);

                return null;
            }).when(appRunner).runApp(any(), any(), any(), any());

            cmd.evaluate(inputStream, outputStream);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testQuotingDoubleQuoteDisablesAllSpecialCharsExceptBackQuote() {
        // Assumes Command Substitution and Echo Application are bug free
        //
        // Cmd: echo "'\t*| <>; `echo hello`"
        // Expected: echo '\t*| <>; hello
        try {
            List<String> argsList = Arrays.asList("echo", "\"\'\\t*| <>; `echo hello`\"");
            CallCommand cmd = new CallCommand(argsList, appRunner, argsResolver);

            doAnswer(invocation -> {
                String app = invocation.getArgument(0);
                String[] parsedArgsList = invocation.getArgument(1); // parsedArgsList (Globbing is done here)
                String actual = app + " " + String.join(" ", parsedArgsList);

                String expected = "echo \'\\t*| <>; hello";
                assertEquals(expected, actual);

                return null;
            }).when(appRunner).runApp(any(), any(), any(), any());

            cmd.evaluate(inputStream, outputStream);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testQuotingDisableSingleQuotesByDoubleQuotesAndEnablesBackQuotes() {
        // Assumes Command Substitution and Echo Application are bug free
        //
        // Cmd: echo "'This is izone `echo fiesta`'"
        // Expected: echo 'This is izone fiesta'
        try {
            List<String> argsList = Arrays.asList("echo", "\"'This is izone `echo fiesta`'\"");
            CallCommand cmd = new CallCommand(argsList, appRunner, argsResolver);

            doAnswer(invocation -> {
                String app = invocation.getArgument(0);
                String[] parsedArgsList = invocation.getArgument(1); // parsedArgsList (Globbing is done here)
                String actual = app + " " + String.join(" ", parsedArgsList);

                String expected = "echo 'This is izone fiesta'";
                assertEquals(expected, actual);

                return null;
            }).when(appRunner).runApp(any(), any(), any(), any());

            cmd.evaluate(inputStream, outputStream);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testQuotingDisableDoubleAndBackQuotesBySingleQuotes() {
        // Assumes Command Substitution and Echo Application are bug free
        //
        // Cmd: echo '"This is izone `echo fiesta`"'
        // Expected: echo "This is izone `echo fiesta`"
        try {
            List<String> argsList = Arrays.asList("echo", "'\"This is izone `echo fiesta`\"'");
            CallCommand cmd = new CallCommand(argsList, appRunner, argsResolver);

            doAnswer(invocation -> {
                String app = invocation.getArgument(0);
                String[] parsedArgsList = invocation.getArgument(1); // parsedArgsList (Globbing is done here)
                String actual = app + " " + String.join(" ", parsedArgsList);

                String expected = "echo \"This is izone `echo fiesta`\"";
                assertEquals(expected, actual);

                return null;
            }).when(appRunner).runApp(any(), any(), any(), any());

            cmd.evaluate(inputStream, outputStream);
        } catch (Exception e) {
            fail();
        }
    }

}

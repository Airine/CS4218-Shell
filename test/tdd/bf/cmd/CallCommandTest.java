package tdd.bf.cmd;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

/**
 * SUT: {@link CallCommand}
 */
@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.AvoidDuplicateLiterals"})
public class CallCommandTest extends BaseCommandTest {
    private static final String EXCEPTION_PREFIX = "shell: ";

    @InjectMocks
    CallCommand callCommand;

    @Mock
    ApplicationRunner appRunner;

    @Mock
    ArgumentResolver argumentResolver;

    List<String> argsList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // NULL tests

    @Test
    void testRun_WithNullArgs_shouldThrowShellException() throws Exception {
        argsList = null;
        callCommand = new CallCommand(argsList, appRunner, argumentResolver);
        ShellException exception = assertThrows(ShellException.class,() -> callCommand.evaluate(System.in, System.out));
        String expected = EXCEPTION_PREFIX + ERR_SYNTAX;
        assertEquals(expected, exception.getMessage());
    }

    @Test
    void testConstructor_WithNullAppRunner_shouldThrowShellException() throws Exception {
        appRunner = null;
        Exception exception = assertThrows(ShellException.class, () -> new CallCommand(argsList, appRunner, argumentResolver));
        assertEquals("shell: " + "Null App Runner", exception.getMessage());
    }

    @Test
    void testConstructor_WithNullArgsResolver_shouldThrowShellException() throws Exception {
        argumentResolver = null;
        Exception exception = assertThrows(ShellException.class, () -> new CallCommand(argsList, appRunner, argumentResolver));
        assertEquals("shell: " + "Null Argument Resolver", exception.getMessage());
    }

    @Test
    void testRun_WithEmptyArgs_shouldThrowShellException() throws Exception {
        argsList = new ArrayList<>();
        callCommand = new CallCommand(argsList, appRunner, argumentResolver);
        ShellException exception = assertThrows(ShellException.class,() -> callCommand.evaluate(System.in, System.out));
        String expected = EXCEPTION_PREFIX + ERR_SYNTAX;
        assertEquals(expected, exception.getMessage());
    }

    // Unit tests

    @Test
    void testEvaluate_WithTypicalEchoApp_shouldRunApp() throws Exception {
        argsList = Arrays.asList("echo", "hello", "world");
        callCommand = new CallCommand(argsList, appRunner, argumentResolver);

        when(argumentResolver.parseArguments(argsList)).thenReturn(new ArrayList<>(argsList));

        callCommand.evaluate(System.in, System.out);

        verify(appRunner, times(1))
                .runApp("echo", new String[]{"hello", "world"}, System.in, System.out);
    }

    @Test
    void testEvaluate_WithParseArgsReturnsEmpty_shouldNotRunApp() throws Exception {
        argsList = Arrays.asList("echo", "hello", "world");
        callCommand = new CallCommand(argsList, appRunner, argumentResolver);

        when(argumentResolver.parseArguments(argsList)).thenReturn(new ArrayList<>());

        callCommand.evaluate(System.in, System.out);

        verify(appRunner, never())
                .runApp("echo", new String[]{"hello", "world"}, System.in, System.out);
    }

}

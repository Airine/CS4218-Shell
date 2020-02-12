package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArgumentResolverTest {
    private final ArgumentResolver argumentResolver = new ArgumentResolver();

    @Test
    void testSingleQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("'aaa'");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("aaa"), parsedArgsList);
    }

    @Test
    void testDoubleQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("\"aaa\"");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("aaa"), parsedArgsList);
    }

    @Test
    void testBackQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("`echo 'aaa'`");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("aaa"), parsedArgsList);
    }

    @Test
    void testBackQuoteWithDoubleQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("\"`echo 'aaa'`\"");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("aaa"), parsedArgsList);
    }

    @Test
    void testBackQuoteWithSingleQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("'`echo 'aaa'`'");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("`echo 'aaa'`"), parsedArgsList);
    }

    // not sure about this case
    @Test
    void testSpecialSymbolWithDoubleQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("\"`|>_<|;\"");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("|>_<|;"), parsedArgsList);
    }

    @Test
    void testSpecialSymbolWithSingleQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("'`|>_<|;'");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("`|>_<|;"), parsedArgsList);
    }

    @Test
    void testMixThreeQuote1() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("\"'aaa `echo \"bbb\"`'\"");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("'aaa bbb'"), parsedArgsList);
    }

    @Test
    void testMixThreeQuote2() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("'\"aaa `echo \"bbb\"`\"'");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("\"aaa `echo \"bbb\"`\""), parsedArgsList);
    }

    @Test
    void testIncorrectQuote1() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("\"aaa'bbb\"ccc'");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("aaa'bbbccc"), parsedArgsList);
    }

    // not sure about the output
    @Test
    void testIncorrectQuote2() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("\"aaa `echo '\"bbb'`");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("aaa \"bbb"), parsedArgsList);
    }
}
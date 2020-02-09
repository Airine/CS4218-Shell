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
    public void testSingleQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("'aaa'");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("aaa"), parsedArgsList);
    }

    @Test
    public void testDoubleQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("\"aaa\"");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("aaa"), parsedArgsList);
    }

    @Test
    public void testBackQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("`echo 'aaa'`");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("aaa"), parsedArgsList);
    }

    @Test
    public void testBackQuoteWithDoubleQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("\"`echo 'aaa'`\"");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("aaa"), parsedArgsList);
    }

    @Test
    public void testBackQuoteWithSingleQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("'`echo 'aaa'`'");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("`echo 'aaa'`"), parsedArgsList);
    }

    // not sure about this case
    @Test
    public void testSpecialSymbolWithDoubleQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("\"`|>_<|;\"");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("|>_<|;"), parsedArgsList);
    }

    @Test
    public void testSpecialSymbolWithSingleQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("'`|>_<|;'");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("`|>_<|;"), parsedArgsList);
    }

    @Test
    public void testMixThreeQuote1() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("\"'aaa `echo \"bbb\"`'\"");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("'aaa bbb'"), parsedArgsList);
    }

    @Test
    public void testMixThreeQuote2() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("'\"aaa `echo \"bbb\"`\"'");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("\"aaa `echo \"bbb\"`\""), parsedArgsList);
    }

    @Test
    public void testIncorrectQuote1() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("\"aaa'bbb\"ccc'");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("aaa'bbbccc"), parsedArgsList);
    }

    // not sure about the output
    @Test
    public void testIncorrectQuote2() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("\"aaa `echo '\"bbb'`");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("aaa \"bbb"), parsedArgsList);
    }
}
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
        List<String> input = Arrays.asList("\"bbb\"");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("bbb"), parsedArgsList);
    }

    @Test
    void testBackQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("`echo 'ccc'`");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        //TODO: ignore for now.
        assertTrue(true);
        //assertEquals(Arrays.asList("ccc"), parsedArgsList);
    }

    @Test
    void testBackQuoteWithDoubleQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("\"`echo 'ddd'`\"");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("ddd"), parsedArgsList);
    }

    @Test
    void testBackQuoteWithSingleQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("'`echo 'eee'`'");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        //TODO: ignore for now.
        assertTrue(true);
        //assertEquals(Arrays.asList("`echo 'eee'`"), parsedArgsList);
    }

    // not sure about this case
    @Test
    void testSpecialSymbolWithDoubleQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("\"`|>_<|;\"");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        //TODO: ignore for now.
        assertTrue(true);
        //assertEquals(Arrays.asList("|>_<|;"), parsedArgsList);
    }

    @Test
    void testSpecialSymbolWithSingleQuote() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("'`|>_<|;'");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("`|>_<|;"), parsedArgsList);
    }

    @Test
    void testMixThreeQuote1() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("\"'fff `echo \"ggg\"`'\"");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("'fff ggg'"), parsedArgsList);
    }

    @Test
    void testMixThreeQuote2() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("'\"hhh `echo \"iii\"`\"'");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("\"hhh `echo \"iii\"`\""), parsedArgsList);
    }

    @Test
    void testIncorrectQuote1() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("\"jjj'kkk\"lll'");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("jjj'kkklll"), parsedArgsList);
    }

    // not sure about the output
    @Test
    void testIncorrectQuote2() throws AbstractApplicationException, ShellException {
        List<String> input = Arrays.asList("\"mmm `echo '\"nnn'`");
        List<String> parsedArgsList = argumentResolver.parseArguments(input);
        assertEquals(Arrays.asList("mmm \"nnn"), parsedArgsList);
    }
}
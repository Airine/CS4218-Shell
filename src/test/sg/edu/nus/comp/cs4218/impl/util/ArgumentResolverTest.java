package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class ArgumentResolverTest {
    private final ArgumentResolver argumentResolver = new ArgumentResolver();

    @Test
    void testSingleQuote(){
        List<String> input = Arrays.asList("'aaa'");
        assertDoesNotThrow(()->{
            List<String> parsedArgsList = argumentResolver.parseArguments(input);
            assertEquals(Arrays.asList("aaa"), parsedArgsList);
        });
    }

    @Test
    void testDoubleQuote(){
        List<String> input = Arrays.asList("\"bbb\"");
        assertDoesNotThrow(()->{
            List<String> parsedArgsList = argumentResolver.parseArguments(input);
            assertEquals(Arrays.asList("bbb"), parsedArgsList);
        });
    }

    @Test
    void testBackQuote(){
        List<String> input = Arrays.asList("`echo 'ccc'`");
        assertDoesNotThrow(()->{
            List<String> parsedArgsList = argumentResolver.parseArguments(input);
            assertEquals(Arrays.asList("ccc"), parsedArgsList);
        });
    }

    @Test
    void testBackQuoteWithDoubleQuote(){
        List<String> input = Arrays.asList("\"`echo 'ddd'`\"");
        assertDoesNotThrow(()->{
            List<String> parsedArgsList = argumentResolver.parseArguments(input);
            assertEquals(Arrays.asList("ddd"), parsedArgsList);
        });
    }

    @Test
    void testBackQuoteWithSingleQuote(){
        List<String> input = Arrays.asList("'`echo 'eee'`'");
        assertDoesNotThrow(()->{
            List<String> parsedArgsList = argumentResolver.parseArguments(input);
            assertEquals(Arrays.asList("`echo eee`"), parsedArgsList);
        });
    }

    // not sure about this case
    @Test
    void testSpecialSymbolWithDoubleQuote(){
        List<String> input = Arrays.asList("\"`|>_<|;\"");
        assertDoesNotThrow(()->{
            List<String> parsedArgsList = argumentResolver.parseArguments(input);
            assertEquals(Arrays.asList(), parsedArgsList);
        });
    }

    @Test
    void testSpecialSymbolWithSingleQuote(){
        List<String> input = Arrays.asList("'`|>_<|;'");
        assertDoesNotThrow(()->{
            List<String> parsedArgsList = argumentResolver.parseArguments(input);
            assertEquals(Arrays.asList("`|>_<|;"), parsedArgsList);
        });
    }

    @Test
    void testMixThreeQuote1(){
        List<String> input = Arrays.asList("\"'fff `echo \"ggg\"`'\"");
        assertDoesNotThrow(()->{
            List<String> parsedArgsList = argumentResolver.parseArguments(input);
            assertEquals(Arrays.asList("'fff ggg'"), parsedArgsList);
        });
    }

    @Test
    void testMixThreeQuote2(){
        List<String> input = Arrays.asList("'\"hhh `echo \"iii\"`\"'");
        assertDoesNotThrow(()->{
            List<String> parsedArgsList = argumentResolver.parseArguments(input);
            assertEquals(Arrays.asList("\"hhh `echo \"iii\"`\""), parsedArgsList);
        });
    }

    @Test
    void testIncorrectQuote1(){
        List<String> input = Arrays.asList("\"jjj'kkk\"lll'");
        assertDoesNotThrow(()->{
            List<String> parsedArgsList = argumentResolver.parseArguments(input);
            assertEquals(Arrays.asList("jjj'kkklll"), parsedArgsList);
        });
    }

    // not sure about the output
    @Test
    void testIncorrectQuote2(){
        List<String> input = Arrays.asList("\"mmm `echo '\"nnn'`");
        assertDoesNotThrow(()->{
            List<String> parsedArgsList = argumentResolver.parseArguments(input);
            assertEquals(Arrays.asList("mmm \"nnn"), parsedArgsList);
        });
    }
}
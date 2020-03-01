package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.SedInterface;
import sg.edu.nus.comp.cs4218.exception.SedException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;


class SedApplicationTest {

    private static String folderName = "asset" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "common";
    private static String fileNameA = "A.txt";
    private static String subDirName = "subDir";
    private static String fileNameNotExist = "notExist.txt";
    private static String sedPrefix = "sed: ";
    private final SedInterface app = new SedApplication();
    private OutputStream outputStream = null;

    @Test
    void testReplaceEmptyCharater() throws Exception {
        String original = " abcab";
        String pattern = "\\b";
        String replacement = "";
        String expected = "abcab";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertDoesNotThrow(() -> {
            assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
        });

    }

    @Test
    void testReplaceWithEmptyCharater() {
        String original = "abcabc";
        String pattern = "a";
        String replacement = "";
        String expected = "bcabc";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertDoesNotThrow(() -> {
            assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
        });
    }

    @Test
    void testReplaceSingleCharater() {
        String original = "abcab";
        String pattern = "a";
        String replacement = "d";
        String expected = "dbcab";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertDoesNotThrow(() -> {
            assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
        });
    }

    @Test
    void testReplaceMultipleCharater() {
        String original = "abcabd";
        String pattern = "abc";
        String replacement = "d";
        String expected = "dabd";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertDoesNotThrow(() -> {
            assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
        });
    }

    @Test
    void testReplaceSecondMatchCharater() {
        String original = "abcab";
        String pattern = "a";
        String replacement = "d";
        String expected = "abcdb";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertDoesNotThrow(() -> {
            assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 2, stdin).trim());
        });
    }

    @Test
    void testReplaceDotCharater() {
        String original = "aabca";
        String pattern = "a.c";
        String replacement = "d";
        String expected = "ada";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertDoesNotThrow(() -> {
            assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
        });
    }

    @Test
    void testReplaceCaretCharater() {
        String original = "abcdabc";
        String pattern = "^abc";
        String replacement = "d";
        String expected = "ddabc";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertDoesNotThrow(() -> {
            assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
        });
    }

    @Test
    void testReplaceDollarCharater() {
        String original = "abcdabc";
        String pattern = "abc$";
        String replacement = "d";
        String expected = "abcdd";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertDoesNotThrow(() -> {
            assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
        });
    }

    @Test
    void testReplaceStarCharater() {
        String original = "abbbbbcd";
        String pattern = "ab*";
        String replacement = "c";
        String expected = "ccd";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertDoesNotThrow(() -> {
            assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
        });
    }

    @Test
    void testReplacePlusCharater() {
        String original = "abbcd";
        String pattern = "ab+";
        String replacement = "c";
        String expected = "ccd";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertDoesNotThrow(() -> {
            assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
        });
    }

    @Test
    void testReplaceQuestionCharater() {
        String original = "abbcd";
        String pattern = "ac?";
        String replacement = "c";
        String expected = "cbbcd";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertDoesNotThrow(() -> {
            assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
        });
    }

    @Test
    void testReplaceSpecialCharater() {
        String original = "ab?cd";
        String pattern = "\\?";
        String replacement = "*";
        String expected = "ab*cd";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertDoesNotThrow(() -> {
            assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
        });
    }

    @Test
    void testReplaceBraceCharater() {
        String original = "abbbbcd";
        String pattern = "ab{2}";
        String replacement = "c";
        String expected = "cbbcd";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertDoesNotThrow(() -> {
            assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
        });
    }

    @Test
    void testReplaceBracketCharater() {
        String original = "abcde";
        String pattern = "[b-d]+";
        String replacement = "a";
        String expected = "aae";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertDoesNotThrow(() -> {
            assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
        });
    }

    @Test
    void testReplaceParenthesesCharater() {
        String original = "abcabcde";
        String pattern = "(abc)+";
        String replacement = "a";
        String expected = "ade";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertDoesNotThrow(() -> {
            assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
        });
    }

    @Test
    void testReplaceBarCharater() {
        String original = "abcde";
        String pattern = "d|f";
        String replacement = "a";
        String expected = "abcae";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertDoesNotThrow(() -> {
            assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
        });
    }

    @Test
    void testRunWithoutSpecifiedFile() {
        String[] args = {"s/a/b/"};
        String original = "abcd";
        String expectResult = "bbcd" + STRING_NEWLINE;
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            app.run(args, stdin, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }

    @Test
    void testRunWithFiles() {
        String[] args = {"s/A/a/", folderName + CHAR_FILE_SEP + fileNameA};
        String expectResult = "a" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            app.run(args, System.in, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }

    @Test
    void testRunEmptyArgs() {
        String[] args = {};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(Exception.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), sedPrefix + ERR_NO_REP_RULE);
    }

    @Test
    void testRunWithTooShortReplacementRule() {
        String[] args = {"s//"};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(Exception.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), sedPrefix + ERR_INVALID_REP_RULE);
    }

    @Test
    void testRunWithReplacementRuleInWrongFormat() {
        String[] args = {"abcde"};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(Exception.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), sedPrefix + ERR_INVALID_REP_RULE);
    }

    @Test
    void testRunWithInvalidReplacementIndex() {
        String[] args = {"s/a/b/0"};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(Exception.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), sedPrefix + ERR_INVALID_REP_X);
    }

    @Test
    void testRunWithEmptyRegularExpression() {
        String[] args = {"s//b/"};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(Exception.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), sedPrefix + ERR_EMPTY_REGEX);
    }

    @Test
    void testRunWithInvalidRegularExpression() {
        String[] args = {"s/{]/b/"};
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(Exception.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), sedPrefix + ERR_INVALID_REGEX);
    }

    @Test
    void testReplaceInNullIStream() {
        Throwable thrown = assertThrows(Exception.class, () -> {
            app.replaceSubstringInStdin("", "", 1, null);
        });
        assertEquals(thrown.getMessage(), ERR_NULL_STREAMS);
    }

    @Test
    void testReplaceInNotExistFileName() {
        Throwable thrown = assertThrows(Exception.class, () -> {
            app.replaceSubstringInFile("", "", 1, folderName + CHAR_FILE_SEP + fileNameNotExist);
        });
        assertEquals(thrown.getMessage(), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testReplaceInNullFileName() {
        Throwable thrown = assertThrows(Exception.class, () -> {
            app.replaceSubstringInFile("", "", 1, null);
        });
        assertEquals(thrown.getMessage(), ERR_NULL_ARGS);
    }

    @Test
    void testReplaceInDirectory() {
        Throwable thrown = assertThrows(Exception.class, () -> {
            app.replaceSubstringInFile("", "", 1, folderName + CHAR_FILE_SEP + subDirName);
        });
        assertEquals(thrown.getMessage(), ERR_IS_DIR);
    }

    @Test
    void testRunWithNullArg() {
        outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(SedException.class, () -> {
            app.run(null, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), sedPrefix + ERR_NULL_ARGS);
    }

    @Test
    void testRunWithNullOStream() {
        String[] args = {""};
        Throwable thrown = assertThrows(SedException.class, () -> {
            app.run(args, System.in, outputStream);
        });
        assertEquals(thrown.getMessage(), sedPrefix + ERR_NULL_STREAMS);
    }
}